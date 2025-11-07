package galena.doom_and_gloom.fabric;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.compat.AmendmentsCompat;
import galena.doom_and_gloom.compat.CompatMods;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        DoomAndGloom.init();

        //if this doesnt work use server started
        ServerLifecycleEvents.SERVER_STARTING.register(DoomAndGloom::onServerAboutToStart);
        if (CompatMods.AMENDMENTS) {
            UseBlockCallback.EVENT.register((player, level, interactionHand, blockHitResult) -> {
                boolean res = AmendmentsCompat.onBlockInteract(level, blockHitResult.getBlockPos(), player,
                        interactionHand, player.getItemInHand(interactionHand));
                if (res) {
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                else return InteractionResult.PASS;
            });
        }
    }


}
