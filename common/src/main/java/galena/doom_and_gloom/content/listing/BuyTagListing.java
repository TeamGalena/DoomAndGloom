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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

public record BuyTagListing(TagKey<Item> want, int count, ItemStack offer, int maxTrades, int xp, float priceMult,
                            int level) implements ModItemListing {

    public static final MapCodec<BuyTagListing> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    TagKey.codec(Registries.ITEM).fieldOf("price").forGetter(BuyTagListing::want),
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(BuyTagListing::count),
                    ItemStack.CODEC.fieldOf("offer").forGetter(BuyTagListing::offer),
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_trades", 16).forGetter(BuyTagListing::maxTrades),
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("xp", 1).forGetter(BuyTagListing::xp),
                    ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("price_multiplier", 1F).forGetter(BuyTagListing::priceMult),
                    Codec.intRange(1, 5).optionalFieldOf("level", 1).forGetter(BuyTagListing::level)
            ).apply(builder, BuyTagListing::new)
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
        return registry.getOrCreateTag(want)
                .getRandomElement(random)
                .map(Holder::value)
                .map(ItemCost::new)
                .map(want -> new MerchantOffer(want, offer, maxTrades, xp, priceMult))
                .orElse(null);
    }

}
