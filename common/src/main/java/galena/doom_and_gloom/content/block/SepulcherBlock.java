package galena.doom_and_gloom.content.block;

import galena.doom_and_gloom.content.entity.SepulcherBlockEntity;
import galena.doom_and_gloom.index.DGBlockEntities;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGSoundEvents;
import java.awt.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SepulcherBlock extends Block implements TickingEntityBlock<SepulcherBlockEntity> {

    public static final int MAX_LEVEL = 13;
    public static final int SEALED_LEVELS = 5;
    public static final int READY = MAX_LEVEL + SEALED_LEVELS;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, READY);

    private static final VoxelShape OUTER_SHAPE = Shapes.block();
    private static final VoxelShape[] SHAPES = Util.make(new VoxelShape[MAX_LEVEL + 1], shapes -> {
        for (int level = 0; level < shapes.length; level++) {
            shapes[level] = Shapes.join(OUTER_SHAPE, Block.box(2.0, Math.max(2, 1 + level), 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);
        }
    });

    public SepulcherBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LEVEL);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int fillLevel = state.getValue(LEVEL);

        if (fillLevel == READY) {
            extract(player, state, level, pos);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        var held = player.getItemInHand(hand);

        if (tryInsert(held, player, state, level, pos, false)) {
            player.awardStat(Stats.ITEM_USED.get(held.getItem()));
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    public static boolean tryInsert(ItemStack stack, @Nullable Player player, BlockState state, Level level, BlockPos pos, boolean simulate) {
        int fillLevel = state.getValue(LEVEL);
        var food = stack.getFoodProperties(null);

        if (fillLevel < MAX_LEVEL && food != null && food.isMeat()) {
            if (!simulate) insert(player, state, level, pos, level.random.nextIntBetweenInclusive(1, 2));
            return true;
        }

        return false;
    }

    public static void insert(@Nullable Entity user, BlockState state, Level level, BlockPos pos, int by) {
        var newState = state.setValue(LEVEL, Math.min(MAX_LEVEL, state.getValue(LEVEL) + by));
        level.setBlock(pos, newState, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(user, newState));

        if(level instanceof ServerLevel serverLevel) {
            var vec = Vec3.atCenterOf(pos);
            serverLevel.sendParticles(ParticleTypes.COMPOSTER, vec.x, vec.y, vec.z, 10, 0.3, 0.3, 0.3, 0.0);
        }

        level.playSound(null, pos, DGSoundEvents.SEPULCHER_FILLED.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    public static void extract(@Nullable Entity user, BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            Vec3 vec = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
            ItemEntity item = new ItemEntity(level, vec.x(), vec.y(), vec.z(), new ItemStack(DGBlocks.BONE_PILE.get()));
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
        }

        clear(user, state, level, pos);
    }

    public static void spawnConsumeParticles(Vec3 at) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        var particles = Minecraft.getInstance().particleEngine;
        var state = DGBlocks.ROTTING_FLESH.get().defaultBlockState();
        for (int i = 0; i < 20; i++) {
            var vec = at.add(level.random.nextDouble() - 0.5, level.random.nextDouble() * 2, level.random.nextDouble() - 0.5);
            particles.add(new TerrainParticle(level, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0, state));
        }
    }

    public static void spawnRottingParticles(BlockPos at) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        var effectColor = new Color(8889187);
        for (int i = 0; i < 20; i++) {
            var vec = Vec3.atBottomCenterOf(at).add(level.random.nextDouble() - 0.5, 0.8, level.random.nextDouble() - 0.5);
            level.addParticle(ParticleTypes.ENTITY_EFFECT, vec.x, vec.y, vec.z, effectColor.getRed() / 255D, effectColor.getGreen() / 255D, effectColor.getBlue() / 255D);
        }
    }

    public static void clear(@Nullable Entity user, BlockState state, Level level, BlockPos pos) {
        var empty = state.setValue(LEVEL, 0);
        level.setBlockAndUpdate(pos, empty);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(user, empty));

        level.playSound(null, pos, DGSoundEvents.SEPULCHER_HARVEST.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int fillLevel = state.getValue(LEVEL);
        if (fillLevel == READY) return SHAPES[0];
        if (fillLevel > MAX_LEVEL) return OUTER_SHAPE;
        return SHAPES[fillLevel];
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SepulcherBlockEntity(pos, state);
    }

    @Override
    public BlockEntityType<SepulcherBlockEntity> getType() {
        return DGBlockEntities.SEPULCHER.get();
    }

}
