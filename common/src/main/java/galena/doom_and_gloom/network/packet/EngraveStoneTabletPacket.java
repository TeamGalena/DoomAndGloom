package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.DoomAndGloomClient;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EngraveStoneTabletPacket(BlockPos pos) implements Message {

    public static final TypeAndCodec<RegistryFriendlyByteBuf, EngraveStoneTabletPacket> TYPE = Message.makeType(
            DoomAndGloom.modLoc("stone_tablet_engrave"),
            EngraveStoneTabletPacket::from
    );

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void handle(Context context) {
        DoomAndGloomClient.openStoneTabletScreen(pos);
    }

    public static EngraveStoneTabletPacket from(RegistryFriendlyByteBuf buffer) {
        var pos = buffer.readBlockPos();
        return new EngraveStoneTabletPacket(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }
}
