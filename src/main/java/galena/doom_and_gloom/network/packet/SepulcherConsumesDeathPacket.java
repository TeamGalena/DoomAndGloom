package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.content.block.SepulcherBlock;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public record SepulcherConsumesDeathPacket(Vec3 at) {

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVector3f(at.toVector3f());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            SepulcherBlock.spawnConsumeParticles(at);
        });

        context.setPacketHandled(true);
    }

    public static SepulcherConsumesDeathPacket from(FriendlyByteBuf buffer) {
        var at = new Vec3(buffer.readVector3f());
        return new SepulcherConsumesDeathPacket(at);
    }

}
