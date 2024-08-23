package galena.oreganized.network;

import galena.oreganized.Oreganized;
import galena.oreganized.network.packet.GargoyleParticlePacket;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class OreganizedNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            Oreganized.modLoc("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;

        CHANNEL.registerMessage(id++, GargoyleParticlePacket.class, GargoyleParticlePacket::write, GargoyleParticlePacket::from, GargoyleParticlePacket::handle);
    }
}
