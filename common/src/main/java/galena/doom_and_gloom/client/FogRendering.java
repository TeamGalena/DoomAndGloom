package galena.doom_and_gloom.client;

import galena.doom_and_gloom.index.DGEffects;
import galena.doom_and_gloom.index.DGParticleTypes;
import java.awt.*;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class FogRendering {

    @Nullable
    private static MobEffectInstance fogEffect;

    public static Optional<MobEffectInstance> activeEffect() {
        return Optional.ofNullable(fogEffect);
    }

    public static void clientTick() {
        if (!(Minecraft.getInstance().gameRenderer.getMainCamera().getEntity() instanceof Player player)) return;
        fogEffect = player.getEffect(DGEffects.FOG.get());

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

    public static Optional<Color> fogEffectColor(Color from, float partialTicks) {
        return activeEffect()
                .flatMap(MobEffectInstance::getFactorData)
                .map(factorData -> {
                    var color = new Color(0x697180);
                    LivingEntity entity = (LivingEntity) Minecraft.getInstance().gameRenderer.getMainCamera().getEntity();
                    float factor = factorData.getFactor(entity, partialTicks);
                    float inverseFactor = 1 - factor;

                    var red = (color.getRed() / 255F * factor + from.getRed() * inverseFactor);
                    var green = (color.getGreen() / 255F * factor + from.getGreen() * inverseFactor);
                    var blue = (color.getBlue() / 255F * factor + from.getBlue() * inverseFactor);

                    return new Color(red, green, blue);
                });
    }

}
