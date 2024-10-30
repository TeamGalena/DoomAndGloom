package galena.doom_and_gloom.content.entity;

import galena.doom_and_gloom.index.OBlockEntities;
import galena.doom_and_gloom.index.OEffects;
import galena.doom_and_gloom.index.OTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class VigilCandleBlockEntity extends BlockEntity implements Ticking {

    public VigilCandleBlockEntity(BlockPos pos, BlockState state) {
        super(OBlockEntities.VIGIL_CANDLE.get(), pos, state);
    }

    @Override
    public void tick(BlockState state, Level level, BlockPos pos) {
        if (level.getGameTime() % 20L != 0) return;
        if (!state.getValue(CandleBlock.LIT)) return;

        var range = 3 + state.getValue(CandleBlock.CANDLES);
        var entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(range));

        var effects = level.registryAccess().registryOrThrow(Registries.MOB_EFFECT);
        var shouldClear = effects.getTagOrEmpty(OTags.Effects.VIGIL_CANDLE_CLEARS);

        entities.forEach(entity -> {
            shouldClear.forEach(effect -> {
                entity.removeEffect(effect.get());
            });

            var duration = 20 * 5;
            if (!entity.hasEffect(OEffects.WARDING.get()) || entity.getEffect(OEffects.WARDING.get()).endsWithin(duration - 1)) {
                entity.addEffect(new MobEffectInstance(OEffects.WARDING.get(), duration, 0, false, false, false));
            }
        });
    }

}
