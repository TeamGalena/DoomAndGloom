package galena.doom_and_gloom.fabric.mixin;

import galena.doom_and_gloom.content.entity.ISepulcherable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ISepulcherable {

    @Unique
    private boolean dg$sepulchered = false;

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    public void DG$cancelDrops(DamageSource damageSource, CallbackInfo ci) {
        //TODO: aa help
    }

    @Override
    public void DG$setSepulchered(boolean sepulchered) {
        this.dg$sepulchered = sepulchered;
    }

    @Override
    public boolean DG$wasSepulchered() {
        return dg$sepulchered;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void dg$readSepulchered(CompoundTag compoundTag, CallbackInfo ci) {

        if (compoundTag.contains(ISepulcherable.DG_TAG_KEY))
            dg$sepulchered = compoundTag.getBoolean(ISepulcherable.DG_TAG_KEY);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void dg$addSepulchered(CompoundTag compoundTag, CallbackInfo ci) {

        if (dg$sepulchered) compoundTag.putBoolean(ISepulcherable.DG_TAG_KEY, dg$sepulchered);
    }
}
