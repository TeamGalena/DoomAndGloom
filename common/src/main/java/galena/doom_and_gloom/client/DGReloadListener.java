package galena.doom_and_gloom.client;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class DGReloadListener extends SimplePreparableReloadListener<List<Integer>> {

    public static final List<Integer> STONE_TABLET_TEXT_COLORS = new ArrayList<>();

    public static int getColor() {
        return STONE_TABLET_TEXT_COLORS.get(0);
    }

    public static int getDarkColor() {
        return STONE_TABLET_TEXT_COLORS.get(1);
    }

    public static int getLightColor() {
        return STONE_TABLET_TEXT_COLORS.get(2);
    }

    @Override
    protected List<Integer> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return parsePaletteStrip(pResourceManager,
                new ResourceLocation("doom_and_gloom:textures/misc/stone_tablet_text_colors.png"),
                3);
    }

    @Override
    protected void apply(List<Integer> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        STONE_TABLET_TEXT_COLORS.clear();
        STONE_TABLET_TEXT_COLORS.addAll(pObject);
    }


    //copy pasted moonlight code

    /**
     * @param manager         resource manager
     * @param fullTexturePath texture location
     * @param expectColors    expected amount of colors. Will stop reading once the amount is reached
     * @return an ordered color list obtained by reading the provided image pixels one by one from left to right then up to bottom (like a book)
     */
    public static List<Integer> parsePaletteStrip(ResourceManager manager, ResourceLocation fullTexturePath, int expectColors) {
        try (NativeImage image = readImage(manager, fullTexturePath)) {
            List<Integer> list = new ArrayList<>();
            forEachPixel(image, (x, y) -> {
                int i = image.getPixelRGBA(x, y);
                if (i == 0 || list.size() >= expectColors) return;
                list.add(swapFormat(i));
            });
            if (list.size() < expectColors) {
                throw new RuntimeException("Image at " + fullTexturePath + " has too few colors! Expected at least " + expectColors + " and got " + list.size());
            }
            return list;
        } catch (IOException | NoSuchElementException e) {
            throw new RuntimeException("Failed to find image at location " + fullTexturePath, e);
        }
    }

    public static int swapFormat(int argb) {
        return (argb & 0xFF00FF00)
                | ((argb >> 16) & 0x000000FF)
                | ((argb << 16) & 0x00FF0000);
    }



    /**
     * Shorthand method to read a NativeImage
     */
    public static NativeImage readImage(ResourceManager manager, ResourceLocation resourceLocation) throws IOException, NoSuchElementException {
        try (var res = manager.getResource(resourceLocation).get().open()) {
            return NativeImage.read(res);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


    public static void forEachPixel(NativeImage image, BiConsumer<Integer, Integer> function) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                function.accept(x, y);
            }
        }
    }
}
