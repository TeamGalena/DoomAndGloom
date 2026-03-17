package galena.doom_and_gloom.content.entity.holler;

import galena.doom_and_gloom.index.DGEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class HollerFollowGoal extends Goal {

    private final PathfinderMob mob;
    private final double maxDistanceSquared;
    private final double panicDistanceSquared;
    private int timeToRecalcPath;

    public HollerFollowGoal(PathfinderMob mob, double maxDistance, double panicDistance) {
        this.mob = mob;
        this.maxDistanceSquared = maxDistance * maxDistance;
        this.panicDistanceSquared = panicDistance * panicDistance;
    }

    @Override
    public boolean canUse() {
        var target = mob.getTarget();
        if(target == null) return false;
        if(mob.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING)) return false;
        var distanceSquared = mob.distanceToSqr(target);
        return distanceSquared > maxDistanceSquared;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !tooClose();
    }

    private boolean tooClose() {
        var target = mob.getTarget();
        if(target == null) return false;
        var distanceSquared = mob.distanceToSqr(target);

        if(target.hasEffect(DGEffects.FOG)) {
            return false;
        }

        return distanceSquared < panicDistanceSquared;
    }

    @Override
    public void start() {
        timeToRecalcPath = 0;
    }

    @Override
    public void tick() {
        var target = mob.getTarget();
        var brain = mob.getBrain();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));

        if (--this.timeToRecalcPath <= 0) {
            timeToRecalcPath = adjustedTickDelay(10);

            var distance = target.distanceToSqr(mob);
            var speed = distance > 16F ? 2F : 1F;

            mob.getNavigation().moveTo(target, speed);
        }
    }

    @Override
    public void stop() {
        super.stop();
        mob.getNavigation().stop();
    }
}
