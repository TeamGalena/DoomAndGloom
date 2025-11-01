package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.data.provider.OBlockStateProvider;
import galena.doom_and_gloom.index.DGBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class OBlockStates extends OBlockStateProvider {

    public OBlockStates(PackOutput output, ExistingFileHelper helper) {
        super(output, helper);
    }

    @Override
    public String getName() {
        return DoomAndGloom.MOD_ID + " Block States";
    }

    @Override
    protected void registerStatesAndModels() {
        sepulcherBlock(DGBlocks.SEPULCHER);
        simpleBlock(DGBlocks.BONE_PILE.get(), models().cubeColumn(blockTexture(DGBlocks.BONE_PILE.get()).getPath(), DoomAndGloom.modLoc("block/bone_pile_side"), DoomAndGloom.modLoc("block/sepulcher_rot_5")));
        simpleBlock(DGBlocks.ROTTING_FLESH.get(), models().cubeAll(blockTexture(DGBlocks.ROTTING_FLESH.get()).getPath(), DoomAndGloom.modLoc("block/sepulcher_rot_1")));
        vigilCandle(DGBlocks.VIGIL_CANDLE, null);
        simpleBlock(DGBlocks.BURIAL_DIRT.get(), models().cubeTop("burial_dirt", new ResourceLocation("block/dirt"), DoomAndGloom.modLoc("block/burial_dirt")));
        DGBlocks.COLORED_VIGIL_CANDLES.forEach((color, block) -> vigilCandle(block, color.getSerializedName()));
        stoneTablet(DGBlocks.STONE_TABLET, null);
        stoneTablet(DGBlocks.ENGRAVED_STONE_TABLET, "engraved");
        stoneTablet(DGBlocks.CRACKED_STONE_TABLET, "cracked");
    }

}
