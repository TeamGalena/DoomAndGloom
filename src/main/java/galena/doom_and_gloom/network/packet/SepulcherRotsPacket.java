package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.content.block.SepulcherBlock;
import java.awt.*;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SepulcherRotsPacket(BlockPos at) {

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(at);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            SepulcherBlock.spawnRottingParticles(at);
        });

        context.setPacketHandled(true);
    }

    public static SepulcherRotsPacket from(FriendlyByteBuf buffer) {
        var at = buffer.readBlockPos();
        return new SepulcherRotsPacket(at);
    }

}
