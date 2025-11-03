package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class DGDamageTags extends TagsProvider<DamageType> {

    public DGDamageTags(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper helper) {
        super(output, Registries.DAMAGE_TYPE, future, DoomAndGloom.MOD_ID, helper);
    }

    @Override
    public @NotNull String getName() {
        return "Oreganized DamageType Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
      // tag(DamageTypeTags.BYPASSES_ARMOR).add(ODamageSources.LEAD_BOLT);
    }
}
