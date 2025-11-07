package galena.doom_and_gloom.content.item;

import galena.doom_and_gloom.index.DGTags;
import net.minecraft.world.item.*;

public class BushHammerItem extends DiggerItem {

    public BushHammerItem(Tier tier, float attack, float modifier, Item.Properties properties) {
        super(attack, modifier, tier, DGTags.Blocks.MINEABLE_WITH_BUSH_HAMMER, properties);
    }

}
