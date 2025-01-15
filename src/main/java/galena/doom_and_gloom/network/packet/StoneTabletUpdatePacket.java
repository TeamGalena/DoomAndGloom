package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.content.block.StoneTabletBlock;
import galena.doom_and_gloom.content.block.StoneTabletBlockEntity;
import galena.doom_and_gloom.index.OBlocks;
import galena.doom_and_gloom.index.OSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record StoneTabletUpdatePacket(BlockPos pos, String[] lines) {

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(lines.length);
        for (var line : lines) {
            buffer.writeUtf(line);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            // text filtering yay
            var sender = context.getSender();
            CompletableFuture.supplyAsync(() ->
                    Stream.of(lines)
                            .map(ChatFormatting::stripFormatting)
                            .map(innerList ->
                                    sender.connection.filterTextPacket(innerList))
                            .map(CompletableFuture::join)
                            .toList()
            ).thenAcceptAsync((l) -> {
                this.updateSignText(sender, l);
            }, sender.server);
        });

        context.setPacketHandled(true);
    }

    private void updateSignText(ServerPlayer player, List<FilteredText> filteredText) {
        player.resetLastActionTime();
        Level level = player.level();

        if (level.hasChunkAt(pos) && level.getBlockEntity(pos) instanceof StoneTabletBlockEntity te) {
            var engraved = filteredText.stream().anyMatch(text -> !text.filteredOrEmpty().isBlank());
            te.updateStoneTabletText(player, filteredText);

            if (engraved) {
                level.setBlockAndUpdate(pos, OBlocks.ENGRAVED_STONE_TABLET.get().withPropertiesOf(level.getBlockState(pos)));
                level.playSound(null, pos, OSoundEvents.STONE_TABLET_ENGRAVE.get(),
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(player.getUsedItemHand(), ItemStack.EMPTY);
                }
            }
        }
    }


    public static StoneTabletUpdatePacket from(FriendlyByteBuf buffer) {
        var pos = buffer.readBlockPos();
        var lines = new String[buffer.readVarInt()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = buffer.readUtf();
        }
        return new StoneTabletUpdatePacket(pos, lines);
    }

}
