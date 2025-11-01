package galena.doom_and_gloom.content.block;

import galena.doom_and_gloom.content.entity.Ticking;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface TickingEntityBlock<T extends BlockEntity & Ticking> extends EntityBlock {

    BlockEntityType<T> getType();

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    default <R extends BlockEntity> BlockEntityTicker<R> getTicker(Level level, BlockState state, BlockEntityType<R> type) {
        if (type == getType()) {
            BlockEntityTicker<T> ticker = (l, p, s, be) -> be.tick(s, l, p);
            return (BlockEntityTicker<R>) ticker;
        }

        return null;
    }
}
