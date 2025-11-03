package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.data.provider.DGLangProvider;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGEffects;
import galena.doom_and_gloom.index.DGItems;
import net.minecraft.data.PackOutput;

public class DGLang extends DGLangProvider {

    public DGLang(PackOutput output) {
        super(output, DoomAndGloom.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addDisc(DGItems.MUSIC_DISC_AFTERLIFE, "Firch", "afterlife");

        addBlock(DGBlocks.BONE_PILE, "Pile of Bones");

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
        addSubtitle("entity", "holler_hollers", "Holler wails"); //howls? howls? whispers?

        add("entity.minecraft.villager." + DoomAndGloom.MOD_ID + ".gravetender", "Gravetender");

        add("gui.doom_and_gloom.stone_tablet.engrave", "Engrave");
        add("gui.doom_and_gloom.stone_tablet.cancel", "Cancel");

        /*
            Automatically create translations for blocks and items based on their registry name.

            This must be at the very bottom to avoid overwriting errors. These functions ignore objects
            that have already been translated above.
         */

        // TODO do this differently
        /*
        for (Supplier<? extends Block> blocks : DoomAndGloom.REGISTRY_HELPER.getBlockSubHelper().getDeferredRegister().getEntries()) {
            tryBlock(blocks);
        }
        for (Supplier<? extends Item> items : DoomAndGloom.REGISTRY_HELPER.getItemSubHelper().getDeferredRegister().getEntries()) {
            tryItem(items);
        }
        for (Supplier<? extends EntityType<?>> entities : DGEntityTypes.ENTITIES.getEntries()) {
            tryEntity(entities);
        }
         */
    }
}
