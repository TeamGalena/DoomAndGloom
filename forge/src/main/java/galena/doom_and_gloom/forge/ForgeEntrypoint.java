package galena.doom_and_gloom.forge;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.DoomAndGloomClient;
import galena.doom_and_gloom.compat.AmendmentsCompat;
import galena.doom_and_gloom.compat.CompatMods;
import galena.doom_and_gloom.content.entity.ISepulcherable;
import galena.doom_and_gloom.forge.compat.OreganizedCompat;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod(DoomAndGloom.MOD_ID)
public class ForgeEntrypoint {

    public ForgeEntrypoint() {
        DoomAndGloom.init();

        if (PlatHelper.getPhysicalSide().isClient()) {
            DoomAndGloomClient.init();
        }

        IEventBus forgeBus = NeoForge.EVENT_BUS;

        if (CompatMods.AMENDMENTS) {
            forgeBus.addListener(this::onBlockInteract);
        }

        if (CompatMods.OREGANIZED) {
            OreganizedCompat.init();
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
        if (event.getEntity() instanceof Player) return;
        if (ISepulcherable.cast(event.getEntity()).DG$wasSepulchered()) {
            event.setCanceled(true);
        }
    }
}
