package galena.doom_and_gloom.client.fog;

import com.mojang.blaze3d.shaders.FogShape;
import galena.doom_and_gloom.index.DGEffects;
import galena.doom_and_gloom.index.DGParticleTypes;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;

public class FogRendering {

    @Nullable
    private static MobEffectInstance fogEffect;

    public static Optional<MobEffectInstance> activeEffect() {
        return Optional.ofNullable(fogEffect);
    }

    public static void clientTick() {
        if (!(Minecraft.getInstance().gameRenderer.getMainCamera().getEntity() instanceof Player player)) return;
        fogEffect = player.getEffect(DGEffects.FOG);

        if (fogEffect == null) return;
        if (Minecraft.getInstance().isPaused()) return;
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        var range = 24;
        var at = player.position().add((level.random.nextDouble() - 0.5) * range, level.random.nextDouble() * 4 - 2, (level.random.nextDouble() - 0.5) * range);
        var blockAt = BlockPos.containing(at.x, at.y, at.z);
        var stateAt = level.getBlockState(blockAt);

        if (!stateAt.canBeReplaced() || !stateAt.getFluidState().isEmpty()) return;

        var below = level.getBlockState(blockAt.below());

        if (below.getFluidState().is(FluidTags.WATER)) {
            addFogGroup(level, DGParticleTypes.FOG_WATER.get(), blockAt, 3, 0);
        } else if (!below.canBeReplaced()) {
            addFogGroup(level, DGParticleTypes.FOG.get(), blockAt, 5, 1);
        }
    }

    private static void addFogGroup(Level level, ParticleOptions type, BlockPos at, int amount, double yRange) {
        if (level.random.nextInt(amount * 2) != 0) return;

        var realAmount = amount - level.random.nextInt(2);

        for (int i = 0; i < realAmount; i++) {
            level.addParticle(type,
                    at.getX() + level.random.nextDouble() * 2 - 1, at.getY() + 0.5 + level.random.nextDouble() * yRange, at.getZ() + level.random.nextDouble() * 2 - 1,
                    level.random.nextFloat() + 0.5F, 0.0, 0.0
            );
        }
    }

    public static float @Nullable [] modifyFogColor(float r, float g, float b, float partialTicks) {
        return activeEffect()
                .map(effect -> {
                    Entity cameraEntity = Minecraft.getInstance().gameRenderer.getMainCamera().getEntity();
                    if (!(cameraEntity instanceof LivingEntity le)) return null;
                    float factor = effect.getBlendFactor(le, partialTicks);
                    // target color components (0x697180)
                    float targetR = 0x85 / 255f;
                    float targetG = 0x90 / 255f;
                    float targetB = 0xA0 / 255f;

                    float red = Mth.lerp(factor, r, targetR);
                    float green = Mth.lerp(factor, g, targetG);
                    float blue = Mth.lerp(factor, b, targetB);

                    return new float[]{red, green, blue};
                }).orElse(null);
    }

    public static float @Nullable [] modifyPlanes(float start, float end,
                                                  FogRenderer.FogMode mode, FogShape fogShape, FogType fogType,
                                                  float partialTicks) {
        if (fogType != FogType.NONE) return null;
        return FogRendering.activeEffect().map(effect -> {
            Entity camE = Minecraft.getInstance().gameRenderer.getMainCamera().getEntity();
            if (!(camE instanceof LivingEntity le)) return null;
            float far = Mth.lerp(effect.getBlendFactor(le, partialTicks), end, 15F);
            float near = (mode == FogRenderer.FogMode.FOG_SKY ? -2F : far * -0.5F);
            return new float[]{near, far};
        }).orElse(null);
    }
}
