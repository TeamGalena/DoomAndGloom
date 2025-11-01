package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.content.block.SepulcherBlock;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record SepulcherRotsPacket(BlockPos at) implements Message {

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(at);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        SepulcherBlock.spawnRottingParticles(at);
    }

    public static SepulcherRotsPacket from(FriendlyByteBuf buffer) {
        var at = buffer.readBlockPos();
        return new SepulcherRotsPacket(at);
    }

}
