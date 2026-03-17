package galena.doom_and_gloom.content.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.trades.ModItemListing;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

/**
 * TODO port
 * this can be done on 1.21.1 with the default {@link net.mehvahdjukaar.moonlight.api.trades.SimpleItemListing} and loot functions
 */
public record SellEnchantedListing(ItemCost want, ItemStack offer, int maxTrades, int xp, float priceMult,
                                   int level) implements ModItemListing {

    public static final MapCodec<SellEnchantedListing> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    ItemCost.CODEC.fieldOf("price").forGetter(SellEnchantedListing::want),
                    ItemStack.CODEC.fieldOf("offer").forGetter(SellEnchantedListing::offer),
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_trades", 16).forGetter(SellEnchantedListing::maxTrades),
                    ExtraCodecs.POSITIVE_INT.optionalFieldOf("xp", 1).forGetter(SellEnchantedListing::xp),
                    ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("price_multiplier", 1F).forGetter(SellEnchantedListing::priceMult),
                    Codec.intRange(1, 5).optionalFieldOf("level", 1).forGetter(SellEnchantedListing::level)
            ).apply(builder, SellEnchantedListing::new)
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
        int level = 5 + random.nextInt(15);
        var registryAccess = entity.level().registryAccess();
        var enchanted = EnchantmentHelper.enchantItem(random, offer.copy(), level, registryAccess, Optional.empty());
        return new MerchantOffer(want, enchanted, maxTrades, xp, priceMult);
    }

}
