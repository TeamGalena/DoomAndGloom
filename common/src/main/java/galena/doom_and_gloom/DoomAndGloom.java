package galena.doom_and_gloom;

import galena.doom_and_gloom.compat.CompatMods;
import galena.doom_and_gloom.compat.moonlight.MoonlightCompat;
import galena.doom_and_gloom.index.DGBlockEntities;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGEffects;
import galena.doom_and_gloom.index.DGEntityTypes;
import galena.doom_and_gloom.index.DGItemListings;
import galena.doom_and_gloom.index.DGItems;
import galena.doom_and_gloom.index.DGParticleTypes;
import galena.doom_and_gloom.index.DGPoi;
import galena.doom_and_gloom.index.DGSoundEvents;
import galena.doom_and_gloom.index.DGVillagerTypes;
import galena.doom_and_gloom.network.DGNetwork;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoomAndGloom {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "doom_and_gloom";

    public static ResourceLocation modLoc(String location) {
        return new ResourceLocation(MOD_ID, location);
    }

    public static void init() {
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

        DGNetwork.register();

        if (PlatHelper.isModLoaded(CompatMods.MOONLIGHT)) {
            MoonlightCompat.init();
        }
    }

}
