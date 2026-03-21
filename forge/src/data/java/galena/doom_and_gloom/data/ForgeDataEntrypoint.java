package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = DoomAndGloom.MOD_ID)
public class ForgeDataEntrypoint {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> future = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();
        boolean client = event.includeClient();
        boolean server = event.includeServer();

        var lang = new DGLang(output);

        generator.addProvider(client, new DGBlockStates(output, helper));
        generator.addProvider(client, new DGItemModels(output, helper));
        generator.addProvider(client, lang);
        generator.addProvider(client, new DGSoundDefinitions(output, helper));

        generator.addProvider(server, new DGRecipes(output, future));
        generator.addProvider(server, new DGLootTables(output, future));
        DGBlockTags blockTags = new DGBlockTags(output, future, helper);
        generator.addProvider(server, blockTags);
        generator.addProvider(server, new DGItemTags(output, future, blockTags.contentsGetter(), helper));
        generator.addProvider(server, new DGEntityTags(output, future, helper));
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(server, new DGDamageTags(output, lookupProvider, helper));
        generator.addProvider(server, new DGMobEffectTags(output, lookupProvider, helper));
        generator.addProvider(server, new DGItemListings(output, helper, future));

        generator.addProvider(server, new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal("Doom & Gloom resources"),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                Optional.empty()
        )));
    }

}
