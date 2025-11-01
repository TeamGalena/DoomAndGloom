package galena.doom_and_gloom.index;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.listing.BuyTagListing;
import galena.doom_and_gloom.content.listing.SellTagListing;
import net.mehvahdjukaar.moonlight.api.trades.ItemListingRegistry;

public class DGItemListings {

    public static void init() {
        ItemListingRegistry.registerSerializer(DoomAndGloom.modLoc("buy_tag"), BuyTagListing.CODEC);
        ItemListingRegistry.registerSerializer(DoomAndGloom.modLoc("sell_tag"), SellTagListing.CODEC);
    }

}
