package galena.doom_and_gloom.index;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;
import static net.mehvahdjukaar.moonlight.api.platform.RegHelper.*;

import galena.doom_and_gloom.content.block.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public class DGBlocks {

    public static final RegSupplier<Block> SEPULCHER = register("sepulcher", () -> new SepulcherBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON).sound(DGSoundTypes.SEPULCHER)));
    public static final RegSupplier<Block> BONE_PILE = register("bone_pile", () -> new BonePileBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK).sound(DGSoundTypes.BONE_PILE).strength(1F)));
    public static final RegSupplier<Block> ROTTING_FLESH = registerBlock(modLoc("rotting_flesh"), () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)));
    public static final RegSupplier<Block> STONE_TABLET = register("stone_tablet", () -> new StoneTabletBlock(BlockBehaviour.Properties.copy(Blocks.STONE), StoneTabletBlock.Type.DEFAULT));
    public static final RegSupplier<Block> ENGRAVED_STONE_TABLET = register("engraved_stone_tablet", () -> new StoneTabletBlock(BlockBehaviour.Properties.copy(Blocks.STONE), StoneTabletBlock.Type.ENGRAVED));
    public static final RegSupplier<Block> CRACKED_STONE_TABLET = register("cracked_stone_tablet", () -> new StoneTabletBlock(BlockBehaviour.Properties.copy(Blocks.STONE), StoneTabletBlock.Type.ENGRAVED));

    private static final Supplier<BlockBehaviour.Properties> VIGIL_CANDLE_PROPERTIES = () -> BlockBehaviour.Properties.of().noOcclusion().lightLevel(VigilCandleBlock.LIGHT_EMISSION).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY);
    public static final RegSupplier<Block> VIGIL_CANDLE = register("vigil_candle", () -> new VigilCandleBlock(VIGIL_CANDLE_PROPERTIES.get()));
    public static final Map<DyeColor, RegSupplier<Block>> COLORED_VIGIL_CANDLES = registerColored("vigil_candle", color -> new VigilCandleBlock(VIGIL_CANDLE_PROPERTIES.get().mapColor(color)));

    public static final RegSupplier<Block> BURIAL_DIRT = register("burial_dirt", () -> new BurialDirtBlock(BlockBehaviour.Properties.copy(Blocks.DIRT)));

    public static Stream<RegSupplier<Block>> vigilCandles() {
        return Stream.of(
                Stream.of(VIGIL_CANDLE),
                COLORED_VIGIL_CANDLES.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(Map.Entry::getValue)
        ).flatMap(Function.identity());
    }

    public static <T extends Block> Map<DyeColor, RegSupplier<T>> registerColored(String baseName, Function<DyeColor, ? extends T> factory) {
        return Arrays.stream(DyeColor.values()).collect(Collectors.toMap(
                it -> it,
                color -> register(color.getSerializedName() + "_" + baseName, () -> factory.apply(color))
        ));
    }

    public static <T extends Block> RegSupplier<T> register(String name, Supplier<T> block, Function<T, ? extends BlockItem> item) {
        RegSupplier<T> register = registerBlock(modLoc(name), block);
        registerItem(modLoc(name), () -> item.apply(register.get()));
        return register;
    }

    public static <T extends Block> RegSupplier<T> register(String name, Supplier<T> block) {
        return registerBlockWithItem(modLoc(name), block);
    }

    public static <T extends Block> RegSupplier<T> register(String name, Supplier<T> block, Item.Properties prop) {
        return registerBlockWithItem(modLoc(name), block, prop, 0);
    }

    public static void init() {
        // Loads this class
    }

}
