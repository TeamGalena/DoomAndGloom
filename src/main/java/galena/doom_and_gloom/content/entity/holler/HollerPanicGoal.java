package galena.doom_and_gloom.content.entity.holler;

import galena.doom_and_gloom.index.OTags;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HollerPanicGoal extends PanicGoal {
    private final Holler holler;

    public HollerPanicGoal(Holler mob, float speedMultiplier) {
        super(mob, speedMultiplier);
        holler = mob;
    }

    @Override
    public void stop() {
        var target = new Vec3(posX, posY, posZ);
        holler.panicFinish(target);
        super.stop();
    }

    @Override
    protected boolean shouldPanic() {
        return super.shouldPanic() || mob.getBrain().getMemory(MemoryModuleType.IS_PANICKING).orElse(false);
    }

    @Override
    protected boolean findRandomPosition() {
        var pos = getPanicPos();
        if(pos == null) return false;

        posX = pos.x;
        posY = pos.y;
        posZ = pos.z;

        return true;
    }

    private @Nullable Vec3 getPanicPos() {
        Optional<Vec3> dirtPosition = findTargetPos().map(Vec3::atBottomCenterOf);
        return dirtPosition.orElseGet(() -> LandRandomPos.getPos(mob, 5, 5));
    }

    private Optional<BlockPos> findPos(Predicate<BlockState> filter) {
        return BlockPos.findClosestMatch(mob.blockPosition(), 10, 10, pos -> {
            var state = mob.level().getBlockState(pos);
            return filter.test(state);
        });
    }

    private Optional<BlockPos> findTargetPos() {
        return findPos(state -> state.is(Blocks.JUKEBOX) && !state.getValue(JukeboxBlock.HAS_RECORD))
                .or(() -> findPos(state -> state.is(OTags.Blocks.CAN_TURN_INTO_BURIAL_DIRT)));
    }
}
