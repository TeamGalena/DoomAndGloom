package galena.doom_and_gloom.index;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;

import java.util.HashSet;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class DGPoi {

    public static final RegSupplier<PoiType> GRAVETENDER_POI = RegHelper.registerPOI(modLoc("gravetender"),
            () -> new PoiType(new HashSet<>(DGBlocks.SEPULCHER.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    public static void init() {
        // Loads this class
    }

}
