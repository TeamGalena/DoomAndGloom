package galena.doom_and_gloom;

import galena.doom_and_gloom.compat.AmendmentsCompat;
import galena.doom_and_gloom.compat.CompatMods;
import galena.doom_and_gloom.content.entity.SepulcherBlockEntity;
import galena.doom_and_gloom.gen.VillageStructureModifier;
import galena.doom_and_gloom.index.*;
import galena.doom_and_gloom.network.DGNetwork;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DoomAndGloom {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "doom_and_gloom";

    public static ResourceLocation modLoc(String location) {
        return new ResourceLocation(MOD_ID, location);
    }

    public static void init() {
        DGConfig.init();
        DGBlockEntities.init();
        DGBlocks.init();
        DGEffects.init();
        DGEntityTypes.init();
        DGItems.init();
        DGParticleTypes.init();
        DGPoi.init();
        DGSoundEvents.init();
        DGVillagerTypes.init();
        DGItemListings.init();

        if (CompatMods.AMENDMENTS) AmendmentsCompat.init();

        DGNetwork.register();

        RegHelper.addItemsToTabsRegistration(DoomAndGloom::addItemsToTabs);
    }


    public static void addItemsToTabs(RegHelper.ItemToTabEvent event) {

        List<ItemLike> allCandles = new ArrayList<>();
        allCandles.add(DGBlocks.VIGIL_CANDLE.get());
        DGBlocks.COLORED_VIGIL_CANDLES.forEach((color, block) -> allCandles.add(block.get()));

        event.addAfter(CreativeModeTabs.BUILDING_BLOCKS, stack -> stack.is(DGTags.Items.VANILLA_LANTERNS),
                allCandles.toArray(ItemLike[]::new));

        event.addAfter(CreativeModeTabs.BUILDING_BLOCKS, i -> i.is(Items.BONE_BLOCK), DGBlocks.BONE_PILE.get());

        event.addAfter(CreativeModeTabs.BUILDING_BLOCKS, i -> i.is(Items.INFESTED_DEEPSLATE), DGBlocks.BURIAL_DIRT.get());

        event.add(CreativeModeTabs.SPAWN_EGGS, DGItems.HOLLER_SPAWN_EGG.get());

        //if (tab == CreativeModeTabs.TOOLS_AND_UTILITIES) {
        //    event.accept(new ItemStack(OItems.BUSH_HAMMER.get()));
        //    event.accept(new ItemStack(OItems.HAMMER_AND_CHISEL.get()));
        //}

        //if (tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
        //    event.accept(new ItemStack(OBlocks.STONE_TABLET.get()));
        //    event.accept(new ItemStack(OBlocks.CRACKED_STONE_TABLET.get()));
        //}
    }

    public static void onServerAboutToStart(MinecraftServer server) {
        VillageStructureModifier.setup(server.registryAccess());
    }

    //TODO: fabric
    public static boolean onItemDrop(Entity entity) {
        if (entity instanceof Player) return false;
        return SepulcherBlockEntity.wasConsumerBySepulcher(entity);
    }

}
