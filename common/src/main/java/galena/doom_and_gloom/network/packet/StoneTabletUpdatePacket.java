package galena.doom_and_gloom.network.packet;

import galena.doom_and_gloom.content.block.StoneTabletBlockEntity;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGSoundEvents;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public record StoneTabletUpdatePacket(BlockPos pos, String[] lines) implements Message {

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(lines.length);
        for (var line : lines) {
            buffer.writeUtf(line);
        }
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        if (!(context.getSender() instanceof ServerPlayer sender)) return;

        // text filtering yay
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
    }

    private void updateSignText(ServerPlayer player, List<FilteredText> filteredText) {
        player.resetLastActionTime();
        Level level = player.level();

        if (level.hasChunkAt(pos) && level.getBlockEntity(pos) instanceof StoneTabletBlockEntity te) {
            var engraved = filteredText.stream().anyMatch(text -> !text.filteredOrEmpty().isBlank());
            te.updateStoneTabletText(player, filteredText);

            if (engraved) {
                level.setBlockAndUpdate(pos, DGBlocks.ENGRAVED_STONE_TABLET.get().withPropertiesOf(level.getBlockState(pos)));
                level.playSound(null, pos, DGSoundEvents.STONE_TABLET_ENGRAVE.get(),
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
