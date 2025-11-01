package galena.doom_and_gloom.forge.mixin;

import galena.doom_and_gloom.content.entity.SepulcherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = SepulcherBlockEntity.class, remap = false)
public abstract class SepulcherBlockEntityMixin extends BlockEntity {

    public SepulcherBlockEntityMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Unique
    private LazyOptional<InvWrapper> itemHandler = createItemHandler();

    @Unique
    private LazyOptional<InvWrapper> createItemHandler() {
        return LazyOptional.of(() -> new InvWrapper((Container) this));
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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER && facing != null && !this.remove) {
            return itemHandler.cast();
        } else {
            return super.getCapability(capability, facing);
        }
    }

}
