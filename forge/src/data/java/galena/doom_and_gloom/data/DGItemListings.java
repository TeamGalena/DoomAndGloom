package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.data.provider.ItemListingProvider;
import galena.doom_and_gloom.index.DGTags;
import galena.doom_and_gloom.index.DGVillagerTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class DGItemListings extends ItemListingProvider {

    public DGItemListings(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, fileHelper, DoomAndGloom.MOD_ID, lookup);
    }

    @Override
    protected void gather() {
        // TODO port replace when moonlight updates
        builder(DGVillagerTypes.GRAVETENDER.unwrapKey().get(), 1)
                .sell("stone_shovel", 1, new ItemStack(Items.STONE_SHOVEL), 12, 1)
                .sell("rose_bush", 1, new ItemStack(Items.ROSE_BUSH), 8, 2)
                .buy("poppy", 1, new ItemCost(Items.POPPY, 8), 12, 1)
                .buy("cornflower", 1, new ItemCost(Items.CORNFLOWER, 8), 12, 1);

        builder(DGVillagerTypes.GRAVETENDER.unwrapKey().get(), 2)
                .sell("gold_nugget", 1, new ItemStack(Items.GOLD_NUGGET), 8, 5)
                .sell("vigil_candles", 2, DGTags.Items.VIGIL_CANDLES, 1, 12, 10)
                .sell("poppy", 1, new ItemStack(Items.FLOWER_POT, 8), 12, 5);

        builder(DGVillagerTypes.GRAVETENDER.unwrapKey().get(), 3)
                .sellEnchanted("iron_shovel", 1, new ItemStack(Items.IRON_SHOVEL), 3, 10)
                .buy("candles", 2, ItemTags.CANDLES, 6, 12, 2);

        builder(DGVillagerTypes.GRAVETENDER.unwrapKey().get(), 4)
                .buy("lantern", 2, new ItemCost(Items.LANTERN), 8, 15)
                .sell("wither_rose", 10, new ItemStack(Items.WITHER_ROSE, 2), 20, 8);

        builder(DGVillagerTypes.GRAVETENDER.unwrapKey().get(), 5)
                .sellEnchanted("diamond_shovel", 13, new ItemStack(Items.DIAMOND_SHOVEL), 3, 30);
    }

}
