package galena.doom_and_gloom.data.provider;

import com.mojang.serialization.JsonOps;
import galena.doom_and_gloom.content.listing.BuyTagListing;
import galena.doom_and_gloom.content.listing.SellTagListing;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.mehvahdjukaar.moonlight.api.trades.ModItemListing;
import net.mehvahdjukaar.moonlight.api.trades.SimpleItemListing;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;

public abstract class ItemListingProvider extends JsonCodecProvider<ModItemListing> {

    public ItemListingProvider(PackOutput output, ExistingFileHelper fileHelper, String modid) {
        super(output, fileHelper, modid, JsonOps.INSTANCE, PackType.SERVER_DATA, "moonlight/villager_trades", ModItemListing.CODEC, new HashMap<>());
    }

    @Override
    protected final void gather(BiConsumer<ResourceLocation, ModItemListing> consumer) {
        run();
        super.gather(consumer);
    }

    protected final void add(String name, ModItemListing listing) {
        add(new ResourceLocation(modid, name), listing);
    }

    protected final void add(ResourceLocation id, ModItemListing listing) {
        if (entries.put(id, listing) != null) {
            throw new RuntimeException("there is already a item listing registered under '%s'".formatted(id));
        }
    }

    protected final LevelBuilder builder(ResourceKey<VillagerProfession> profession, int level) {
        return new LevelBuilder(profession, level);
    }

    protected final ProfessionBuilder builder(ResourceKey<VillagerProfession> profession) {
        return level -> builder(profession, level);
    }

    protected abstract void run();

    @FunctionalInterface
    public interface ProfessionBuilder {
        LevelBuilder level(int level);
    }

    public class LevelBuilder {

        private final ResourceKey<VillagerProfession> profession;
        private final int level;

        public LevelBuilder(ResourceKey<VillagerProfession> profession, int level) {
            this.profession = profession;
            this.level = level;
        }

        public LevelBuilder add(String name, ModItemListing listing) {
            ItemListingProvider.this.add(profession.location().withSuffix("/" + name), listing);
            return this;
        }

        public LevelBuilder sell(String name, int emeralds, ItemStack offer, int maxTrades, int xp) {
            return add("sell_" + name, SimpleItemListing.createDefault(
                    new ItemStack(Items.EMERALD, emeralds), ItemStack.EMPTY, offer,
                    maxTrades, Optional.of(xp), 0.2F, level
            ));
        }

        public LevelBuilder sell(String name, int emeralds, TagKey<Item> tag, int count, int maxTrades, int xp) {
            return add("sell_" + name, new SellTagListing(
                    new ItemStack(Items.EMERALD, emeralds), tag, count,
                    maxTrades, xp, 0.2F, level
            ));
        }

        public LevelBuilder buy(String name, int emeralds, ItemStack want, int maxTrades, int xp) {
            return add("buy_" + name, SimpleItemListing.createDefault(
                    want, ItemStack.EMPTY, new ItemStack(Items.EMERALD, emeralds),
                    maxTrades, Optional.of(xp), 0.2F, level
            ));
        }

        public LevelBuilder buy(String name, int emeralds, TagKey<Item> tag, int count, int maxTrades, int xp) {
            return add("buy_" + name, new BuyTagListing(
                    tag, count, new ItemStack(Items.EMERALD, emeralds),
                    maxTrades, xp, 0.2F, level
            ));
        }

    }

}
