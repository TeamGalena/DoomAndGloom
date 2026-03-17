package galena.doom_and_gloom.network;

import galena.doom_and_gloom.network.packet.EngraveStoneTabletPacket;
import galena.doom_and_gloom.network.packet.SepulcherConsumesDeathPacket;
import galena.doom_and_gloom.network.packet.SepulcherRotsPacket;
import galena.doom_and_gloom.network.packet.StoneTabletUpdatePacket;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;

public class DGNetwork {

    public static void register() {
        NetworkHelper.addNetworkRegistration(event -> {
            event.registerClientBound(SepulcherConsumesDeathPacket.TYPE);
            event.registerClientBound(SepulcherRotsPacket.TYPE);
            event.registerServerBound(StoneTabletUpdatePacket.TYPE);
            event.registerClientBound(EngraveStoneTabletPacket.TYPE);
        }, 3);
    }

}