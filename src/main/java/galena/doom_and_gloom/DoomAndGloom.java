package galena.doom_and_gloom;

import com.mojang.serialization.Codec;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import galena.doom_and_gloom.compat.CompatHandler;
import galena.doom_and_gloom.compat.amendments.AmendmentsCompat;
import galena.doom_and_gloom.compat.moonlight.MoonlightCompat;
import galena.doom_and_gloom.content.entity.holler.Holler;
import galena.doom_and_gloom.data.OBlockStates;
import galena.doom_and_gloom.data.OBlockTags;
import galena.doom_and_gloom.data.ODamageTags;
import galena.doom_and_gloom.data.OEntityTags;
import galena.doom_and_gloom.data.OItemModels;
import galena.doom_and_gloom.data.OItemTags;
import galena.doom_and_gloom.data.OLang;
import galena.doom_and_gloom.data.OLootTables;
import galena.doom_and_gloom.data.OMobEffectTags;
import galena.doom_and_gloom.data.ORecipes;
import galena.doom_and_gloom.data.OSoundDefinitions;
import galena.doom_and_gloom.index.OBlocks;
import galena.doom_and_gloom.index.OEffects;
import galena.doom_and_gloom.index.OEntityTypes;
import galena.doom_and_gloom.index.OItems;
import galena.doom_and_gloom.index.OParticleTypes;
import galena.doom_and_gloom.index.OPoi;
import galena.doom_and_gloom.index.OVillagerTypes;
import galena.doom_and_gloom.network.DGNetwork;
import galena.doom_and_gloom.world.AddItemLootModifier;
import galena.doom_and_gloom.world.gen.VillageStructureModifier;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DoomAndGloom.MOD_ID)
public class DoomAndGloom {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "doom_and_gloom";

    public static ResourceLocation modLoc(String location) {
        return new ResourceLocation(MOD_ID, location);
    }

    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DoomAndGloom.MOD_ID);

    public DoomAndGloom() {
        final IEventBus modBus = Bus.MOD.bus().get();
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        DGConfig.register();

        modBus.addListener(this::gatherData);
        modBus.addListener(this::buildCreativeModeTabContents);
        modBus.addListener(this::registerAttributes);
        modBus.addListener(this::registerSpawnPlacements);
        forgeBus.addListener(this::onServerStart);

        LOOT_MODIFIERS.register("add_item", () -> AddItemLootModifier.CODEC);

        DeferredRegister<?>[] registers = {
                OEffects.EFFECTS,
                OEntityTypes.ENTITIES,
                OParticleTypes.PARTICLES,
                OPoi.POI_TYPES,
                OVillagerTypes.VILLAGER_PROFESSIONS,
                LOOT_MODIFIERS,
        };

        for (DeferredRegister<?> register : registers) {
            register.register(modBus);
        }

        REGISTRY_HELPER.register(modBus);

        DGNetwork.register();

        if (CompatHandler.AMENDMENTS) {
            AmendmentsCompat.register();
        }
        if (CompatHandler.MOONLIGHT) {
            MoonlightCompat.init();
        }
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(OEntityTypes.HOLLER.get(), Holler.createAttributes().build());
    }

    private void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(OEntityTypes.HOLLER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Holler::checkHollerSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> future = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();
        boolean client = event.includeClient();
        boolean server = event.includeServer();

        var lang = new OLang(output);

        generator.addProvider(client, new OBlockStates(output, helper));
        generator.addProvider(client, new OItemModels(output, helper));
        generator.addProvider(client, lang);
        generator.addProvider(client, new OSoundDefinitions(output, helper));

        generator.addProvider(server, new ORecipes(output));
        generator.addProvider(server, new OLootTables(output));
        OBlockTags blockTags = new OBlockTags(output, future, helper);
        generator.addProvider(server, blockTags);
        generator.addProvider(server, new OItemTags(output, future, blockTags.contentsGetter(), helper));
        generator.addProvider(server, new OEntityTags(output, future, helper));
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(server, new ODamageTags(output, lookupProvider, helper));
        generator.addProvider(server, new OMobEffectTags(output, lookupProvider, helper));

        generator.addProvider(server, new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal("Doom & Gloom resources"),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion))
        )));
    }


    public void onServerStart(ServerAboutToStartEvent event) {
        VillageStructureModifier.setup(event.getServer().registryAccess());
    }

    public void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tab = event.getTabKey();
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();

        putAfter(entries, Blocks.LANTERN, OBlocks.VIGIL_CANDLE);
        OBlocks.COLORED_VIGIL_CANDLES.forEach((color, block) -> putAfter(entries, OBlocks.VIGIL_CANDLE.get(), block));
        putAfter(entries, Blocks.COMPOSTER, OBlocks.SEPULCHER);

        putAfter(entries, Blocks.BONE_BLOCK, OBlocks.BONE_PILE);

        putAfter(entries, Blocks.INFESTED_DEEPSLATE, OBlocks.BURIAL_DIRT);

        if (tab == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(new ItemStack(OItems.HOLLER_SPAWN_EGG.get()));
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

}
