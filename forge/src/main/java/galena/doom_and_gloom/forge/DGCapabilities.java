package galena.doom_and_gloom.forge;

import galena.doom_and_gloom.DoomAndGloom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = DoomAndGloom.MOD_ID)
public class DGCapabilities {

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlock(Capabilities.ItemHandler.BLOCK, DGCapabilities::wrapContainer);
    }

    @Nullable
    private static IItemHandler wrapContainer(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, @Nullable Direction direction) {
        if (be instanceof Container container) return new InvWrapper(container);
        return null;
    }

}
