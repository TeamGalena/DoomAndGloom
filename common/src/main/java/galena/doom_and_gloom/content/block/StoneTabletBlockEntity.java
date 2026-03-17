package galena.doom_and_gloom.content.block;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.entity.Ticking;
import galena.doom_and_gloom.index.DGBlockEntities;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// shamelessly copied from vanilla sign block entity.
// Reason: vanilla has the number of lines hardcoded as shit
public class StoneTabletBlockEntity extends BlockEntity implements Ticking {
    private static final int MAX_TEXT_LINE_WIDTH = 120;
    private static final int TEXT_LINE_HEIGHT = 10;
    @Nullable
    private UUID playerWhoMayEdit;
    private StoneTabletText frontText = new StoneTabletText();
    public final StoneTabletBlock.Type type;

    public StoneTabletBlockEntity(BlockPos pos, BlockState blockState) {
        super(DGBlockEntities.STONE_TABLET.get(), pos, blockState);
        this.type = ((StoneTabletBlock) blockState.getBlock()).type;
    }

    public StoneTabletText getText() {
        return this.frontText;
    }

    public int getTextLineHeight() {
        return TEXT_LINE_HEIGHT;
    }

    public int getMaxTextLineWidth() {
        return MAX_TEXT_LINE_WIDTH;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        StoneTabletText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.frontText)
                .resultOrPartial(DoomAndGloom.LOGGER::error).ifPresent(tagx -> tag.put("front_text", tagx));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        if (tag.contains("text")) {
            StoneTabletText.DIRECT_CODEC
                    .parse(NbtOps.INSTANCE, tag.getCompound("text"))
                    .resultOrPartial(DoomAndGloom.LOGGER::error)
                    .ifPresent(signText -> this.frontText = this.loadLines(signText));
        }
    }

    //ugly ass code. tanks mojang. Not moving this as its easier to update as it matches the sign one
    private StoneTabletText loadLines(StoneTabletText text) {
        for (int i = 0; i < text.getLineCount(); ++i) {
            Component notFiltered = this.loadLine(text.getMessage(i, false));
            Component filtered = this.loadLine(text.getMessage(i, true));
            text = text.withMessage(i, notFiltered, filtered);
        }

        return text;
    }

    private Component loadLine(Component lineText) {
        if (this.level instanceof ServerLevel serverLevel) {
            try {
                return ComponentUtils.updateForEntity(createCommandSourceStack(null, serverLevel, this.worldPosition), lineText, null, 0);
            } catch (CommandSyntaxException ignored) {
            }
        }

        return lineText;
    }

    public void updateStoneTabletText(Player player, List<FilteredText> filteredText) {
        if (player.getUUID().equals(this.getPlayerWhoMayEdit()) && this.level != null) {
            this.updateText(signText -> this.setMessages(player, filteredText, signText));
            this.setAllowedPlayerEditor(null);
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        } else {
            DoomAndGloom.LOGGER.warn("Player {} just tried to change non-editable sign", player.getName().getString());
        }
    }

    public boolean updateText(UnaryOperator<StoneTabletText> updater) {
        StoneTabletText signText = this.getText();
        return this.setText(updater.apply(signText));
    }

    private StoneTabletText setMessages(Player player, List<FilteredText> filteredText, StoneTabletText text) {
        for (int i = 0; i < filteredText.size(); ++i) {
            FilteredText filteredText2 = filteredText.get(i);
            Style style = text.getMessage(i, player.isTextFilteringEnabled()).getStyle();
            if (player.isTextFilteringEnabled()) {
                text = text.withMessage(i, Component.literal(filteredText2.filteredOrEmpty()).setStyle(style));
            } else {
                text = text.withMessage(i, Component.literal(filteredText2.raw()).setStyle(style), Component.literal(filteredText2.filteredOrEmpty()).setStyle(style));
            }
        }

        return text;
    }

    public boolean setText(StoneTabletText text) {
        if (text != this.frontText) {
            this.frontText = text;
            this.markUpdated();
            return true;
        } else {
            return false;
        }
    }

    public static CommandSourceStack createCommandSourceStack(@Nullable Player player, Level level, BlockPos pos) {
        String string = player == null ? "Sign" : player.getName().getString();
        Component component = (player == null ? Component.literal("Sign") : player.getDisplayName());
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(pos), Vec2.ZERO, (ServerLevel) level, 2, string, component, level.getServer(), player);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public void setAllowedPlayerEditor(@Nullable UUID playWhoMayEdit) {
        this.playerWhoMayEdit = playWhoMayEdit;
    }

    @Nullable
    public UUID getPlayerWhoMayEdit() {
        return this.playerWhoMayEdit;
    }

    private void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public boolean playerIsTooFarAwayToEdit(UUID uuid) {
        Player player = this.level.getPlayerByUUID(uuid);
        return player == null
                || player.distanceToSqr(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ()) > 64.0;
    }

    @Override
    public void tick(BlockState state, Level level, BlockPos pos) {
        UUID uUID = this.getPlayerWhoMayEdit();
        if (uUID != null) {
            this.clearInvalidPlayerWhoMayEdit(level, uUID);
        }
    }

    private void clearInvalidPlayerWhoMayEdit(Level level, UUID uuid) {
        if (this.playerIsTooFarAwayToEdit(uuid)) {
            this.setAllowedPlayerEditor(null);
        }
    }
}
