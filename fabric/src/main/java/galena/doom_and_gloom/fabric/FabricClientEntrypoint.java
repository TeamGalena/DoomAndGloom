package galena.doom_and_gloom.fabric;

import galena.doom_and_gloom.client.DoomAndGloomClient;
import net.fabricmc.api.ClientModInitializer;

public class FabricClientEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DoomAndGloomClient.init();
    }

}
