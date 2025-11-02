package galena.doom_and_gloom.index;

import galena.doom_and_gloom.content.entity.DirtMound;
import galena.doom_and_gloom.content.entity.holler.Holler;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Supplier;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;

public class DGEntityTypes {

    public static final RegSupplier<EntityType<Holler>> HOLLER = register("holler", () ->
            EntityType.Builder.of(Holler::new, MobCategory.MONSTER).sized(0.4F, 0.8F).clientTrackingRange(8).updateInterval(2)
    );

    public static final RegSupplier<EntityType<DirtMound>> DIRT_MOUND = register("dirt_mound", () ->
            EntityType.Builder.of(DirtMound::new, MobCategory.MISC).sized(0.8F, 0.25F)
    );

    private static <T extends Entity> RegSupplier<EntityType<T>> register(String name, Supplier<EntityType.Builder<T>> type) {
        return RegHelper.registerEntityType(modLoc(name), () -> type.get().build(name));
    }

    public static void init() {
        // Loads this class
        RegHelper.addAttributeRegistration(DGEntityTypes::registerAttributes);
        RegHelper.addSpawnPlacementsRegistration(DGEntityTypes::registerSpawnPlacements);
    }

    private static void registerSpawnPlacements(RegHelper.SpawnPlacementEvent event) {
        event.register(DGEntityTypes.HOLLER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Holler::checkHollerSpawnRules);
    }

    private static void registerAttributes(RegHelper.AttributeEvent event) {
        event.register(DGEntityTypes.HOLLER.get(), Holler.createAttributes());
    }


}
