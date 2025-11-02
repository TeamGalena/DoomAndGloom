package galena.doom_and_gloom.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import galena.doom_and_gloom.DGConfig;
import galena.doom_and_gloom.DoomAndGloom;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.mehvahdjukaar.moonlight.api.events.fabric.DropItemOnDeathEvent;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        DoomAndGloom.init();

        //if this doesnt work use server started
        ServerLifecycleEvents.SERVER_STARTING.register(DoomAndGloom::onServerAboutToStart);
    }


}
