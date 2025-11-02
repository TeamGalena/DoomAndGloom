package galena.doom_and_gloom;

import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;

import java.util.function.Supplier;

public class DGConfig {
    public static final Common COMMON;
    private static final ConfigSpec COMMON_SPEC;

    public static final Client CLIENT;
    private static final ConfigSpec CLIENT_SPEC;

    public static void init() {
        //just classloads this
    }

    public static class Common {
        public final Supplier<Integer> sepulcherDuration;

        private Common(ConfigBuilder builder) {
            builder.comment("Common");
            builder.push("common");

            sepulcherDuration = builder.comment("Time in ticks the sepulcher takes to turn meat into bones")
                    .define("sepulcherDuration", 20 * 30, 0, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static class Client {
        public final Supplier<Boolean> fancyRenderType;

        private Client(ConfigBuilder builder) {
            builder.comment("Client");
            builder.push("client");

            fancyRenderType = builder.comment("Use fancy render type for hollers")
                    .define("glowy_render_type", false);

            builder.pop();
        }
    }

    static {

        ConfigBuilder commonBuilder = ConfigBuilder.create(DoomAndGloom.MOD_ID, ConfigType.COMMON);

        COMMON = new Common(commonBuilder);
        COMMON_SPEC = commonBuilder.buildAndRegister();

        ConfigBuilder clientBuilder = ConfigBuilder.create(DoomAndGloom.MOD_ID, ConfigType.CLIENT);
        CLIENT = new Client(clientBuilder);
        CLIENT_SPEC = clientBuilder.buildAndRegister();
    }

}
