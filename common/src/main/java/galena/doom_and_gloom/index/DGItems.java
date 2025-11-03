package galena.doom_and_gloom.index;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;

import galena.doom_and_gloom.content.item.BushHammerItem;
import galena.doom_and_gloom.content.item.HammerAndChiselItem;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.item.*;

public class DGItems {

    // Discs
    public static final RegSupplier<RecordItem> MUSIC_DISC_AFTERLIFE = register("music_disc_afterlife", () -> PlatHelper.newMusicDisc(13, DGSoundEvents.MUSIC_DISC_AFTERLIFE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 155));

    // Crafting Materials
    public static final RegSupplier<Item> BUSH_HAMMER = register("bush_hammer", () -> new BushHammerItem(DGItemTiers.LEAD, 2.5F, -2.8F, (new Item.Properties()).stacksTo(1)));
    public static final RegSupplier<Item> HAMMER_AND_CHISEL = register("hammer_and_chisel", () -> new HammerAndChiselItem(DGBlocks.STONE_TABLET.get()));

    public static final RegSupplier<Item> HOLLER_SPAWN_EGG = register("holler_spawn_egg", () ->
            PlatHelper.newSpawnEgg(DGEntityTypes.HOLLER, 0x84EED2, 0x24352F,new Item.Properties()));

    private static <T extends Item> RegSupplier<T> register(String name, Supplier<T> factory) {
        return RegHelper.registerItem(modLoc(name), factory);
    }

    public static void init() {
        // Loads this class
    }

}