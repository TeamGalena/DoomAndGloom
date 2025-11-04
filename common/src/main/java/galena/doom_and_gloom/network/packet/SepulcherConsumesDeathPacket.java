package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.client.DoomAndGloomClient;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public record SepulcherConsumesDeathPacket(Vec3 at) implements Message {

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeVector3f(at.toVector3f());
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        DoomAndGloomClient.spawnConsumeParticles(at);
    }

    public static SepulcherConsumesDeathPacket from(FriendlyByteBuf buffer) {
        var at = new Vec3(buffer.readVector3f());
        return new SepulcherConsumesDeathPacket(at);
    }

}
