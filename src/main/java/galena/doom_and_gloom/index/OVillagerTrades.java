package galena.doom_and_gloom.index;

import galena.doom_and_gloom.DoomAndGloom;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.EmeraldForItems;
import net.minecraft.world.entity.npc.VillagerTrades.EnchantedItemForEmeralds;
import net.minecraft.world.entity.npc.VillagerTrades.ItemsForEmeralds;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;


@Mod.EventBusSubscriber(modid = DoomAndGloom.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OVillagerTrades {


    @SubscribeEvent
    public static void tadeEvent(VillagerTradesEvent event) {
        VillagerProfession type = event.getType();
        if (type == OVillagerTypes.GRAVETENDER.get()) {
            addGravetenderTrades(event);
        }
    }

    public record TagForEmeralds(TagKey<Item> tag, int cost, int emeralds, int maxUses, int xp, float priceMultiplier) implements VillagerTrades.ItemListing {

        @Override
        public @Nullable MerchantOffer getOffer(Entity entity, RandomSource random) {
            if(!(entity.level() instanceof ServerLevel level)) return null;
            var tagHolder = level.registryAccess().registryOrThrow(Registries.ITEM).getOrCreateTag(tag);
            return tagHolder.getRandomElement(random).map(item ->
                    new MerchantOffer(new ItemStack(Items.EMERALD, cost), new ItemStack(item), maxUses, xp, priceMultiplier)
            ).orElse(null);
        }
    }

    public record EmeraldForTag(TagKey<Item> tag, int emeralds, int count, int maxUses, int xp, float priceMultiplier) implements VillagerTrades.ItemListing {

        @Override
        public @Nullable MerchantOffer getOffer(Entity entity, RandomSource random) {
            if(!(entity.level() instanceof ServerLevel level)) return null;
            var tagHolder = level.registryAccess().registryOrThrow(Registries.ITEM).getOrCreateTag(tag);
            return tagHolder.getRandomElement(random).map(item ->
                    new MerchantOffer(new ItemStack(item), new ItemStack(Items.EMERALD, emeralds), maxUses, xp, priceMultiplier)
            ).orElse(null);
        }
    }

    private static void addGravetenderTrades(VillagerTradesEvent event) {
        var trades = event.getTrades();

        trades.put(1, List.of(
                new ItemsForEmeralds(new ItemStack(Items.STONE_SHOVEL), 1, 1, 12, 1, 0.2F),
                new ItemsForEmeralds(Items.ROSE_BUSH, 1, 1, 8, 2),
                new BasicItemListing(new ItemStack(Items.POPPY, 8), new ItemStack(Items.EMERALD), 1, 12, 0.2F),
                new BasicItemListing(new ItemStack(Items.CORNFLOWER, 8), new ItemStack(Items.EMERALD), 1, 12, 0.2F)
        ));

        trades.put(2, List.of(
                new ItemsForEmeralds(Items.GOLD_NUGGET, 1, 5, 8, 5),
                new TagForEmeralds(OTags.Items.VIGIL_CANDLES, 2, 1, 12, 10, 2F),
                new ItemsForEmeralds(Items.FLOWER_POT, 2, 1, 12, 5)
        ));

        trades.put(3, List.of(
                new EnchantedItemForEmeralds(Items.IRON_SHOVEL, 2, 3, 10, 0.2F),
                new EmeraldForTag(ItemTags.CANDLES, 2, 6, 12, 2, 0.1F)
        ));

        trades.put(4, List.of(
                new EmeraldForItems(Items.LANTERN, 2, 8, 15),
                new ItemsForEmeralds(new ItemStack(Items.WITHER_ROSE), 10, 2, 8, 20, 2F)
        ));

        trades.put(5, List.of(
                new EnchantedItemForEmeralds(Items.DIAMOND_SHOVEL, 13, 3, 30, 0.2F)
        ));
    }

}
