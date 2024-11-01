package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.client.screen.StoneTabletScreen;
import galena.doom_and_gloom.content.block.StoneTabletBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record EngraveStoneTabletPacket(BlockPos pos) {

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            var level = mc.level;
            if(level != null && level.getBlockEntity(pos) instanceof StoneTabletBlockEntity tile) {
              mc.setScreen(new StoneTabletScreen(tile, mc.isTextFilteringEnabled()));
            }
        });

        context.setPacketHandled(true);
    }

    public static EngraveStoneTabletPacket from(FriendlyByteBuf buffer) {
        var pos = buffer.readBlockPos();
        return new EngraveStoneTabletPacket(pos);
    }

}
