package galena.doom_and_gloom.content.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.moonlight.api.trades.ModItemListing;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

public record SellTagListing(ItemCost want, TagKey<Item> offer, int count, int maxTrades, int xp, float priceMult,
                             int level) implements ModItemListing {

    public static final MapCodec<SellTagListing> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    ItemCost.CODEC.fieldOf("price").forGetter(SellTagListing::want),
                    TagKey.codec(Registries.ITEM).fieldOf("offer").forGetter(SellTagListing::offer),
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(SellTagListing::count),
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_trades", 16).forGetter(SellTagListing::maxTrades),
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("xp", 1).forGetter(SellTagListing::xp),
                    ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("price_multiplier", 1F).forGetter(SellTagListing::priceMult),
                    Codec.intRange(1, 5).optionalFieldOf("level", 1).forGetter(SellTagListing::level)
            ).apply(builder, SellTagListing::new)
    );

    @Override
    public MapCodec<? extends ModItemListing> getCodec() {
        return CODEC;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public MerchantOffer getOffer(Entity entity, RandomSource random) {
        var registry = entity.level().registryAccess().registryOrThrow(Registries.ITEM);
        return registry.getOrCreateTag(offer)
                .getRandomElement(random)
                .map(Holder::value)
                .map(Item::getDefaultInstance)
                .map(offer -> new MerchantOffer(want, offer, maxTrades, xp, priceMult))
                .orElse(null);
    }

}
