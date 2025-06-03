package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.content.block.StoneTabletBlock;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record EngraveStoneTabletPacket(BlockPos pos) {

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            StoneTabletBlock.openScreen(pos);
        });

        context.setPacketHandled(true);
    }

    public static EngraveStoneTabletPacket from(FriendlyByteBuf buffer) {
        var pos = buffer.readBlockPos();
        return new EngraveStoneTabletPacket(pos);
    }

}
