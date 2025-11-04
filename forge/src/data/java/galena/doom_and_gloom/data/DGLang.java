package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.data.provider.DGLangProvider;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGEffects;
import galena.doom_and_gloom.index.DGEntityTypes;
import galena.doom_and_gloom.index.DGItems;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.minecraft.data.PackOutput;

public class DGLang extends DGLangProvider {

    public DGLang(PackOutput output) {
        super(output, DoomAndGloom.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addDisc(DGItems.MUSIC_DISC_AFTERLIFE, "Firch", "afterlife");

        addBlock(DGBlocks.BONE_PILE, "Pile of Bones");
        add(DGBlocks.SEPULCHER.getHolder());
        add(DGBlocks.ROTTING_FLESH.getHolder());
        add(DGBlocks.STONE_TABLET.getHolder());
        add(DGBlocks.ENGRAVED_STONE_TABLET.getHolder());
        add(DGBlocks.CRACKED_STONE_TABLET.getHolder());
        DGBlocks.vigilCandles().map(RegSupplier::getHolder).forEach(this::add);
        add(DGBlocks.BURIAL_DIRT.getHolder());

        add(DGItems.BUSH_HAMMER.getHolder());
        add(DGItems.HOLLER_SPAWN_EGG.getHolder());
        add(DGItems.HAMMER_AND_CHISEL.getHolder());

        add(DGEntityTypes.DIRT_MOUND.getHolder());
        add(DGEntityTypes.HOLLER.getHolder());

        addEffect(DGEffects.FOG, "Fog");
        addEffect(DGEffects.WARDING, "Warding");

        // JEED compat
        add("effect.doom_and_gloom.fog.description", "An eerie fog that accompanies the holler");
        add("effect.doom_and_gloom.warding.description", "Wards against evil spirits");

        addSubtitle("block", "sepulcher.unsealing", "Sepulcher opened");
        addSubtitle("block", "sepulcher.sealing", "Sepulcher sealed");
        addSubtitle("block", "sepulcher.rotting", "Sepulcher rotting");
        addSubtitle("block", "sepulcher.harvest", "Sepulcher emptied");
        addSubtitle("block", "sepulcher.filled", "Sepulcher filled");
        addSubtitle("block", "sepulcher.corpse_stuffed", "Sepulcher consumes corpse");

        addSubtitle("entity", "holler_death", "Holler dies");
        addSubtitle("entity", "holler_hurt", "Holler hurts");
        addSubtitle("entity", "holler_shrieks", "Holler shrieks");
        addSubtitle("entity", "holler_hollers", "Holler wails");

        add("entity.minecraft.villager." + DoomAndGloom.MOD_ID + ".gravetender", "Gravetender");

        add("gui.doom_and_gloom.stone_tablet.engrave", "Engrave");
        add("gui.doom_and_gloom.stone_tablet.cancel", "Cancel");
    }

}
