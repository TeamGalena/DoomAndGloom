package galena.doom_and_gloom.data.provider;

import static galena.doom_and_gloom.DoomAndGloom.MOD_ID;
import static galena.doom_and_gloom.content.block.StoneTabletBlock.Attachment;
import static net.neoforged.neoforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.block.SepulcherBlock;
import galena.doom_and_gloom.content.block.StoneTabletBlock;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public abstract class DGBlockStateProvider extends BlockStateProvider {

    public DGBlockStateProvider(PackOutput output, ExistingFileHelper help) {
        super(output, MOD_ID, help);
    }

    protected ResourceLocation texture(String name) {
        return modLoc(BLOCK_FOLDER + "/" + name);
    }

    protected String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    protected String name(Supplier<? extends Block> block) {
        return name(block.get());
    }

    private static String asPrefix(@Nullable String string) {
        return Optional.ofNullable(string).map(it -> it + "_").orElse("");
    }

    private static String asSuffix(@Nullable String string) {
        return Optional.ofNullable(string).map(it -> "_" + it).orElse("");
    }

    private String sepulcherSuffix(int fillLevel) {
        if (fillLevel == 0) return "";
        if (fillLevel > SepulcherBlock.MAX_LEVEL) return "_sealed_" + (fillLevel - SepulcherBlock.MAX_LEVEL);
        return "_being_filled_" + fillLevel;
    }

    private BlockModelBuilder sepulcherContents(ResourceLocation texture, String name, float progress) {
        return models().getBuilder(name)
                .texture("texture", texture)
                .element()
                .from(2, 3, 2)
                .to(14, 4 + 11 * progress, 14)
                .face(Direction.UP).uvs(2, 2, 14, 14).end()
                .face(Direction.NORTH).uvs(2, 2, 14, 12 * progress).end()
                .face(Direction.SOUTH).uvs(2, 2, 14, 12 * progress).end()
                .face(Direction.WEST).uvs(2, 2, 14, 12 * progress).end()
                .face(Direction.EAST).uvs(2, 2, 14, 12 * progress).end()
                .faces((d, it) -> it.texture("#texture"))
                .end();
    }

    public void sepulcherBlock(Supplier<? extends Block> block) {
        var builder = getMultipartBuilder(block.get());

        var name = blockTexture(block.get());
        var base = models().getExistingFile(name);
        var seal = models().getExistingFile(name.withSuffix("_seal"));

        builder.part().modelFile(base).addModel();

        var sealedLevels = IntStream.range(SepulcherBlock.MAX_LEVEL + 1, SepulcherBlock.READY)
                .boxed()
                .toArray(Integer[]::new);

        builder.part()
                .modelFile(seal)
                .addModel()
                .condition(SepulcherBlock.LEVEL, sealedLevels);

        for (int level = 1; level <= SepulcherBlock.MAX_LEVEL; level++) {
            var texture = name.withSuffix("_rot_1");
            var progress = level / (float) SepulcherBlock.MAX_LEVEL;
            var contents = sepulcherContents(texture, name.getPath() + "_filled_" + level, progress);

            builder.part()
                    .modelFile(contents)
                    .addModel()
                    .condition(SepulcherBlock.LEVEL, level);
        }

        builder.part()
                .modelFile(sepulcherContents(name.withSuffix("_rot_1"), name.getPath() + "_rotting_1", 10 / 12F))
                .addModel()
                .condition(SepulcherBlock.LEVEL, SepulcherBlock.READY - 4);

        builder.part()
                .modelFile(sepulcherContents(name.withSuffix("_rot_2"), name.getPath() + "_rotting_2", 8 / 12F))
                .addModel()
                .condition(SepulcherBlock.LEVEL, SepulcherBlock.READY - 3);

        builder.part()
                .modelFile(sepulcherContents(name.withSuffix("_rot_3"), name.getPath() + "_rotting_3", 6 / 12F))
                .addModel()
                .condition(SepulcherBlock.LEVEL, SepulcherBlock.READY - 2);

        builder.part()
                .modelFile(sepulcherContents(name.withSuffix("_rot_4"), name.getPath() + "_rotting_4", 4 / 12F))
                .addModel()
                .condition(SepulcherBlock.LEVEL, SepulcherBlock.READY - 1);

        builder.part()
                .modelFile(sepulcherContents(name.withSuffix("_rot_5"), name.getPath() + "_bones", 2 / 12F))
                .addModel()
                .condition(SepulcherBlock.LEVEL, SepulcherBlock.READY);
    }

    private String candleSuffix(int amount) {
        return switch (amount) {
            case 1 -> "single";
            case 2 -> "double";
            case 3 -> "triple";
            case 4 -> "quadruple";
            default -> throw new IllegalArgumentException("Illegal candle amount: " + amount);
        };
    }

    public void vigilCandle(Supplier<? extends Block> block, @Nullable String prefix) {
        getVariantBuilder(block.get()).forAllStatesExcept(state -> {
            var candles = state.getValue(BlockStateProperties.CANDLES);
            boolean hanging = state.getValue(BlockStateProperties.HANGING);
            boolean lit = state.getValue(AbstractCandleBlock.LIT);

            var hangingSuffix = hanging ? "_ceiling" : "";
            var parent = "vigil_candle_" + candleSuffix(candles) + hangingSuffix;
            var optionalPrefix = Optional.ofNullable(prefix).map(it -> it + "_");
            var litSuffix = lit ? "_lit" : "";
            var name = optionalPrefix.orElse("default") + parent + litSuffix;
            var texture = BLOCK_FOLDER + "/" + optionalPrefix.orElse("") + "vigil_candle" + litSuffix;

            var model = models().withExistingParent(name, DoomAndGloom.modLoc(parent))
                    .texture("0", texture);

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .build();
        }, BlockStateProperties.WATERLOGGED);
    }

    public void stoneTablet(Supplier<? extends Block> block, @Nullable String type) {
        getVariantBuilder(block.get()).forAllStatesExcept(state -> {
            var attachment = state.getValue(StoneTabletBlock.ATTACHMENT);

            var suffix = switch (attachment) {
                case Attachment.WALL -> "_wall";
                case Attachment.CEILING, Attachment.FLOOR -> "_floor";
                default -> "";
            };

            var xRot = attachment == Attachment.FLOOR ? 180 : 0;
            var yRot = switch (state.getValue(StoneTabletBlock.FACING)) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };

            var base = "stone_tablet" + suffix;
            var name = asPrefix(type) + base;
            var parent = DoomAndGloom.modLoc(BLOCK_FOLDER + "/template/" + base);
            var texture = BLOCK_FOLDER + "/stone_tablet" + asSuffix(type);

            var model = models().withExistingParent(name, parent)
                    .texture("texture", texture);

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(yRot)
                    .rotationX(xRot)
                    .build();
        }, BlockStateProperties.WATERLOGGED);
    }

}
