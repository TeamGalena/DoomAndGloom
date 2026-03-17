package galena.doom_and_gloom.index;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.listing.BuyTagListing;
import galena.doom_and_gloom.content.listing.SellEnchantedListing;
import galena.doom_and_gloom.content.listing.SellTagListing;
import net.mehvahdjukaar.moonlight.api.trades.ItemListingManager;

public class DGItemListings {

    public static void init() {
        ItemListingManager.registerSerializer(DoomAndGloom.modLoc("buy_tag"), BuyTagListing.CODEC);
        ItemListingManager.registerSerializer(DoomAndGloom.modLoc("sell_tag"), SellTagListing.CODEC);
        ItemListingManager.registerSerializer(DoomAndGloom.modLoc("sell_enchanted"), SellEnchantedListing.CODEC);
    }

}
