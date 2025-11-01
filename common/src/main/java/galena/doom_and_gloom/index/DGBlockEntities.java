package galena.doom_and_gloom.index;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;

import galena.doom_and_gloom.content.block.StoneTabletBlockEntity;
import galena.doom_and_gloom.content.entity.SepulcherBlockEntity;
import galena.doom_and_gloom.content.entity.VigilCandleBlockEntity;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DGBlockEntities {

    public static final RegSupplier<BlockEntityType<VigilCandleBlockEntity>> VIGIL_CANDLE = register("vigil_candle", VigilCandleBlockEntity::new, DGBlocks.vigilCandles().toArray(Supplier[]::new));
    public static final RegSupplier<BlockEntityType<SepulcherBlockEntity>> SEPULCHER = register("sepulcher", SepulcherBlockEntity::new, DGBlocks.SEPULCHER);
    public static final RegSupplier<BlockEntityType<StoneTabletBlockEntity>> STONE_TABLET = register("stone_tablet", StoneTabletBlockEntity::new, DGBlocks.STONE_TABLET);

    @SafeVarargs
    private static <E extends BlockEntity> RegSupplier<BlockEntityType<E>> register(String name, BiFunction<BlockPos, BlockState, E> factory, Supplier<Block>... blocks) {
        return RegHelper.registerBlockEntityType(modLoc(name), factory, blocks);
    }

    public static void init() {
        // Loads this class
    }

}
