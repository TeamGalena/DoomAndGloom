package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.block.SepulcherBlock;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SepulcherRotsPacket(BlockPos at) implements Message {

    public static final TypeAndCodec<RegistryFriendlyByteBuf, SepulcherRotsPacket> TYPE = Message.makeType(
            DoomAndGloom.modLoc("sepulcher_rots"),
            SepulcherRotsPacket::from
    );

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(at);
    }

    @Override
    public void handle(Context context) {
        SepulcherBlock.spawnRottingParticles(at);
    }

    public static SepulcherRotsPacket from(RegistryFriendlyByteBuf buffer) {
        var at = buffer.readBlockPos();
        return new SepulcherRotsPacket(at);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }
}
