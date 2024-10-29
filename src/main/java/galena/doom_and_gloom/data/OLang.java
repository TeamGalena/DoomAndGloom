package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.data.provider.OLangProvider;
import galena.doom_and_gloom.index.OBlocks;
import galena.doom_and_gloom.index.OEffects;
import galena.doom_and_gloom.index.OEntityTypes;
import galena.doom_and_gloom.index.OItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class OLang extends OLangProvider {

    public OLang(PackOutput output) {
        super(output, DoomAndGloom.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addDisc(OItems.MUSIC_DISC_AFTERLIFE, "Firch", "afterlife");

        addBlock(OBlocks.BONE_PILE, "Pile of Bones");

        addEffect(OEffects.FOG, "Fog");

        // JEED compat
        add("effect.doom_and_gloom.fog.description", "An eerie fog that accompanies the holler");

        addSubtitle("block", "sepulcher.unsealing", "Sepulcher opened");
        addSubtitle("block", "sepulcher.sealing", "Sepulcher sealed");
        addSubtitle("block", "sepulcher.rotting", "Sepulcher rotting");
        addSubtitle("block", "sepulcher.harvest", "Sepulcher emptied");
        addSubtitle("block", "sepulcher.filled", "Sepulcher filled");
        addSubtitle("block", "sepulcher.corpse_stuffed", "Sepulcher consumes corpse");

        addSubtitle("entity", "holler_death", "Holler dies");
        addSubtitle("entity", "holler_hurt", "Holler hurts");
        addSubtitle("entity", "holler_shrieks", "Holler shrieks");
        addSubtitle("entity", "holler_hollers", "Holler hollers");

        /*
            Automatically create translations for blocks and items based on their registry name.

            This must be at the very bottom to avoid overwriting errors. These functions ignore objects
            that have already been translated above.
         */
        for (Supplier<? extends Block> blocks : DoomAndGloom.REGISTRY_HELPER.getBlockSubHelper().getDeferredRegister().getEntries()) {
            tryBlock(blocks);
        }
        for (Supplier<? extends Item> items : DoomAndGloom.REGISTRY_HELPER.getItemSubHelper().getDeferredRegister().getEntries()) {
            tryItem(items);
        }
        for (Supplier<? extends EntityType<?>> entities : OEntityTypes.ENTITIES.getEntries()) {
            tryEntity(entities);
        }
    }
}
