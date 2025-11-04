package galena.doom_and_gloom;

import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGItems;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class DGItemTabContents {

    public static void addItemsToTabs(RegHelper.ItemToTabEvent event) {
        var vigilCandles = DGBlocks.vigilCandles().map(RegSupplier::get).toArray(ItemLike[]::new);

        event.addAfter(CreativeModeTabs.FUNCTIONAL_BLOCKS, stack -> stack.is(Items.SOUL_LANTERN), vigilCandles);
        event.addAfter(CreativeModeTabs.COLORED_BLOCKS, stack -> stack.is(ItemTags.CANDLES), vigilCandles);

        event.add(CreativeModeTabs.BUILDING_BLOCKS, DGBlocks.BONE_PILE.get());
        event.addAfter(CreativeModeTabs.FUNCTIONAL_BLOCKS, stack -> stack.is(Items.COMPOSTER), DGBlocks.SEPULCHER.get());
        event.addAfter(CreativeModeTabs.FUNCTIONAL_BLOCKS, stack -> stack.is(Items.INFESTED_DEEPSLATE), DGBlocks.BURIAL_DIRT.get());

        event.add(CreativeModeTabs.SPAWN_EGGS, DGItems.HOLLER_SPAWN_EGG.get());

        event.addAfter(CreativeModeTabs.TOOLS_AND_UTILITIES, stack -> stack.is(ItemTags.MUSIC_DISCS), DGItems.MUSIC_DISC_AFTERLIFE.get());

        //if (tab == CreativeModeTabs.TOOLS_AND_UTILITIES) {
        //    event.accept(new ItemStack(OItems.BUSH_HAMMER.get()));
        //    event.accept(new ItemStack(OItems.HAMMER_AND_CHISEL.get()));
        //}

        //if (tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
        //    event.accept(new ItemStack(OBlocks.STONE_TABLET.get()));
        //    event.accept(new ItemStack(OBlocks.CRACKED_STONE_TABLET.get()));
        //}
    }

}
