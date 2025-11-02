package galena.doom_and_gloom.index;

import galena.doom_and_gloom.DoomAndGloom;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class DGTags {

    public static class Items {

        public static final TagKey<Item> INGOTS_SILVER = cTag("ingots/silver");
        public static final TagKey<Item> INGOTS_LEAD = cTag("ingots/lead");

        public static final TagKey<Item> TOOLS_BUSH_HAMMER = tag("tools/bush_hammer");

        public static final TagKey<Item> VIGIL_CANDLES = tag("vigil_candles");

        public static final TagKey<Item> VANILLA_LANTERNS = cTag("lanterns");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, DoomAndGloom.modLoc(name));
        }

        private static TagKey<Item> cTag(String name) {
            return TagKey.create(Registries.ITEM, cLoc(name));
        }
    }

    public static class Blocks {

        public static final TagKey<Block> MINEABLE_WITH_BUSH_HAMMER = tag("mineable/bush_hammer");
        public static final TagKey<Block> ENGRAVABLE = tag("engravable");
        public static final TagKey<Block> ENGRAVABLE_NEEDS_PLATE = tag("engravable/needs_plate");
        public static final TagKey<Block> HEAT_SOURCE = TagKey.create(Registries.BLOCK, new ResourceLocation("oreganized", "fire_source"));
        public static final TagKey<Block> VIGIL_CANDLES = tag("vigil_candles");
        public static final TagKey<Block> CAN_TURN_INTO_BURIAL_DIRT = tag("burial_dirt_convertible");
        public static final TagKey<Block> GRAVETENDER_LIGHTABLE = tag("gravetender_lightables");


        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, DoomAndGloom.modLoc(name));
        }

        private static TagKey<Block> cTag(String name) {
            return TagKey.create(Registries.BLOCK, cLoc(name));
        }
    }

    public static class Entities {

        public static final TagKey<EntityType<?>> FILLS_SEPULCHER = tag("fills_sepulcher");


        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, DoomAndGloom.modLoc(name));
        }

        private static TagKey<EntityType<?>> cTag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, cLoc(name));
        }
    }

    public static class Effects {

        public static final TagKey<MobEffect> VIGIL_CANDLE_CLEARS = tag("cleared_by_vigil_candle");

        private static TagKey<MobEffect> tag(String name) {
            return TagKey.create(Registries.MOB_EFFECT, DoomAndGloom.modLoc(name));
        }

    }

    private static @NotNull ResourceLocation cLoc(String name) {
        return new ResourceLocation(PlatHelper.getPlatform().isFabric() ? "c" : "forge", name);
    }
}
