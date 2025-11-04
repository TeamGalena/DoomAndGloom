package galena.doom_and_gloom.forge.compat;

import galena.doom_and_gloom.index.DGItemTiers;
import galena.oreganized.index.OItemTiers;

public class OreganizedCompat {

    public static void init() {
        DGItemTiers.replaceHammerTier(OItemTiers.LEAD);
    }

}
