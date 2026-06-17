package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGTags;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class DGBlockTags extends IntrinsicHolderTagsProvider<Block> {

    public DGBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper helper) {
        super(output, Registries.BLOCK, future, block -> block.builtInRegistryHolder().key(), DoomAndGloom.MOD_ID, helper);
    }

    @Override
    public @NotNull String getName() {
        return "Oreganized Block Tags";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DGTags.Blocks.HEAT_SOURCE).addTag(BlockTags.FIRE).addTag(BlockTags.CAMPFIRES);

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                DGBlocks.SEPULCHER.get(),
                DGBlocks.BONE_PILE.get()
        );

        var vigilCandles = tag(DGTags.Blocks.VIGIL_CANDLES);

        tag(DGTags.Blocks.STORAGE_BLOCKS_BONE).add(DGBlocks.BONE_PILE.get());
        tag(Tags.Blocks.STORAGE_BLOCKS).addTag(DGTags.Blocks.STORAGE_BLOCKS_BONE);

        DGBlocks.vigilCandles().forEach(block -> {
            var id = BuiltInRegistries.BLOCK.getKey(block.get());
            vigilCandles.addOptional(id);
        });

        tag(BlockTags.CANDLES).addTags(DGTags.Blocks.VIGIL_CANDLES);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).addTags(DGTags.Blocks.VIGIL_CANDLES);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DGBlocks.SEPULCHER.get());

        tag(DGTags.Blocks.CAN_TURN_INTO_BURIAL_DIRT).add(
                Blocks.DIRT,
                Blocks.GRASS_BLOCK,
                Blocks.PODZOL,
                Blocks.MYCELIUM,
                Blocks.COARSE_DIRT,
                Blocks.ROOTED_DIRT
        );

        tag(DGTags.Blocks.GRAVETENDER_LIGHTABLE)
                .addTag(BlockTags.CANDLES)
                .addOptionalTag(ResourceLocation.fromNamespaceAndPath("amendments", "skull_candles"));

        tag(BlockTags.DIRT).add(DGBlocks.BURIAL_DIRT.get());

        DGBlocks.COLORED_VIGIL_CANDLES.forEach((dye, block) ->
                tag(DGTags.Blocks.DYED.get(dye)).addOptional(block.getId())
        );
    }
}
