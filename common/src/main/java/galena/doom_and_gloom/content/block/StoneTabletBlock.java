package galena.doom_and_gloom.content.block;

import galena.doom_and_gloom.index.DGBlockEntities;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.network.packet.EngraveStoneTabletPacket;
import java.util.Arrays;
import java.util.UUID;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class StoneTabletBlock extends Block implements SimpleWaterloggedBlock, TickingEntityBlock<StoneTabletBlockEntity> {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Attachment> ATTACHMENT = EnumProperty.create("attachment", Attachment.class);

    protected static final VoxelShape SHAPE_Z = Block.box(6.0, 0.0, 2.0, 10.0, 16.0, 14.0);
    protected static final VoxelShape SHAPE_X = Block.box(2.0, 0.0, 6.0, 14.0, 16.0, 10.0);

    protected static final VoxelShape SHAPE_SOUTH = Block.box(2.0D, 0.0D, 0.0D, 14.0D, 16.0D, 4.0D);
    protected static final VoxelShape SHAPE_NORTH = Block.box(2.0D, 0.0D, 12.0D, 14.0D, 16.0D, 16.0D);

    protected static final VoxelShape SHAPE_EAST = Block.box(0.0D, 0.0D, 2.0D, 4.0D, 16.0D, 14.0D);
    protected static final VoxelShape SHAPE_WEST = Block.box(12.0D, 0.0D, 2.0D, 16.0D, 16.0D, 14.0D);

    protected static final VoxelShape SHAPE_FLOOR_Z = Block.box(2.0, 0.0, 0.0, 14.0, 4.0, 16.0);
    protected static final VoxelShape SHAPE_FLOOR_X = Block.box(0.0, 0.0, 2.0, 16.0, 4.0, 14.0);

    protected static final VoxelShape SHAPE_CEILING_Z = Block.box(2.0, 12.0, 0.0, 14.0, 16.0, 16.0);
    protected static final VoxelShape SHAPE_CEILING_X = Block.box(0.0, 12.0, 2.0, 16.0, 16.0, 14.0);
    public final Type type;

    public StoneTabletBlock(Properties properties, Type type) {
        super(properties);
        this.type = type;
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(ATTACHMENT, Attachment.CENTER_UPRIGHT)
        );
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        boolean water = context.getLevel().getFluidState(pos).getType() == Fluids.WATER;
        BlockState state = this.defaultBlockState().setValue(WATERLOGGED, water);

        Level level = context.getLevel();

        Direction clickFace = context.getClickedFace();
        if (clickFace.getAxis() == Direction.Axis.Y) {
            BlockState below = level.getBlockState(pos.relative(clickFace.getOpposite()));
            if (below.getBlock() instanceof StoneTabletBlock && below.getValue(ATTACHMENT).isUpright()) {
                return state.setValue(FACING, below.getValue(FACING))
                        .setValue(ATTACHMENT, below.getValue(ATTACHMENT));
            }

            Player p = context.getPlayer();
            if (p != null && p.isShiftKeyDown()) {
                return state.setValue(FACING, context.getHorizontalDirection().getOpposite())
                        .setValue(ATTACHMENT, clickFace == Direction.UP ? Attachment.FLOOR : Attachment.CEILING);
            }

            return state.setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(ATTACHMENT, Attachment.CENTER_UPRIGHT);
        } else {
            return this.defaultBlockState().setValue(FACING, clickFace)
                    .setValue(ATTACHMENT, Attachment.WALL);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, FACING, ATTACHMENT);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(ATTACHMENT)) {
            case FLOOR -> switch (state.getValue(FACING)) {
                case NORTH, SOUTH -> SHAPE_FLOOR_Z;
                default -> SHAPE_FLOOR_X;
            };

            case CEILING -> switch (state.getValue(FACING)) {
                case NORTH, SOUTH -> SHAPE_CEILING_Z;
                default -> SHAPE_CEILING_X;
            };

            case WALL -> switch (state.getValue(FACING)) {
                case SOUTH -> SHAPE_SOUTH;
                case EAST -> SHAPE_EAST;
                case WEST -> SHAPE_WEST;
                default -> SHAPE_NORTH;
            };

            case CENTER_UPRIGHT -> switch (state.getValue(FACING)) {
                case NORTH, SOUTH -> SHAPE_X;
                default -> SHAPE_Z;
            };
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StoneTabletBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof StoneTabletBlockEntity tile))
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        if (!(player instanceof ServerPlayer serverPlayer)) return ItemInteractionResult.CONSUME;

        if (clear(level, pos, player, state, held, hand)) return ItemInteractionResult.SUCCESS;

        if (openTextEdit(serverPlayer, tile)) {
            return ItemInteractionResult.SUCCESS;
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (!(level.getBlockEntity(pos) instanceof StoneTabletBlockEntity tile))
            return InteractionResult.PASS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.CONSUME;

        if (openTextEdit(serverPlayer, tile)) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    private boolean clear(Level level, BlockPos pos, Player player, BlockState state, ItemStack held, InteractionHand hand) {
        if (!held.is(ItemTags.PICKAXES)) return false;
        if (type != Type.ENGRAVED) return false;

        var tablet = level.random.nextInt(5) == 0 ? DGBlocks.CRACKED_STONE_TABLET : DGBlocks.STONE_TABLET;

        level.setBlockAndUpdate(pos, tablet.get().withPropertiesOf(state));

        held.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

        return true;
    }

    private boolean hasEditableText(Player player, StoneTabletBlockEntity signEntity) {
        StoneTabletText signText = signEntity.getText();
        return Arrays.stream(signText.getMessages(player.isTextFilteringEnabled()))
                .allMatch((p) -> p.equals(CommonComponents.EMPTY) || p.getContents() instanceof PlainTextContents);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean openTextEdit(ServerPlayer player, StoneTabletBlockEntity blockEntity) {
        if (otherPlayerIsEditingSign(player, blockEntity)) return false;
        if (!player.mayBuild()) return false;
        if (!hasEditableText(player, blockEntity)) return false;

        blockEntity.setAllowedPlayerEditor(player.getUUID());
        NetworkHelper.sendToClientPlayer(player, new EngraveStoneTabletPacket(blockEntity.getBlockPos()));

        return true;
    }

    private boolean otherPlayerIsEditingSign(Player player, StoneTabletBlockEntity signEntity) {
        UUID id = signEntity.getPlayerWhoMayEdit();
        return id != null && !id.equals(player.getUUID());
    }

    @Override
    public BlockEntityType<StoneTabletBlockEntity> getType() {
        return DGBlockEntities.STONE_TABLET.get();
    }

    public enum Type implements StringRepresentable {
        DEFAULT, ENGRAVED, CRACKED;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

    public enum Attachment implements StringRepresentable {
        CENTER_UPRIGHT, WALL, CEILING, FLOOR;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }

        public boolean isUpright() {
            return this == CENTER_UPRIGHT || this == WALL;
        }
    }
}