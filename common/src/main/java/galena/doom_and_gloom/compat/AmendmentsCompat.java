package galena.doom_and_gloom.compat;

import net.mehvahdjukaar.amendments.common.tile.WallLanternBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CandleBlock;

public class AmendmentsCompat {


    //TODO: add better amendments API to register interactions on wall lanterns
    public static boolean onBlockInteract(Level level, BlockPos pos,
                                          Player player, InteractionHand hand, ItemStack held) {

        var be = level.getBlockEntity(pos);

        if (!(be instanceof WallLanternBlockTile lantern)) return false;

        var state = lantern.getHeldBlock();
        if (!state.hasProperty(CandleBlock.LIT)) return false;

        boolean lit = state.getValue(CandleBlock.LIT);

        InteractionResult result = InteractionResult.PASS;

        if (held.is(Items.FLINT_AND_STEEL) && !lit) {
            level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            lantern.setHeldBlock(state.setValue(CandleBlock.LIT, true));
            if (player != null) {
                held.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }

            return true;
        } else if (held.isEmpty() && lit) {
            level.playSound(player, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            lantern.setHeldBlock(state.setValue(CandleBlock.LIT, false));

            return true;
        }

        return false;
    }


}
