package galena.doom_and_gloom.content.listing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.moonlight.api.misc.StrOpt;
import net.mehvahdjukaar.moonlight.api.trades.ModItemListing;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;

/**
 * this can be done on 1.21.1 with the default {@link net.mehvahdjukaar.moonlight.api.trades.SimpleItemListing} and loot functions
 */
public record SellEnchantedListing(ItemStack want, ItemStack offer, int maxTrades, int xp, float priceMult,
                                   int level) implements ModItemListing {

    public static final Codec<SellEnchantedListing> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ItemStack.CODEC.fieldOf("price").forGetter(SellEnchantedListing::want),
                    ItemStack.CODEC.fieldOf("offer").forGetter(SellEnchantedListing::offer),
                    StrOpt.of(ExtraCodecs.POSITIVE_INT, "max_trades", 16).forGetter(SellEnchantedListing::maxTrades),
                    StrOpt.of(ExtraCodecs.POSITIVE_INT, "xp", 1).forGetter(SellEnchantedListing::xp),
                    StrOpt.of(ExtraCodecs.POSITIVE_FLOAT, "price_multiplier", 1F).forGetter(SellEnchantedListing::priceMult),
                    StrOpt.of(Codec.intRange(1, 5), "level", 1).forGetter(SellEnchantedListing::level)
            ).apply(builder, SellEnchantedListing::new)
    );

    @Override
    public Codec<? extends ModItemListing> getCodec() {
        return CODEC;
    }

    @Override
    public MerchantOffer getOffer(Entity entity, RandomSource random) {
        int level = 5 + random.nextInt(15);
        var  enchanted = EnchantmentHelper.enchantItem(random, offer.copy(), level, false);
        return new MerchantOffer(want, enchanted, maxTrades, xp, priceMult);
    }

}
