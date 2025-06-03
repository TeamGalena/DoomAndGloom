package galena.doom_and_gloom;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber(modid = DoomAndGloom.MOD_ID)
public class DGConfig {
    public static final Common COMMON;
    private static final ForgeConfigSpec COMMON_SPEC;

    public static final Client CLIENT;
    private static final ForgeConfigSpec CLIENT_SPEC;

    public static class Common {
        public final ConfigValue<Integer> sepulcherDuration;

        private Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common");
            builder.push("common");

            sepulcherDuration = builder.comment("Time in ticks the sepulcher takes to turn meat into bones").defineInRange("sepulcherDuration", 20 * 30, 0, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static class Client {
        public final ConfigValue<Boolean> fancyRenderType;

        private Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client");
            builder.push("client");

            fancyRenderType = builder.comment("Use fancy render type for hollers")
                    .define("glowy_render_type", false);

            builder.pop();
        }
    }

    static {
        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);

        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();

        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
    }

    public static void register() {
        var context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
    }

}
