package galena.doom_and_gloom.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import galena.doom_and_gloom.index.DGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow private Level level;

    @Shadow public abstract BlockPos getBlockPosBelowThatAffectsMyMovement();

    @Shadow public abstract Vec3 getDeltaMovement();

    @ModifyExpressionValue(
            method = "canSpawnSprintParticle()Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isSprinting()Z")
    )
    public boolean isSprinting(boolean original) {
        return original || (
                this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).is(DGBlocks.BONE_PILE.get())
                    && (this.getDeltaMovement().x != 0.0 || this.getDeltaMovement().z != 0.0)
        );
    }

}
