package galena.doom_and_gloom.index;

import galena.doom_and_gloom.DoomAndGloom;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.resources.ResourceLocation;

public class DGLootInjects {

    public static void init() {
        RegHelper.addLootTableInjects(DGLootInjects::injectLootTables);
    }

    //TODO: add repurposed structures or similar compat here
    private static final ResourceLocation PYRAMID = new ResourceLocation("chests/desert_pyramid");
    private static final ResourceLocation PYRAMID_BONES = DoomAndGloom.modLoc("inject/desert_pyramid_bone_pile");

    private static void injectLootTables(RegHelper.LootInjectEvent event) {
        if (event.getTable().equals(PYRAMID))
            event.addTableReference(PYRAMID_BONES);

    }
}
