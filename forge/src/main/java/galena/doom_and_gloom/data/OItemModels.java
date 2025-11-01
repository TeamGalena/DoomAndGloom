package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.data.provider.OItemModelProvider;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class OItemModels extends OItemModelProvider {

    public OItemModels(PackOutput output, ExistingFileHelper helper) {
        super(output, helper);
    }

    @Override
    public String getName() {
        return DoomAndGloom.MOD_ID + " Item Models";
    }

    @Override
    protected void registerModels() {
        toolItem(DGItems.BUSH_HAMMER);
        block(DGBlocks.SEPULCHER);
        block(DGBlocks.BONE_PILE);
        block(DGBlocks.BURIAL_DIRT);
        DGBlocks.vigilCandles().forEach(this::normalItem);
        spawnEggItem(DGItems.HOLLER_SPAWN_EGG);
        normalItem(DGItems.MUSIC_DISC_AFTERLIFE);
        normalItem(DGBlocks.STONE_TABLET);
        normalItem(DGBlocks.ENGRAVED_STONE_TABLET);
        normalItem(DGBlocks.CRACKED_STONE_TABLET);
        normalItem(DGItems.HAMMER_AND_CHISEL);
    }

}
