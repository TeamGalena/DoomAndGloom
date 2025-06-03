package galena.doom_and_gloom.data;

import static galena.doom_and_gloom.index.OTags.Items.TOOLS_BUSH_HAMMER;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.index.OItems;
import galena.doom_and_gloom.index.OTags;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class OItemTags extends ItemTagsProvider {

    public OItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> provider, @Nullable ExistingFileHelper helper) {
        super(output, future, provider, DoomAndGloom.MOD_ID, helper);
    }

    @Override
    public String getName() {
        return "Oreganized Item Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TOOLS_BUSH_HAMMER).add(OItems.BUSH_HAMMER.get());

        tag(ItemTags.MUSIC_DISCS).add(OItems.MUSIC_DISC_AFTERLIFE.get());

        copy(OTags.Blocks.VIGIL_CANDLES, OTags.Items.VIGIL_CANDLES);
    }
}
