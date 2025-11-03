package galena.doom_and_gloom.data;

import static galena.doom_and_gloom.index.DGTags.Items.TOOLS_BUSH_HAMMER;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.index.DGItems;
import galena.doom_and_gloom.index.DGTags;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DGItemTags extends ItemTagsProvider {

    public DGItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> provider, @Nullable ExistingFileHelper helper) {
        super(output, future, provider, DoomAndGloom.MOD_ID, helper);
    }

    @Override
    public String getName() {
        return "Oreganized Item Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TOOLS_BUSH_HAMMER).add(DGItems.BUSH_HAMMER.get());

        tag(ItemTags.MUSIC_DISCS).add(DGItems.MUSIC_DISC_AFTERLIFE.get());

        copy(DGTags.Blocks.VIGIL_CANDLES, DGTags.Items.VIGIL_CANDLES);
    }
}
