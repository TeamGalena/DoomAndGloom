package galena.doom_and_gloom.data.provider;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class DGLangProvider implements DataProvider {
    private final Map<String, String> data = new TreeMap<>();
    private final PackOutput output;
    private final String modid;
    private final String locale;

    private final List<Runnable> subProviders = Lists.newArrayList();

    public DGLangProvider(PackOutput output, String modid, String locale) {
        this.output = output;
        this.modid = modid;
        this.locale = locale;
    }

    public void addSubProvider(Runnable runnable) {
        this.subProviders.add(runnable);
    }

    protected abstract void addTranslations();

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        this.subProviders.forEach(Runnable::run);
        addTranslations();
        if (!data.isEmpty())
            return save(cache, this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.modid).resolve("lang").resolve(this.locale + ".json"));

        return CompletableFuture.allOf();
    }

    @Override
    public String getName() {
        return modid + " Languages: " + locale;
    }

    private CompletableFuture<?> save(CachedOutput cache, Path target) {
        JsonObject json = new JsonObject();
        this.data.forEach(json::addProperty);

        return DataProvider.saveStable(cache, json, target);
    }

    public void addBlock(Supplier<? extends Block> key, String name) {
        add(key.get(), name);
    }

    public void add(Block key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void addItem(Supplier<? extends Item> key, String name) {
        add(key.get(), name);
    }

    public void add(Item key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void addDisc(Supplier<? extends RecordItem> disc, String desc) {
        addItem(disc, "Music Disc");
        add(disc.get().getDescriptionId() + ".desc", desc);
    }

    public void addDisc(Supplier<? extends RecordItem> disc, String artist, String song) {
        addDisc(disc, artist + " - " + song);
    }

    public void addAdvTitle(String advancementTitle, String name) {
        data.putIfAbsent("advancements." + advancementTitle + ".title", name);
    }

    public void addAdvDesc(String advancementTitle, String name) {
        data.putIfAbsent("advancements." + advancementTitle + ".description", name);
    }

    public void addSubtitle(String category, String subtitleName, String name) {
        add("subtitles." + category + "." + subtitleName, name);
    }

    public void add(ItemStack key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void add(Enchantment key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void addEffect(Supplier<? extends MobEffect> key, String name) {
        add(key.get(), name);
    }

    public void add(MobEffect key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void add(EntityType<?> key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void add(String key, String value) {
        if (data.put(key, value) != null)
            throw new IllegalStateException("Duplicate translation key " + key);
    }

    public void tryBlock(Supplier<? extends Block> block) {
        String key = block.get().getDescriptionId();
        String value = formatString(ForgeRegistries.BLOCKS.getKey(block.get()).getPath());
        data.putIfAbsent(key, value);
    }

    public void tryItem(Supplier<? extends Item> item) {
        String key = item.get().getDescriptionId();
        String value = formatString(ForgeRegistries.ITEMS.getKey(item.get()).getPath());
        data.putIfAbsent(key, value);
    }

    public void tryEntity(Supplier<? extends EntityType<?>> entity) {
        String key = entity.get().getDescriptionId();
        String value = formatString(ForgeRegistries.ENTITY_TYPES.getKey(entity.get()).getPath());
        data.putIfAbsent(key, value);
    }

    private String formatString(String key) {
        String[] strArr = key.split("_");
        StringBuffer res = new StringBuffer();
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }
        return res.toString().trim();
    }
}
