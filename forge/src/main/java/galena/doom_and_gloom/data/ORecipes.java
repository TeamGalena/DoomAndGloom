package galena.doom_and_gloom.data;

import com.possible_triangle.multikulti.datagen.conditions.Conditional;
import com.possible_triangle.multikulti.datagen.conditions.ModLoaded;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.compat.CompatMods;
import galena.doom_and_gloom.compat.DyeColors;
import galena.doom_and_gloom.data.provider.ORecipeProvider;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGTags;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import vectorwing.farmersdelight.common.crafting.ingredient.ToolActionIngredient;
import vectorwing.farmersdelight.data.builder.CuttingBoardRecipeBuilder;

public class ORecipes extends ORecipeProvider {

    public ORecipes(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        vigilCandle(DGBlocks.VIGIL_CANDLE, Blocks.CANDLE).save(consumer);

        DGBlocks.COLORED_VIGIL_CANDLES.forEach((color, block) -> {
            var namespace = DyeColors.modNamespace(color);
            var conditions = namespace.map(ModLoaded::new).map(List::of).orElseGet(List::of);

            Conditional.with(this, conditions, () -> {
                var candle = BuiltInRegistries.BLOCK.get(new ResourceLocation(namespace.orElse(ResourceLocation.DEFAULT_NAMESPACE), color.getSerializedName() + "_candle"));
                vigilCandle(block, candle).save(consumer);

                ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, block.get())
                        .requires(DGBlocks.VIGIL_CANDLE.get())
                        .requires(DyeItem.byColor(color))
                        .unlockedBy("has_vigil_candle", has(DGBlocks.VIGIL_CANDLE.get()))
                        .group("vigil_candle")
                        .save(consumer, RecipeBuilder.getDefaultRecipeId(block.get()).withSuffix("_dyeing"));
            });
        });

        withFallback(DGTags.Items.INGOTS_SILVER, Tags.Items.INGOTS_IRON, ingot ->
                ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DGBlocks.SEPULCHER.get())
                        .pattern("# #")
                        .pattern("# #")
                        .pattern("###")
                        .define('#', ingot)
        ).accept(consumer);

        compact(DGBlocks.BONE_PILE.get().asItem(), Items.BONE).save(consumer);
        unCompact(Items.BONE, DGBlocks.BONE_PILE.get().asItem()).save(consumer);

        Conditional.with(this, List.of(new ModLoaded(CompatMods.FARMERS_DELIGHT_NAME)), () -> {
            CuttingBoardRecipeBuilder.cuttingRecipe(
                    Ingredient.of(DGBlocks.STONE_TABLET.get()),
                    new ToolActionIngredient(ToolActions.PICKAXE_DIG),
                    DGBlocks.CRACKED_STONE_TABLET.get()
            ).build(consumer, DoomAndGloom.modLoc("cutting/stone_tablet"));
        });

        /*
        withFallback(OTags.Items.INGOTS_LEAD, Tags.Items.INGOTS_COPPER, ingot ->
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, OItems.BUSH_HAMMER.get())
                        .pattern("AA")
                        .pattern("B ")
                        .define('A', ingot)
                        .define('B', Tags.Items.RODS_WOODEN)
                        .unlockedBy("has_stick", has(Tags.Items.RODS_WOODEN))
        ).accept(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, OBlocks.STONE_TABLET.get())
                .pattern("##")
                .pattern("##")
                .define('#', Blocks.STONE_PRESSURE_PLATE)
                .unlockedBy("has_stone", has(Blocks.STONE_PRESSURE_PLATE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, OItems.HAMMER_AND_CHISEL.get())
                .pattern("## ")
                .pattern("##H")
                .define('#', Blocks.STONE_PRESSURE_PLATE)
                .define('H', OItems.BUSH_HAMMER.get())
                .unlockedBy("has_hammer", has(OItems.BUSH_HAMMER.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, OItems.HAMMER_AND_CHISEL.get())
                .requires(OItems.BUSH_HAMMER.get())
                .requires(OBlocks.STONE_TABLET.get())
                .unlockedBy("has_hammer", has(OItems.BUSH_HAMMER.get()))
                .save(consumer, new ResourceLocation(DoomAndGloom.MOD_ID, "hammer_and_chisel_from_tablet"));

        smeltingResultFromBase(consumer, OBlocks.CRACKED_STONE_TABLET.get(), OBlocks.STONE_TABLET.get());
        */
    }

    private Consumer<Consumer<FinishedRecipe>> withFallback(TagKey<Item> prefer, TagKey<Item> fallback, Function<TagKey<Item>, RecipeBuilder> builder) {
        return consumer -> {
            var preferredRecipe = builder.apply(prefer).unlockedBy("has_ingredient", has(prefer));
            var id = RecipeBuilder.getDefaultRecipeId(preferredRecipe.getResult());

            ConditionalRecipe.builder()
                    .addCondition(new NotCondition(new TagEmptyCondition(prefer.location())))
                    .addRecipe(preferredRecipe::save)
                    .generateAdvancement()
                    .build(consumer, id);

            ConditionalRecipe.builder()
                    .addCondition(new TagEmptyCondition(prefer.location()))
                    .addRecipe(builder.apply(fallback).unlockedBy("has_ingredient", has(fallback))::save)
                    .generateAdvancement()
                    .build(consumer, id.withSuffix("_fallback"));

        };
    }

}
