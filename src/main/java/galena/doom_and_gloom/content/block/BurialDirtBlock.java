package galena.doom_and_gloom.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BurialDirtBlock extends Block {

    public BurialDirtBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean pDropExperience) {
        spawnMonster(level, pos);
    }

    private EntityType<? extends Mob> randomMonster(RandomSource random) {
        if (random.nextDouble() < 0.3) return EntityType.SKELETON;
        return EntityType.ZOMBIE;
    }

    public void spawnMonster(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        var monster = randomMonster(level.random).create(level);
        if (monster != null) {
            monster.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            monster.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 14, 2, false, false));
            serverLevel.addFreshEntity(monster);
            monster.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
            monster.spawnAnim();
        }
    }

    private void discard(ServerLevel level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(random.nextInt(64) == 0) return;

        if (level.isNight() && random.nextInt(32) == 0) {
            spawnMonster(level, pos);
            discard(level, pos);
            return;
        }

        if (!level.getBlockState(pos.above()).canBeReplaced()) {
            discard(level, pos);
        }
    }
}
