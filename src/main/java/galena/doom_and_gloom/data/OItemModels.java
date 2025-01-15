package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.data.provider.OItemModelProvider;
import galena.doom_and_gloom.index.OBlocks;
import galena.doom_and_gloom.index.OItems;
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
        toolItem(OItems.BUSH_HAMMER);
        block(OBlocks.SEPULCHER);
        block(OBlocks.BONE_PILE);
        block(OBlocks.BURIAL_DIRT);
        OBlocks.vigilCandles().forEach(this::normalItem);
        spawnEggItem(OItems.HOLLER_SPAWN_EGG);
        normalItem(OItems.MUSIC_DISC_AFTERLIFE);
        normalItem(OBlocks.STONE_TABLET);
        normalItem(OBlocks.ENGRAVED_STONE_TABLET);
        normalItem(OBlocks.CRACKED_STONE_TABLET);
        normalItem(OItems.HAMMER_AND_CHISEL);
    }

}
