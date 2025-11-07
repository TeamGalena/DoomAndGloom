package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.client.DoomAndGloomClient;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record EngraveStoneTabletPacket(BlockPos pos) implements Message {

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        DoomAndGloomClient.openStoneTabletScreen(pos);
    }

    public static EngraveStoneTabletPacket from(FriendlyByteBuf buffer) {
        var pos = buffer.readBlockPos();
        return new EngraveStoneTabletPacket(pos);
    }

}
