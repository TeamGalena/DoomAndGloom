package galena.doom_and_gloom.fabric;

import galena.doom_and_gloom.client.DoomAndGloomClient;
import galena.doom_and_gloom.client.FogRendering;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

public class FabricClientEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DoomAndGloomClient.init();
        DoomAndGloomClient.setup();

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            FogRendering.clientTick();
        });
    }

}
