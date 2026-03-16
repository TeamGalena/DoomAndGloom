package galena.doom_and_gloom.content.entity.holler;

import galena.doom_and_gloom.index.*;
import galena.doom_and_gloom.index.DGBlocks;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Holler extends PathfinderMob {

    private static final List<SensorType<? extends Sensor<? super Holler>>> SENSOR_TYPES = List.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.HURT_BY
    );

    private static final List<MemoryModuleType<?>> MEMORY_TYPES = List.of(
            MemoryModuleType.PATH,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.IS_PANICKING
    );

    private Vec3 prevPosDelta = Vec3.ZERO;
    private Vec3 prevPosDeltaO = Vec3.ZERO;
    private float prevYRotDelta = 0;
    private float prevYRotDeltaO = 0;

    public Holler(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new HollerPanicGoal(this, 3F));
        goalSelector.addGoal(3, new HollerAvoidGoal(this, 5F, 3F));
        goalSelector.addGoal(4, new HollerFollowGoal(this, 10F, 5F));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0F));
        goalSelector.addGoal(9, new HollerStrollGoal(this, 1F));

        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, it -> !it.hasEffect(DGEffects.WARDING.get())));
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (tickCount % 20 == 0) {
            applyFogAround((ServerLevel) level(), position(), this, 20);
        }
    }

    public static void applyFogAround(ServerLevel level, Vec3 pos, Entity source, int radius) {
        var duration = 20 * 60 * 4;

        var applied = level.getPlayers(player ->
                player.gameMode.isSurvival()
                        && pos.closerThan(player.position(), radius)
                        && (!player.hasEffect(DGEffects.FOG.get()) || player.getEffect(DGEffects.FOG.get()).endsWithin(duration - 1))
                        && !player.hasEffect(DGEffects.WARDING.get())
        );

        applied.forEach(it -> {
            it.addEffect(new MobEffectInstance(DGEffects.FOG.get(), 260, 0, false, false), source);
            level.playSound(it, source, DGSoundEvents.HOLLER_SHRIEKS.get(), source.getSoundSource(), 1F, 1F);
        });
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && isAlive() && tickCount % 10 == 0) {
            heal(1.0F);
        }
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    //for renderer
    public float getYRotDelta(float partialTicks) {
        return Mth.lerp(partialTicks, prevYRotDeltaO, prevYRotDelta);
    }

    public Vec3 getPosDelta(float partialTicks) {
        return prevPosDeltaO.lerp(prevPosDelta, partialTicks);
    }

    @Override
    public void tick() {
        prevPosDeltaO = prevPosDelta;
        prevYRotDeltaO = prevYRotDelta;
        //anim testing mode. remove
        if (false) {
            this.tickCount++;
            this.xo = this.getX() + Mth.sin(tickCount * 0.02f) * 2f;
            this.yo = this.getY();
            this.zo = this.getZ();
            this.yRotO = this.getYRot() + 20;
            return;
        }
        noPhysics = true;
        super.tick();
        noPhysics = false;
        setNoGravity(true);

        prevPosDelta = this.position().subtract(new Vec3(this.xOld, this.yOld, this.zOld));
        prevYRotDelta = this.yBodyRot - this.yBodyRotO;

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.10000000149011612)
                .add(Attributes.MOVEMENT_SPEED, 0.10000000149011612)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public void travel(Vec3 vec3) {
        if (isControlledByLocalInstance()) {
            if (isInWater()) {
                moveRelative(0.02F, vec3);
                move(MoverType.SELF, getDeltaMovement());
                setDeltaMovement(getDeltaMovement().scale(0.800000011920929));
            } else if (isInLava()) {
                moveRelative(0.02F, vec3);
                move(MoverType.SELF, getDeltaMovement());
                setDeltaMovement(getDeltaMovement().scale(0.5));
            } else {
                moveRelative(getSpeed(), vec3);
                move(MoverType.SELF, getDeltaMovement());
                setDeltaMovement(getDeltaMovement().scale(0.9100000262260437));
            }
        }

        calculateEntityAnimation(false);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected void checkFallDamage(double p_218316_, boolean p_218317_, BlockState state, BlockPos pos) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return DGSoundEvents.HOLLER_HOLLERS.get();
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSource) {
        return DGSoundEvents.HOLLER_HURTS.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return DGSoundEvents.HOLLER_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    void disappear() {
        playSound(DGSoundEvents.HOLLER_SHRIEKS.get(), 1F, 1F);
        discard();
    }

    public void panicFinish(Vec3 target) {
        if (!(level() instanceof ServerLevel level)) return;
        if (target.distanceToSqr(position()) > 0.5) return;

        var state = level.getBlockState(blockPosition());

        if (state.is(Blocks.JUKEBOX)) {
            disappear();

            if (state.getValue(JukeboxBlock.HAS_RECORD)) return;

            for (int i = 0; i < 6; i++) {
                var vec = Vec3.atCenterOf(blockPosition()).add(Math.random() * 1 - 0.5, Math.random() * 1 - 0.5, Math.random() * 1 - 0.5);
                level.sendParticles(DGParticleTypes.HOLLERING_SOUL.get(), vec.x, vec.y, vec.z, 1, 0, 0, 0, 0);
            }

            level.getBlockEntity(blockPosition(), BlockEntityType.JUKEBOX).ifPresent(jukebox -> {
                var stack = new ItemStack(DGItems.MUSIC_DISC_AFTERLIFE.get());
                jukebox.setFirstItem(stack);
            });
        } else {
            curseGround(level, blockPosition());
            disappear();
        }
    }

    private void curseBlock(ServerLevel level, BlockPos pos) {
        if (level.random.nextDouble() > 0.3) return;

        if (level.random.nextBoolean()) {
            level.setBlockAndUpdate(pos, DGBlocks.BURIAL_DIRT.get().defaultBlockState());
        } else if (level.random.nextBoolean()) {
            level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
        } else {
            level.setBlockAndUpdate(pos, Blocks.COARSE_DIRT.defaultBlockState());
        }

        var vec = Vec3.atCenterOf(pos.above());
        level.sendParticles(DGParticleTypes.HOLLERING_SOUL.get(), vec.x, vec.y, vec.z, 4, 0.5, 0.5, 0.5, 0.01);
    }

    private void curseGround(ServerLevel level, BlockPos center) {
        var aabb = new AABB(center).inflate(2, 1, 2);
        BlockPos.betweenClosedStream(aabb).forEach(pos -> {
            var state = level.getBlockState(pos);
            var above = level.getBlockState(pos.above());
            if (state.is(DGTags.Blocks.CAN_TURN_INTO_BURIAL_DIRT) && above.canBeReplaced()) {
                curseBlock(level, pos);
            }
        });
    }

    public static boolean checkHollerSpawnRules(EntityType<Holler> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!Monster.isDarkEnoughToSpawn(level, pos, random)) return false;
        if (spawnType != MobSpawnType.SPAWNER && spawnType != MobSpawnType.SPAWN_EGG) {
            if (pos.getY() < (level.getSeaLevel() - 10)) return false;
            var spawnRate = isHalloween() ? 8 : 5;
            if (random.nextInt(10) > spawnRate) return false;
        }
        return checkMobSpawnRules(entityType, level, spawnType, pos, random);
    }

    private static boolean isHalloween() {
        LocalDate data = LocalDate.now();
        int day = data.get(ChronoField.DAY_OF_MONTH);
        int month = data.get(ChronoField.MONTH_OF_YEAR);
        return month == 10 && day >= 20 || month == 11 && day <= 3;
    }
}
