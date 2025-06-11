package galena.doom_and_gloom.content.entity;

import galena.doom_and_gloom.DGConfig;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.block.SepulcherBlock;
import galena.doom_and_gloom.index.OBlockEntities;
import galena.doom_and_gloom.index.OBlocks;
import galena.doom_and_gloom.index.OSoundEvents;
import galena.doom_and_gloom.index.OTags;
import galena.doom_and_gloom.network.DGNetwork;
import galena.doom_and_gloom.network.packet.SepulcherConsumesDeathPacket;
import galena.doom_and_gloom.network.packet.SepulcherRotsPacket;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class SepulcherBlockEntity extends BlockEntity implements Ticking, Container, GameEventListener.Holder<SepulcherBlockEntity.DeathListener> {

    private final DeathListener listener;
    private LazyOptional<InvWrapper> itemHandler;
    private int progress = 0;
    private boolean heated = false;

    public SepulcherBlockEntity(BlockPos pos, BlockState state) {
        super(OBlockEntities.SEPULCHER.get(), pos, state);
        this.listener = new DeathListener();
        this.itemHandler = createItemHandler();
    }

    private int progressNeeded(int fillLevel) {
        if (fillLevel == SepulcherBlock.MAX_LEVEL) return 20;
        return DGConfig.COMMON.sepulcherDuration.get() / (SepulcherBlock.SEALED_LEVELS - 1);
    }

    @Override
    public void tick(BlockState state, Level level, BlockPos pos) {
        int fillLevel = state.getValue(SepulcherBlock.LEVEL);
        if (fillLevel < SepulcherBlock.MAX_LEVEL) return;
        if (fillLevel == SepulcherBlock.READY) return;

        if (level.getGameTime() % 20L == 0) {
            checkHeatSource(level, pos);
        }

        var step = heated ? 3 : 1;
        progress += step;

        if (progress < progressNeeded(fillLevel)) return;

        int nextLevel = fillLevel + 1;
        level.setBlockAndUpdate(pos, state.setValue(SepulcherBlock.LEVEL, nextLevel));
        progress = 0;

        if (fillLevel == SepulcherBlock.MAX_LEVEL) {
            sound(OSoundEvents.SEPULCHER_SEALING, 1F);
        } else if (nextLevel == SepulcherBlock.READY) {
            sound(OSoundEvents.SEPULCHER_UNSEALING, 1F);
        } else {
            sound(OSoundEvents.SEPULCHER_ROTTING, 0.5F);
        }

        if(!level.isClientSide()) {
            DGNetwork.CHANNEL.send(
                    PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 16.0, level.dimension())),
                    new SepulcherRotsPacket(pos)
            );
        }
    }

    private void sound(Supplier<? extends SoundEvent> sound, float volume) {
        if (!hasLevel()) return;
        level.playSound(null, getBlockPos(), sound.get(), SoundSource.BLOCKS, volume, 1F);
    }

    private void checkHeatSource(Level level, BlockPos pos) {
        var below = pos.below();
        var belowState = level.getBlockState(below);
        heated = belowState.is(OTags.Blocks.HEAT_SOURCE);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("progress", progress);
        nbt.putBoolean("heated", heated);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        progress = nbt.getInt("progress");
        heated = nbt.getBoolean("heated");
    }

    public static boolean wasConsumerBySepulcher(Entity entity) {
        return entity.getPersistentData().getBoolean(DeathListener.TAG_KEY);
    }

    @Override
    public DeathListener getListener() {
        return listener;
    }

    public class DeathListener implements GameEventListener {
        private final PositionSource listenerSource;
        private final int listenerRadius;

        private static final String TAG_KEY = DoomAndGloom.MOD_ID + ":sepulched";

        public DeathListener() {
            this.listenerSource = new BlockPositionSource(SepulcherBlockEntity.this.getBlockPos());
            this.listenerRadius = 3;
        }

        public PositionSource getListenerSource() {
            return this.listenerSource;
        }

        public int getListenerRadius() {
            return this.listenerRadius;
        }

        public boolean handleGameEvent(ServerLevel level, GameEvent event, GameEvent.Context context, Vec3 vec) {
            if (event != GameEvent.ENTITY_DIE) return false;

            var entity = context.sourceEntity();
            if (entity == null) return false;
            if (wasConsumerBySepulcher(entity)) return false;

            if (!entity.getType().is(OTags.Entities.FILLS_SEPULCHER)) return false;

            var state = getBlockState();
            var fillLevel = state.getValue(SepulcherBlock.LEVEL);

            if (fillLevel >= SepulcherBlock.MAX_LEVEL) return false;

            entity.getPersistentData().putBoolean(TAG_KEY, true);

            if (entity instanceof LivingEntity living && !(entity instanceof Player)) {
                living.skipDropExperience();
            }

            SepulcherBlock.insert(null, state, level, getBlockPos(), level.random.nextIntBetweenInclusive(3, 4));

            sound(OSoundEvents.SEPULCHER_CORPSE_STUFFED, 1F);

            DGNetwork.CHANNEL.send(
                    PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(vec.x, vec.y, vec.z, 16.0, entity.level().dimension())),
                    new SepulcherConsumesDeathPacket(vec)
            );

            entity.setPos(Vec3.atCenterOf(getBlockPos()));
            if (entity.getPose() == Pose.DYING) entity.setPose(Pose.STANDING);

            return true;
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = createItemHandler();
    }

    private LazyOptional<InvWrapper> createItemHandler() {
        return LazyOptional.of(() -> new InvWrapper(this));
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        var fillLevel = getBlockState().getValue(SepulcherBlock.LEVEL);
        return fillLevel < SepulcherBlock.READY;
    }

    @Override
    public ItemStack getItem(int slot) {
        var fillLevel = getBlockState().getValue(SepulcherBlock.LEVEL);
        if (fillLevel == SepulcherBlock.READY) return new ItemStack(OBlocks.BONE_PILE.get());
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        var stack = getItem(slot);
        if (!stack.isEmpty()) SepulcherBlock.clear(null, getBlockState(), getLevel(), getBlockPos());
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return removeItem(slot, 1);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return SepulcherBlock.tryInsert(stack, null, getBlockState(), getLevel(), getBlockPos(), true);
    }

    @Override
    public boolean canTakeItem(Container container, int slot, ItemStack stack) {
        return getBlockState().getValue(SepulcherBlock.LEVEL) == SepulcherBlock.READY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        SepulcherBlock.tryInsert(stack, null, getBlockState(), getLevel(), getBlockPos(), false);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        SepulcherBlock.clear(null, getBlockState(), getLevel(), getBlockPos());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER && facing != null && !this.remove) {
            return itemHandler.cast();
        } else {
            return super.getCapability(capability, facing);
        }
    }
}
