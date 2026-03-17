package galena.doom_and_gloom.data.provider;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;

public abstract class DGLangProvider extends LanguageProvider {

    protected DGLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    protected void addDisc(Supplier<? extends Item> disc, String desc) {
        addItem(disc, "Music Disc");
        add(disc.get().getDescriptionId() + ".desc", desc);
    }

    protected void addDisc(Supplier<? extends Item> disc, String artist, String song) {
        addDisc(disc, artist + " - " + song);
    }

    protected void addSubtitle(String category, String subtitleName, String name) {
        add("subtitles." + category + "." + subtitleName, name);
    }

    protected void add(Holder<?> holder) {
        var id = holder.unwrapKey().orElseThrow();
        var key = Util.makeDescriptionId(id.registry().getPath(), id.location());
        add(key, translate(id.location().getPath()));
    }

    private String translate(String key) {
        return Arrays.stream(key.split("_"))
                .map(it -> it.substring(0, 1).toUpperCase(Locale.ROOT) + it.substring(1))
                .collect(Collectors.joining(" "));
    }
}
