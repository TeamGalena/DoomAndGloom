package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = DoomAndGloom.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ForgeDataEntrypoint {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
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

}
