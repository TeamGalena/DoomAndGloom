package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.index.DGBlocks;
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
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class OBlockTags extends IntrinsicHolderTagsProvider<Block> {

    public OBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper helper) {
        super(output, Registries.BLOCK, future, block -> block.builtInRegistryHolder().key(), DoomAndGloom.MOD_ID, helper);
    }

    @Override
    public @NotNull String getName() {
        return "Oreganized Block Tags";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(HEAT_SOURCE).addTag(BlockTags.FIRE).addTag(BlockTags.CAMPFIRES);

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                DGBlocks.SEPULCHER.get(),
                DGBlocks.BONE_PILE.get()
        );

        var vigilCandles = tag(VIGIL_CANDLES);

        DGBlocks.vigilCandles().forEach(block -> {
            var id = BuiltInRegistries.BLOCK.getKey(block.get());
            vigilCandles.addOptional(id);
        });

        tag(BlockTags.CANDLES).addTags(VIGIL_CANDLES);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).addTags(VIGIL_CANDLES);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DGBlocks.SEPULCHER.get());

        tag(CAN_TURN_INTO_BURIAL_DIRT).add(
                Blocks.DIRT,
                Blocks.GRASS_BLOCK,
                Blocks.PODZOL,
                Blocks.MYCELIUM,
                Blocks.COARSE_DIRT,
                Blocks.ROOTED_DIRT
        );

        tag(GRAVETENDER_LIGHTABLE)
                .addTag(BlockTags.CANDLES)
                .addOptionalTag(new ResourceLocation("amendments:skull_candles"));

        tag(BlockTags.DIRT).add(DGBlocks.BURIAL_DIRT.get());
    }
}
