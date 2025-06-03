package galena.doom_and_gloom.world.gen;

import com.mojang.datafixers.util.Pair;
import galena.doom_and_gloom.DoomAndGloom;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

// Thanks to TelepathicGrunt
public class VillageStructureModifier {
    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(
            Registries.PROCESSOR_LIST, new ResourceLocation("empty"));
    private static final ResourceKey<StructureProcessorList> MOSSY_PROCESSOR_LIST_KEY = ResourceKey.create(
            Registries.PROCESSOR_LIST, new ResourceLocation("mossify_10_percent"));

    private static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry,
                                          ResourceLocation poolRL,
                                          String nbtPieceRL,
                                          Holder<StructureProcessorList> processors,
                                          int weight) {

        StructureTemplatePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null) {
            return;
        }

        SinglePoolElement piece = SinglePoolElement.legacy(nbtPieceRL, processors).apply(StructureTemplatePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.rawTemplates);
        listOfPieceEntries.add(new Pair<>(piece, weight));
        pool.rawTemplates = listOfPieceEntries;
    }

    public static void setup(RegistryAccess registryAccess) {
        DoomAndGloom.LOGGER.info("Injecting Gravetender Village Houses");

        Registry<StructureTemplatePool> templatePoolRegistry = registryAccess.registry(Registries.TEMPLATE_POOL).orElseThrow();
        Registry<StructureProcessorList> processorListRegistry = registryAccess.registry(Registries.PROCESSOR_LIST).orElseThrow();

        addStandardHouses(templatePoolRegistry, processorListRegistry,
                "taiga", true, 1, 1, 1);

        addStandardHouses(templatePoolRegistry, processorListRegistry,
                "plains", false, 1, 1, 1);

        addStandardHouses(templatePoolRegistry, processorListRegistry,
                "savanna", false, 1, 1, 1);

        addStandardHouses(templatePoolRegistry, processorListRegistry,
                "snowy", false, 1, 1, 1);

        addStandardHouses(templatePoolRegistry, processorListRegistry,
                "desert", false, 10, 10, 10);

    }

    private static void addStandardHouses(Registry<StructureTemplatePool> reg,
                                          Registry<StructureProcessorList> list,
                                          String villageType, boolean mossy,
                                          int weightSmall, int weightNormal, int weightBig) {
        addVillageHouse(reg, list,
                villageType, "doom_and_gloom:village/graveyard/" + villageType + "_small", mossy, weightSmall);
        addVillageHouse(reg, list,
                villageType, "doom_and_gloom:village/graveyard/" + villageType + "_medium", mossy, weightNormal);
        addVillageHouse(reg, list,
                villageType, "doom_and_gloom:village/graveyard/" + villageType + "_large", mossy, weightBig);
    }

    private static void addVillageHouse(Registry<StructureTemplatePool> templatePoolRegistry,
                                        Registry<StructureProcessorList> processorListRegistry,
                                        String villageName, String pieceName,
                                        boolean mossy, int weight) {

        Holder<StructureProcessorList> normalProcessor =
                mossy ? processorListRegistry.getHolderOrThrow(MOSSY_PROCESSOR_LIST_KEY) :
                        processorListRegistry.getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY);

        Holder<StructureProcessorList> zombieProcessor = processorListRegistry.getHolderOrThrow(ResourceKey.create(
                Registries.PROCESSOR_LIST, new ResourceLocation("zombie_" + villageName)
        ));

        addBuildingToPool(templatePoolRegistry, new ResourceLocation("village/" + villageName + "/houses"),
                pieceName, normalProcessor, weight);

        addBuildingToPool(templatePoolRegistry, new ResourceLocation("village/" + villageName + "/zombie/houses"),
                pieceName, zombieProcessor, weight);
    }


}