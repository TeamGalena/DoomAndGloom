package galena.doom_and_gloom.index;

import com.teamabnormals.blueprint.core.util.registry.BlockSubRegistryHelper;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.block.BonePileBlock;
import galena.doom_and_gloom.content.block.BurialDirtBlock;
import galena.doom_and_gloom.content.block.SepulcherBlock;
import galena.doom_and_gloom.content.block.StoneTabletBlock;
import galena.doom_and_gloom.content.block.VigilCandleBlock;
import galena.doom_and_gloom.content.item.HammerAndChiselItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = DoomAndGloom.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OBlocks {
    public static final BlockSubRegistryHelper HELPER = DoomAndGloom.REGISTRY_HELPER.getBlockSubHelper();

    public static final RegistryObject<Block> SEPULCHER = register("sepulcher", () -> new SepulcherBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON).sound(OSoundTypes.SEPULCHER)));
    public static final RegistryObject<Block> BONE_PILE = register("bone_pile", () -> new BonePileBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK).sound(OSoundTypes.BONE_PILE).strength(1F)));
    public static final RegistryObject<Block> ROTTING_FLESH = HELPER.createBlock("rotting_flesh", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)));
    public static final RegistryObject<Block> STONE_TABLET = register("stone_tablet", () -> new StoneTabletBlock(BlockBehaviour.Properties.copy(Blocks.STONE), StoneTabletBlock.Type.DEFAULT));
    public static final RegistryObject<Block> ENGRAVED_STONE_TABLET = register("engraved_stone_tablet", () -> new StoneTabletBlock(BlockBehaviour.Properties.copy(Blocks.STONE), StoneTabletBlock.Type.ENGRAVED));
    public static final RegistryObject<Block> CRACKED_STONE_TABLET = register("cracked_stone_tablet", () -> new StoneTabletBlock(BlockBehaviour.Properties.copy(Blocks.STONE), StoneTabletBlock.Type.ENGRAVED));

    private static final Supplier<BlockBehaviour.Properties> VIGIL_CANDLE_PROPERTIES = () -> BlockBehaviour.Properties.of().noOcclusion().lightLevel(VigilCandleBlock.LIGHT_EMISSION).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY);
    public static final RegistryObject<Block> VIGIL_CANDLE = register("vigil_candle", () -> new VigilCandleBlock(VIGIL_CANDLE_PROPERTIES.get()));
    public static final Map<DyeColor, RegistryObject<Block>> COLORED_VIGIL_CANDLES = registerColored("vigil_candle", color -> new VigilCandleBlock(VIGIL_CANDLE_PROPERTIES.get().mapColor(color)));

    public static final RegistryObject<Block> BURIAL_DIRT = register("burial_dirt", () -> new BurialDirtBlock(BlockBehaviour.Properties.copy(Blocks.DIRT)));

    public static Stream<RegistryObject<Block>> vigilCandles() {
        return Stream.of(
                Stream.of(VIGIL_CANDLE),
                COLORED_VIGIL_CANDLES.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(Map.Entry::getValue)
        ).flatMap(Function.identity());
    }

    public static <T extends Block> Map<DyeColor, RegistryObject<T>> registerColored(String baseName, Function<DyeColor, ? extends T> factory) {
        return Arrays.stream(DyeColor.values()).collect(Collectors.toMap(
                it -> it,
                color -> register(color.getSerializedName() + "_" + baseName, () -> factory.apply(color))
        ));
    }

    public static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> block, Function<T, ? extends BlockItem> item) {
        RegistryObject<T> register = HELPER.createBlockNoItem(name, block);
        OItems.HELPER.createItem(name, () -> item.apply(register.get()));
        return register;
    }

    public static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> block) {
        return register(name, block, OBlocks::createBlockItem);
    }

    private static BlockItem createBlockItem(final Block block) {
        return new BlockItem(Objects.requireNonNull(block), new Item.Properties());
    }
}
