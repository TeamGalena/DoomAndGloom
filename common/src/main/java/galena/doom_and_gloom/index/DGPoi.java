package galena.doom_and_gloom.index;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;

import java.util.HashSet;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class DGPoi {

    public static final ResourceKey<PoiType> GRAVETENDER_POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE,
            modLoc("gravetender"));

    public static final Supplier<PoiType> GRAVETENDER_POI = RegHelper.registerPOI(modLoc("gravetender"),
            () -> new PoiType(new HashSet<>(DGBlocks.SEPULCHER.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    public static void init() {
        // Loads this class
    }

}
