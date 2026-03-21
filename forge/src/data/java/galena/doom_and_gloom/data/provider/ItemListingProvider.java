package galena.doom_and_gloom.data.provider;

import galena.doom_and_gloom.content.listing.BuyTagListing;
import galena.doom_and_gloom.content.listing.SellEnchantedListing;
import galena.doom_and_gloom.content.listing.SellTagListing;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.mehvahdjukaar.moonlight.api.trades.ModItemListing;
import net.mehvahdjukaar.moonlight.api.trades.SimpleItemListing;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

public abstract class ItemListingProvider extends JsonCodecProvider<ModItemListing> {

    public ItemListingProvider(PackOutput output, ExistingFileHelper fileHelper, String modid, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, PackOutput.Target.DATA_PACK, "moonlight/villager_trades", PackType.SERVER_DATA, ModItemListing.CODEC, lookup, modid, fileHelper);
    }

    protected final void add(String name, ModItemListing listing) {
        add(ResourceLocation.fromNamespaceAndPath(modid, name), listing);
    }

    protected final void add(ResourceLocation id, ModItemListing listing) {
        unconditional(id, listing);
    }

    protected final LevelBuilder builder(ResourceKey<VillagerProfession> profession, int level) {
        return new LevelBuilder(profession, level);
    }

    protected final ProfessionBuilder builder(ResourceKey<VillagerProfession> profession) {
        return level -> builder(profession, level);
    }

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
                    new ItemCost(Items.EMERALD, emeralds), Optional.empty(), offer,
                    maxTrades, Optional.of(xp), 0.2F, level, Optional.empty()
            ));
        }

        public LevelBuilder sellEnchanted(String name, int emeralds, ItemStack offer, int maxTrades, int xp) {
            return add("sell_enchanted_" + name, new SellEnchantedListing(
                    new ItemCost(Items.EMERALD, emeralds), offer,
                    maxTrades, xp, 0.2F, level
            ));
        }

        public LevelBuilder sell(String name, int emeralds, TagKey<Item> tag, int count, int maxTrades, int xp) {
            return add("sell_" + name, new SellTagListing(
                    new ItemCost(Items.EMERALD, emeralds), tag, count,
                    maxTrades, xp, 0.2F, level
            ));
        }

        public LevelBuilder buy(String name, int emeralds, ItemCost want, int maxTrades, int xp) {
            return add("buy_" + name, SimpleItemListing.createDefault(
                    want, Optional.empty(), new ItemStack(Items.EMERALD, emeralds),
                    maxTrades, Optional.of(xp), 0.2F, level, Optional.empty()
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
