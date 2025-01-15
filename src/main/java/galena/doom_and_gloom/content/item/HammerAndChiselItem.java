package galena.doom_and_gloom.content.item;

import galena.doom_and_gloom.content.block.StoneTabletBlock;
import galena.doom_and_gloom.content.block.StoneTabletBlockEntity;
import galena.doom_and_gloom.index.OItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HammerAndChiselItem extends BlockItem {

    public HammerAndChiselItem(Block block) {
        super(block, new Properties().stacksTo(1));
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        var updated = super.updateCustomBlockEntityTag(pos, level, player, stack, state);

        if (!updated && player instanceof ServerPlayer serverPlayer) {
            if (level.getBlockEntity(pos) instanceof StoneTabletBlockEntity blockEntity && state.getBlock() instanceof StoneTabletBlock block) {
                if(!serverPlayer.getAbilities().instabuild) {
                    serverPlayer.setItemInHand(serverPlayer.getUsedItemHand(), new ItemStack(OItems.BUSH_HAMMER.get()));
                }
                block.openTextEdit(serverPlayer, blockEntity);
            }
        }

        return updated;
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
}