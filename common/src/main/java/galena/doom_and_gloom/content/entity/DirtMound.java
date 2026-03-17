package galena.doom_and_gloom.content.entity;

import galena.doom_and_gloom.index.DGTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;

public class DirtMound extends Entity {

    private EntityType<? extends Mob> randomMonster(RandomSource random) {
        if (random.nextDouble() < 0.3) return EntityType.SKELETON;
        return EntityType.ZOMBIE;
    }

    public DirtMound(EntityType<?> type, Level level) {
        super(type, level);
    }

    public void spawnMonster(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        var monster = randomMonster(level.random).create(level);
        if (monster != null) {
            monster.moveTo(pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5, 0, 0);
            monster.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 14, 2, false, false));
            serverLevel.addFreshEntity(monster);
            monster.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null);
            monster.spawnAnim();
        }
    }

    @Override
    public void tick() {
        var level = level();
        var pos = blockPosition();
        if(tickCount % 10 != 0) return;

        if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.below()).is(DGTags.Blocks.CAN_TURN_INTO_BURIAL_DIRT)) {
            spawnMonster(level, pos);
            discard();
            return;
        }

        if (level.isNight() && random.nextInt(32) == 0) {
            spawnMonster(level, pos);
            discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

}
