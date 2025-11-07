package galena.doom_and_gloom.fabric;

import galena.doom_and_gloom.client.DoomAndGloomClient;
import galena.doom_and_gloom.client.fog.FogRendering;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class FabricClientEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DoomAndGloomClient.init();

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            FogRendering.clientTick();
        });
    }

}
