package galena.doom_and_gloom.content.entity.holler;

import galena.doom_and_gloom.index.DGEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

public class HollerAvoidGoal extends Goal {

    private final Holler mob;
    private final double atDistance;
    private final float speedModifier;
    private final TargetingConditions targetingCondition;
    private Path path = null;
    private boolean disapearAfterwards = false;

    public HollerAvoidGoal(Holler mob, double atDistance, float speedModifier) {
        this.mob = mob;
        this.atDistance = atDistance;
        this.speedModifier = speedModifier;
        this.targetingCondition = TargetingConditions.forNonCombat().range(atDistance).ignoreLineOfSight()
                .selector(it -> it instanceof ServerPlayer player && player.gameMode.isSurvival());
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        if (disapearAfterwards) {
            mob.disappear();
        }
    }

    @Override
    public boolean canUse() {
        var avoid = mob.getTarget();

        if (avoid == null) {
            avoid = mob.level().getNearestEntity(mob.level().getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(atDistance)), targetingCondition, mob, mob.getX(), mob.getY(), mob.getZ());
        }

        if (avoid != null) {
            disapearAfterwards = avoid.hasEffect(DGEffects.WARDING.get());
            var target = mob.position().subtract(avoid.position());
            var radius = disapearAfterwards ? 24 : 4;
            var away = AirRandomPos.getPosTowards(mob, radius, 1, 0, target, 1F);
            if (away != null) path = mob.getNavigation().createPath(away.x, away.y, away.z, 0);
        } else {
            path = null;
        }
        return path != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        mob.getNavigation().moveTo(path, disapearAfterwards ? speedModifier : 1F);
    }

}
