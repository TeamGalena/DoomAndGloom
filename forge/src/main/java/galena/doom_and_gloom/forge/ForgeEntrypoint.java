package galena.doom_and_gloom.forge;

import com.mojang.serialization.Codec;
import galena.doom_and_gloom.DGConfig;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.compat.CompatMods;
import galena.doom_and_gloom.content.entity.SepulcherBlockEntity;
import galena.doom_and_gloom.content.entity.holler.Holler;
import galena.doom_and_gloom.forge.compat.AmendmentsCompat;
import galena.doom_and_gloom.gen.VillageStructureModifier;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGEntityTypes;
import galena.doom_and_gloom.index.DGItems;
import java.util.function.Supplier;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(DoomAndGloom.MOD_ID)
public class ForgeEntrypoint {

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DoomAndGloom.MOD_ID);

    // TODO can maybe be replaced with moonlights modifier?
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM = LOOT_MODIFIERS.register("add_item", () -> AddItemLootModifier.CODEC);

    public ForgeEntrypoint() {
        DoomAndGloom.init();

        if (PlatHelper.isModLoaded(CompatMods.AMENDMENTS)) {
            AmendmentsCompat.register();
        }

        registerConfigs();

        var modBus = EventBusSubscriber.Bus.MOD.bus().get();
        var forgeBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(this::buildCreativeModeTabContents);
        modBus.addListener(this::registerAttributes);
        modBus.addListener(this::registerSpawnPlacements);
        forgeBus.addListener(this::onServerStart);
        forgeBus.addListener(this::onLivingDrops);

        LOOT_MODIFIERS.register(modBus);
    }

    private void registerConfigs() {
        var context = ModLoadingContext.get();
        DGConfig.register(context::registerConfig);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(DGEntityTypes.HOLLER.get(), Holler.createAttributes().build());
    }

    private void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(DGEntityTypes.HOLLER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Holler::checkHollerSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }


    public void onServerStart(ServerAboutToStartEvent event) {
        VillageStructureModifier.setup(event.getServer().registryAccess());
    }

    public void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tab = event.getTabKey();
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();

        putAfter(entries, Blocks.LANTERN, DGBlocks.VIGIL_CANDLE);
        DGBlocks.COLORED_VIGIL_CANDLES.forEach((color, block) -> putAfter(entries, DGBlocks.VIGIL_CANDLE.get(), block));
        putAfter(entries, Blocks.COMPOSTER, DGBlocks.SEPULCHER);

        putAfter(entries, Blocks.BONE_BLOCK, DGBlocks.BONE_PILE);

        putAfter(entries, Blocks.INFESTED_DEEPSLATE, DGBlocks.BURIAL_DIRT);

        if (tab == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(new ItemStack(DGItems.HOLLER_SPAWN_EGG.get()));
        }

        //if (tab == CreativeModeTabs.TOOLS_AND_UTILITIES) {
        //    event.accept(new ItemStack(OItems.BUSH_HAMMER.get()));
        //    event.accept(new ItemStack(OItems.HAMMER_AND_CHISEL.get()));
        //}

        //if (tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
        //    event.accept(new ItemStack(OBlocks.STONE_TABLET.get()));
        //    event.accept(new ItemStack(OBlocks.CRACKED_STONE_TABLET.get()));
        //}
    }

    private static void putAfter(MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries, ItemLike after, Supplier<? extends ItemLike> supplier) {
        ItemLike key = supplier.get();
        if (!entries.contains(new ItemStack(after))) return;
        entries.putAfter(new ItemStack(after), new ItemStack(key), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    private void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player) return;
        if (SepulcherBlockEntity.wasConsumerBySepulcher(event.getEntity())) {
            event.setCanceled(true);
        }
    }

}
