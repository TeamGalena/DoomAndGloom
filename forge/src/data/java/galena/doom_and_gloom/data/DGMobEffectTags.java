package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.index.DGTags;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class DGMobEffectTags extends TagsProvider<MobEffect> {

    public DGMobEffectTags(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper helper) {
        super(output, Registries.MOB_EFFECT, future, DoomAndGloom.MOD_ID, helper);
    }

    @Override
    public @NotNull String getName() {
        return "Oreganized MobEffect Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DGTags.Effects.VIGIL_CANDLE_CLEARS).add(ResourceKey.create(Registries.MOB_EFFECT, DoomAndGloom.modLoc("fog")));
    }
}
