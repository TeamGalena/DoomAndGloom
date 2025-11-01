package galena.doom_and_gloom.network;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.network.packet.EngraveStoneTabletPacket;
import galena.doom_and_gloom.network.packet.SepulcherConsumesDeathPacket;
import galena.doom_and_gloom.network.packet.SepulcherRotsPacket;
import galena.doom_and_gloom.network.packet.StoneTabletUpdatePacket;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkDir;

public class DGNetwork {

    public static final ChannelHandler CHANNEL = ChannelHandler.builder(DoomAndGloom.MOD_ID)
            .version(2)
            .register(NetworkDir.PLAY_TO_CLIENT, SepulcherConsumesDeathPacket.class, SepulcherConsumesDeathPacket::from)
            .register(NetworkDir.PLAY_TO_CLIENT, SepulcherRotsPacket.class, SepulcherRotsPacket::from)
            .register(NetworkDir.PLAY_TO_SERVER, StoneTabletUpdatePacket.class, StoneTabletUpdatePacket::from)
            .register(NetworkDir.PLAY_TO_CLIENT, EngraveStoneTabletPacket.class, EngraveStoneTabletPacket::from)
            .build();

    public static void register() {
        // Loads this class
    }

}