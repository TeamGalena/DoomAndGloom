package galena.doom_and_gloom.content.entity.holler;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HollerStrollGoal extends RandomStrollGoal {

    public HollerStrollGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    @Override
    protected @Nullable Vec3 getPosition() {
        return AirRandomPos.getPosTowards(mob, 10, 2, 0, mob.position(), 1.0);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && mob.getTarget() == null;
    }
}
