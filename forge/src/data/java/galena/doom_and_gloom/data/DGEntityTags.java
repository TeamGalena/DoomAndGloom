package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.index.DGTags;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DGEntityTags extends EntityTypeTagsProvider {

    public DGEntityTags(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper help) {
        super(output, future, DoomAndGloom.MOD_ID, help);
    }

    @Override
    public String getName() {
        return "Oreganized Entity Type Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DGTags.Entities.FILLS_SEPULCHER)
                .add(EntityType.PLAYER)
                .add(EntityType.ZOMBIE)
                .add(EntityType.ZOMBIE_VILLAGER)
                .add(EntityType.VILLAGER);
    }
}
