package galena.doom_and_gloom.forge;

import com.mojang.serialization.Codec;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.compat.AmendmentsCompat;
import galena.doom_and_gloom.compat.CompatMods;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(DoomAndGloom.MOD_ID)
public class ForgeEntrypoint {

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DoomAndGloom.MOD_ID);

    // TODO can maybe be replaced with moonlights modifier? yeah there is one that does just this
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM = LOOT_MODIFIERS.register("add_item", () -> AddItemLootModifier.CODEC);

    public ForgeEntrypoint() {
        DoomAndGloom.init();

        //if(PlatHelper.getPhysicalSide().isClient()) DoomAndGloomClient.init();

        if (PlatHelper.isModLoaded(CompatMods.AMENDMENTS)) {
            AmendmentsCompat.register();
        }

        var modBus = EventBusSubscriber.Bus.MOD.bus().get();
        var forgeBus = MinecraftForge.EVENT_BUS;

        forgeBus.addListener(this::onServerStart);
        forgeBus.addListener(this::onLivingDrops);

        LOOT_MODIFIERS.register(modBus);
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
