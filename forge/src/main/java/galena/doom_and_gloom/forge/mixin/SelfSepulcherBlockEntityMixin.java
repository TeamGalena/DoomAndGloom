package galena.doom_and_gloom.forge.mixin;

import galena.doom_and_gloom.content.entity.SepulcherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SepulcherBlockEntity.class, remap = false)
public abstract class SelfSepulcherBlockEntityMixin extends BlockEntity {

    public SelfSepulcherBlockEntityMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }


}
