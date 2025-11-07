package galena.doom_and_gloom.content.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface Ticking {

    void tick(BlockState state, Level level, BlockPos pos);

}
