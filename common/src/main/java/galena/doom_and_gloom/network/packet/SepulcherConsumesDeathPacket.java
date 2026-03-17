package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.DoomAndGloomClient;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public record SepulcherConsumesDeathPacket(Vec3 at) implements Message {

    public static final TypeAndCodec<RegistryFriendlyByteBuf, SepulcherConsumesDeathPacket> TYPE = Message.makeType(
            DoomAndGloom.modLoc("sepulcher_consumes"),
            SepulcherConsumesDeathPacket::from
    );

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVector3f(at.toVector3f());
    }

    @Override
    public void handle(Context context) {
        DoomAndGloomClient.spawnConsumeParticles(at);
    }

    public static SepulcherConsumesDeathPacket from(RegistryFriendlyByteBuf buffer) {
        var at = new Vec3(buffer.readVector3f());
        return new SepulcherConsumesDeathPacket(at);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }

}
