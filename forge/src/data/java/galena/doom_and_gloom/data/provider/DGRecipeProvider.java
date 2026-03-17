package galena.doom_and_gloom.data.provider;

import galena.doom_and_gloom.index.DGTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public abstract class DGRecipeProvider extends RecipeProvider {

    public DGRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    public ShapedRecipeBuilder compact(Item itemOut, Item itemIn) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, itemOut)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', itemIn)
                .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(itemIn).getPath(), has(itemIn));
    }

    public ShapelessRecipeBuilder unCompact(Item itemOut, Item itemIn) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, itemOut, 9)
                .requires(itemIn)
                .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(itemIn).getPath(), has(itemIn));
    }

    public ShapedRecipeBuilder vigilCandle(Supplier<? extends Block> block, ItemLike candle) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block.get())
                .define('S', DGTags.Items.INGOTS_SILVER)
                .define('C', candle)
                .pattern("S")
                .unlockedBy("has_silver", has(DGTags.Items.INGOTS_SILVER))
                .group("vigil_candle")
                .pattern("C");
    }

}
