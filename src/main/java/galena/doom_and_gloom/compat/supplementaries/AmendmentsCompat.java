package galena.doom_and_gloom.compat.supplementaries;

import net.mehvahdjukaar.amendments.common.tile.WallLanternBlockTile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class AmendmentsCompat {

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(AmendmentsCompat::onBlockInteract);
    }

    private static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        var pos = event.getPos();
        var level = event.getLevel();
        var be = level.getBlockEntity(pos);
        var held = event.getItemStack();
        var player = event.getEntity();

        if(!(be instanceof WallLanternBlockTile lantern)) return;

        var state = lantern.getHeldBlock();
        var lit = state.getValue(CandleBlock.LIT);

        InteractionResult result = InteractionResult.PASS;

        if(held.is(Items.FLINT_AND_STEEL) && !lit) {
            level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            lantern.setHeldBlock(state.setValue(BlockStateProperties.LIT, true));
            if (player != null) {
                held.hurtAndBreak(1, player, (p_41303_) -> {
                    p_41303_.broadcastBreakEvent(event.getHand());
                });
            }

            result = InteractionResult.sidedSuccess(level.isClientSide());
        } else if(held.isEmpty() && lit) {
            level.playSound(player, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            lantern.setHeldBlock(state.setValue(BlockStateProperties.LIT, false));

            result = InteractionResult.sidedSuccess(level.isClientSide());
        }

        if(result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

}
