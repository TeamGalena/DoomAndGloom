package galena.doom_and_gloom;

import galena.doom_and_gloom.gen.VillageStructureModifier;
import galena.doom_and_gloom.index.*;
import galena.doom_and_gloom.network.DGNetwork;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoomAndGloom {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "doom_and_gloom";

    public static ResourceLocation modLoc(String location) {
        return new ResourceLocation(MOD_ID, location);
    }

    public static void init() {

        DGConfig.init();
        DGBlockEntities.init();
        DGBlocks.init();
        DGEffects.init();
        DGEntityTypes.init();
        DGItems.init();
        DGParticleTypes.init();
        DGPoi.init();
        DGSoundEvents.init();
        DGVillagerTypes.init();
        DGItemListings.init();
        DGLootInjects.init();

        DGNetwork.register();

        RegHelper.addItemsToTabsRegistration(DGItemTabContents::addItemsToTabs);
    }


    public static void onServerAboutToStart(MinecraftServer server) {
        VillageStructureModifier.setup(server.registryAccess());
    }

}
