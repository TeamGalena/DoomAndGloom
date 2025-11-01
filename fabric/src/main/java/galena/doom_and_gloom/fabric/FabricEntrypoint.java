package galena.doom_and_gloom.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import galena.doom_and_gloom.DGConfig;
import galena.doom_and_gloom.DoomAndGloom;
import net.fabricmc.api.ModInitializer;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        DoomAndGloom.init();
        registerConfigs();
    }

    private void registerConfigs() {
        DGConfig.register((type, spec) ->
                ForgeConfigRegistry.INSTANCE.register(DoomAndGloom.MOD_ID, type, spec)
        );
    }

}
