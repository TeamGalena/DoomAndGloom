package galena.doom_and_gloom.compat;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;

public class CompatMods {

    public static final String DYE_DEPOT_NAME = "dye_depot";
    public static final String AMENDMENTS_NAME = "moonlight";
    public static final boolean DYE_DEPOT = PlatHelper.isModLoaded(DYE_DEPOT_NAME);
    public static final boolean AMENDMENTS = PlatHelper.isModLoaded(AMENDMENTS_NAME);
}
