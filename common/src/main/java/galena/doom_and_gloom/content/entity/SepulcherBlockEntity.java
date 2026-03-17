package galena.doom_and_gloom.content.entity;

import galena.doom_and_gloom.DGConfig;
import galena.doom_and_gloom.content.block.SepulcherBlock;
import galena.doom_and_gloom.index.DGBlockEntities;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGSoundEvents;
import galena.doom_and_gloom.index.DGTags;
import galena.doom_and_gloom.network.packet.SepulcherConsumesDeathPacket;
import galena.doom_and_gloom.network.packet.SepulcherRotsPacket;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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

public class SepulcherBlockEntity extends BlockEntity implements Ticking, Container, GameEventListener.Provider<SepulcherBlockEntity.DeathListener> {

    private final DeathListener listener;
    private int progress = 0;
    private boolean heated = false;

    public SepulcherBlockEntity(BlockPos pos, BlockState state) {
        super(DGBlockEntities.SEPULCHER.get(), pos, state);
        this.listener = new DeathListener();
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
            sound(DGSoundEvents.SEPULCHER_SEALING, 1F);
        } else if (nextLevel == SepulcherBlock.READY) {
            sound(DGSoundEvents.SEPULCHER_UNSEALING, 1F);
        } else {
            sound(DGSoundEvents.SEPULCHER_ROTTING, 0.5F);
        }

        if (level instanceof ServerLevel serverLevel) {
            NetworkHelper.sendToAllClientPlayersInRange(serverLevel, pos, 16.0, new SepulcherRotsPacket(pos));
        }
    }

    private void sound(Supplier<? extends SoundEvent> sound, float volume) {
        if (!hasLevel()) return;
        level.playSound(null, getBlockPos(), sound.get(), SoundSource.BLOCKS, volume, 1F);
    }

    private void checkHeatSource(Level level, BlockPos pos) {
        var below = pos.below();
        var belowState = level.getBlockState(below);
        heated = belowState.is(DGTags.Blocks.HEAT_SOURCE);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        nbt.putInt("progress", progress);
        nbt.putBoolean("heated", heated);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        progress = nbt.getInt("progress");
        heated = nbt.getBoolean("heated");
    }

    @Override
    public DeathListener getListener() {
        return listener;
    }

    public class DeathListener implements GameEventListener {
        private final PositionSource listenerSource;
        private final int listenerRadius;

        public DeathListener() {
            this.listenerSource = new BlockPositionSource(SepulcherBlockEntity.this.getBlockPos());
            this.listenerRadius = 3;
        }

        @Override
        public PositionSource getListenerSource() {
            return this.listenerSource;
        }

        @Override
        public int getListenerRadius() {
            return this.listenerRadius;
        }

        @Override
        public boolean handleGameEvent(ServerLevel level, Holder<GameEvent> event, GameEvent.Context context, Vec3 vec) {
            if (!GameEvent.ENTITY_DIE.is(event)) return false;

            Entity entity = context.sourceEntity();
            if (!(entity instanceof LivingEntity living)) return false;
            ISepulcherable sepulchered = ISepulcherable.cast(living);
            if (sepulchered.DG$wasSepulchered()) return false;

            if (!entity.getType().is(DGTags.Entities.FILLS_SEPULCHER)) return false;

            BlockState state = getBlockState();
            int fillLevel = state.getValue(SepulcherBlock.LEVEL);

            if (fillLevel >= SepulcherBlock.MAX_LEVEL) return false;

            sepulchered.DG$setSepulchered(true);

            if (!(entity instanceof Player)) {
                living.skipDropExperience();
            }

            SepulcherBlock.insert(null, state, level, getBlockPos(), level.random.nextIntBetweenInclusive(3, 4));

            sound(DGSoundEvents.SEPULCHER_CORPSE_STUFFED, 1F);

            NetworkHelper.sendToAllClientPlayersInRange(level, BlockPos.containing(vec), 16.0, new SepulcherConsumesDeathPacket(vec));

            entity.setPos(Vec3.atCenterOf(getBlockPos()));
            if (entity.getPose() == Pose.DYING) entity.setPose(Pose.STANDING);

            return true;
        }
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
        if (fillLevel == SepulcherBlock.READY) return new ItemStack(DGBlocks.BONE_PILE.get());
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

}
