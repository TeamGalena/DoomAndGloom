package galena.doom_and_gloom.forge;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.compat.AmendmentsCompat;
import galena.doom_and_gloom.compat.CompatMods;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;

@Mod(DoomAndGloom.MOD_ID)
public class ForgeEntrypoint {

    public ForgeEntrypoint() {
        DoomAndGloom.init();

        //if(PlatHelper.getPhysicalSide().isClient()) DoomAndGloomClient.init();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        if (CompatMods.AMENDMENTS) {
            forgeBus.addListener(this::onBlockInteract);
        }

        forgeBus.addListener(this::onServerStart);
        forgeBus.addListener(this::onLivingDrops);
    }



    private void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (AmendmentsCompat.onBlockInteract(event.getLevel(), event.getPos(),
                event.getEntity(),
                event.getHand(),
                event.getItemStack())) {
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            event.setCanceled(true);
        }
    }

    public void onServerStart(ServerAboutToStartEvent event) {
        DoomAndGloom.onServerAboutToStart(event.getServer());
    }

    private void onLivingDrops(LivingDropsEvent event) {
        if (DoomAndGloom.onItemDrop(event.getEntity())) {
            event.setCanceled(true);
        }
    }

}
