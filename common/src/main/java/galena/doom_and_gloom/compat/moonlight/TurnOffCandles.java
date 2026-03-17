package galena.doom_and_gloom.compat.moonlight;

import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import galena.doom_and_gloom.index.DGTags;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.FakePlayerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

//TODO: this isnt working! villagers arent persisting their added memory!
public class TurnOffCandles extends Behavior<Villager> {
    private final float speedModifier;
    private int ticksSinceReached = 0;
    private int cooldown = 20 * 30;
    protected int lastBreakProgress = -1;
    protected GlobalPos targetPos = null;

    public TurnOffCandles(float speed) {
        super(ImmutableMap.of(
                        MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT,
                        MoonlightCompat.CANDLES_I_LIT.get(), MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                270, 350);
        this.speedModifier = speed * 1.1f;

    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        if (cooldown-- > 0) return false;
        if (!PlatHelper.isMobGriefingOn(pLevel, pOwner)) {
            cooldown = 20 * 60;
            return false;
        }
        List<GlobalPos> globalpos = pOwner.getBrain().getMemory(MoonlightCompat.CANDLES_I_LIT.get()).get();
        GlobalPos closest = findClosestCandleILit(globalpos, pLevel, pOwner, 20);
        targetPos = closest;
        return closest != null;
    }

    @Nullable
    public GlobalPos findClosestCandleILit(List<GlobalPos> pos, Level level, Villager owner, int maxDistance) {
        GlobalPos closest = null;
        double closestDist = Double.MAX_VALUE;
        for (GlobalPos p : pos) {
            if (p.dimension() != level.dimension()) continue;
            double dist = owner.position().distanceToSqr(Vec3.atBottomCenterOf(p.pos()));
            if (dist < closestDist) {
                closest = p;
                closestDist = dist;
            }
        }
        return closest;
    }

    @Override
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        this.cooldown = 20 * (5 + pLevel.random.nextInt(20)) + pLevel.random.nextInt(20);
        this.ticksSinceReached = 0;
        this.lastBreakProgress = -1;

        pEntity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        pEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos.pos(), this.speedModifier, 1));
        //   AskCandy.displayAsHeldItem(pEntity, new ItemStack(Items.IRON_AXE));
    }

    @Override
    protected void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);
        // AskCandy.clearHeldItem(pEntity);
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        Optional<List<GlobalPos>> memory = pEntity.getBrain()
                .getMemory(MoonlightCompat.CANDLES_I_LIT.get());
        return memory.isPresent() && memory.get().contains(targetPos);
    }

    private static final GameProfile GRAVETENDER = new GameProfile(UUID.fromString("f3f3f3f3-2233-f3f3-f3f3-f3f3f3f3f3f3"), "[Gravetender]");

    @Override
    protected void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
        BlockPos pos = targetPos.pos();

        //hax
        pOwner.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        pOwner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, this.speedModifier, 2));

        pOwner.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));
        if (pos.closerToCenterThan(pOwner.position(), 2.3)) {
            this.ticksSinceReached++;

            BlockState state = pLevel.getBlockState(pos);
            if (!state.is(DGTags.Blocks.GRAVETENDER_LIGHTABLE)) {
                var list = pOwner.getBrain().getMemory(MoonlightCompat.CANDLES_I_LIT.get());
                if (list.isPresent()) {
                    list.get().remove(targetPos);
                    if (list.get().isEmpty()) {
                        pOwner.getBrain().eraseMemory(MoonlightCompat.CANDLES_I_LIT.get());
                    }
                }
            } else {
                //breaking animation. same as fodder lol. might have the same issues
                int k = (int) ((float) this.ticksSinceReached / (float) 20 * 10.0F);
                if (k != this.lastBreakProgress) {
                    this.lastBreakProgress = k;
                }

                //TODO: this task is run for candles that are already on too. We would need to clear them off first and validate thatthey canbe extinguished
                if (ticksSinceReached > 20) {
                    var player = FakePlayerManager.get(GRAVETENDER, pLevel);
                    player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    BlockHitResult hit = new BlockHitResult(Vec3.atBottomCenterOf(pos), Direction.UP, pos, false);
                    state.useWithoutItem(pLevel, player, hit);
                }
            }

        }
    }
}