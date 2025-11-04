package galena.doom_and_gloom.index;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public class DGItemTiers {

    private static Tier HAMMER_TIER = Tiers.IRON;

    public static Tier getHammerTier() {
        return HAMMER_TIER;
    }

    public static void replaceHammerTier(Tier tier) {
        HAMMER_TIER = tier;
    }

}
