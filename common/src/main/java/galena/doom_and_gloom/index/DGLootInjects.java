package galena.doom_and_gloom.index;

import galena.doom_and_gloom.DoomAndGloom;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class DGLootInjects {

    public static void init() {
        RegHelper.addLootTableInjects(DGLootInjects::injectLootTables);
    }

    //TODO: add repurposed structures or similar compat here
    private static final Set<ResourceLocation> PYRAMIDS = Set.of(
            BuiltInLootTables.DESERT_PYRAMID.location()
    );

    public static final ResourceKey<LootTable> PYRAMID_BONES = ResourceKey.create(Registries.LOOT_TABLE, DoomAndGloom.modLoc("inject/desert_pyramid_bone_pile"));

    private static void injectLootTables(RegHelper.LootInjectEvent event) {
        if (PYRAMIDS.contains(event.getTable()))
            event.addTableReference(PYRAMID_BONES.location());

    }
}
