package galena.doom_and_gloom.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.DGReloadListener;
import galena.doom_and_gloom.content.block.StoneTabletBlock;
import galena.doom_and_gloom.content.block.StoneTabletBlockEntity;
import galena.doom_and_gloom.content.block.StoneTabletText;
import galena.doom_and_gloom.index.DGItems;
import galena.doom_and_gloom.network.DGNetwork;
import galena.doom_and_gloom.network.packet.StoneTabletUpdatePacket;
import java.util.stream.IntStream;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

//Based off of AbstractSignEditScreen
public class StoneTabletScreen extends Screen {

    private static final ResourceLocation BACKGROUND = DoomAndGloom.modLoc("textures/gui/tablet.png");

    /**
     * Reference to the sign object.
     */
    private final StoneTabletBlockEntity tablet;
    private final int maxLines;
    private final boolean canEdit;
    private StoneTabletText text;
    private final String[] messages;
    /**
     * Counts the number of screen updates.
     */
    private int frame;
    /**
     * The index of the line that is being edited.
     */
    private int line;
    @Nullable
    private TextFieldHelper signField;
    private boolean engraveOnClose = false;

    public StoneTabletScreen(StoneTabletBlockEntity tablet, ItemStack stack, boolean isFiltered) {
        super(Component.empty());
        this.tablet = tablet;
        this.text = tablet.getText();
        this.maxLines = tablet.getText().getLineCount();
        this.messages = IntStream.range(0, maxLines)
                .mapToObj(i -> this.text.getMessage(i, isFiltered))
                .map(Component::getString).toArray(String[]::new);

        this.canEdit = tablet.type == StoneTabletBlock.Type.DEFAULT && stack.is(DGItems.BUSH_HAMMER.get());
    }

    @Override
    protected void init() {
        if (!this.canEdit) {
            this.addRenderableWidget(
                    Button.builder(CommonComponents.GUI_DONE, button -> this.onDone())
                            .bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build()
            );
        } else {
            this.addRenderableWidget(Button.builder(Component.translatable("gui.doom_and_gloom.stone_tablet.engrave"), (p_98177_) -> {
                this.onEngrave();
            }).bounds(this.width / 2 - 100, 196, 98, 20).build());
            this.addRenderableWidget(Button.builder(Component.translatable("gui.doom_and_gloom.stone_tablet.cancel"), (p_280851_) -> {
                this.onDone();
            }).bounds(this.width / 2 + 2, 196, 98, 20).build());
        }

        this.signField = new TextFieldHelper(
                () -> this.messages[this.line],
                this::setMessage,
                TextFieldHelper.createClipboardGetter(this.minecraft),
                TextFieldHelper.createClipboardSetter(this.minecraft),
                string -> this.minecraft.font.width(string) <= this.tablet.getMaxTextLineWidth()
        );
    }

    @Override
    public void tick() {
        if (canEdit) ++this.frame;
        if (!this.isValid()) {
            this.onDone();
        }
    }

    private boolean isValid() {
        return this.minecraft != null
                && this.minecraft.player != null
                && !this.tablet.isRemoved()
                && !this.tablet.playerIsTooFarAwayToEdit(this.minecraft.player.getUUID());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.canEdit) return super.keyPressed(keyCode, scanCode, modifiers);
        if (keyCode == 265) {
            this.line = (this.line - 1 + maxLines) % maxLines;
            this.signField.setCursorToEnd();
            return true;
        } else if (keyCode == 264 || keyCode == 257 || keyCode == 335) {
            this.line = (this.line + 1) % maxLines;
            this.signField.setCursorToEnd();
            return true;
        } else {
            return this.signField.keyPressed(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (canEdit) this.signField.charTyped(codePoint);
        return canEdit;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Lighting.setupForFlatItems();
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);
        this.renderSign(guiGraphics);
        Lighting.setupFor3DItems();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.onDone();
    }

    @Override
    public void removed() {
        if (engraveOnClose) {
            DGNetwork.CHANNEL.sendToServer(new StoneTabletUpdatePacket(tablet.getBlockPos(), messages));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void renderSign(GuiGraphics guiGraphics) {

        guiGraphics.pose().pushPose();

        int imageWidth = 152;
        int imageHeight = 184;
        guiGraphics.pose().translate((float) this.width / 2.0F, 100.0F, 50.0F);

        guiGraphics.blit(BACKGROUND, -imageWidth / 2, -imageHeight / 2,
                0, 0, imageWidth, imageHeight);


        this.renderText(guiGraphics);
        guiGraphics.pose().popPose();
    }

    private void renderText(GuiGraphics guiGraphics) {
        guiGraphics.pose().translate(0.0F, -5.0F, 4.0F);

        int color = DGReloadListener.getColor();
        int darkColor2 = DGReloadListener.getLightColor();
        int darkColor = DGReloadListener.getDarkColor();
        boolean showCursor = this.frame / 6 % 2 == 0 && canEdit;
        int j = this.signField.getCursorPos();
        int k = this.signField.getSelectionPos();
        int l = maxLines * this.tablet.getTextLineHeight() / 2;
        int m = this.line * this.tablet.getTextLineHeight() - l;

        for (int n = 0; n < this.messages.length; ++n) {
            String string = this.messages[n];
            if (string != null) {
                if (this.font.isBidirectional()) {
                    string = this.font.bidirectionalShaping(string);
                }

                int pX = -this.font.width(string) / 2 - 1;
                int pY = n * this.tablet.getTextLineHeight() - l;
                drawEngravedString(guiGraphics, string, pX, pY, color, darkColor2, darkColor);


                if (n == this.line && j >= 0 && showCursor) {
                    int p = this.font.width(string.substring(0, Math.max(Math.min(j, string.length()), 0)));
                    int q = p - this.font.width(string) / 2;
                    if (j >= string.length()) {
                        guiGraphics.drawString(this.font, "_", q, m, darkColor, false);
                    }
                }
            }
        }

        for (int n = 0; n < this.messages.length; ++n) {
            String string = this.messages[n];
            if (string != null && n == this.line && j >= 0) {
                int o = this.font.width(string.substring(0, Math.max(Math.min(j, string.length()), 0)));
                int p = o - this.font.width(string) / 2;
                if (showCursor && j < string.length()) {
                    guiGraphics.fill(p, m - 1, p + 1, m + this.tablet.getTextLineHeight(), 0xFF000000 | color);
                }

                if (k != j) {
                    int q = Math.min(j, k);
                    int r = Math.max(j, k);
                    int s = this.font.width(string.substring(0, q)) - this.font.width(string) / 2;
                    int t = this.font.width(string.substring(0, r)) - this.font.width(string) / 2;
                    int u = Math.min(s, t);
                    int v = Math.max(s, t);
                    guiGraphics.fill(RenderType.guiTextHighlight(), u, m, v, m + this.tablet.getTextLineHeight(), -16776961);
                }
            }
        }
    }

    private void drawEngravedString(GuiGraphics guiGraphics, String string, int pX, int pY, int normal, int dark, int light) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        guiGraphics.drawString(this.font, string, pX, pY, normal, false);
        pose.translate(0, 0, -1);

        guiGraphics.drawString(this.font, string, pX - 1, pY - 1, dark, false);
        guiGraphics.drawString(this.font, string, pX, pY - 1, dark, false);
        guiGraphics.drawString(this.font, string, pX - 1, pY, dark, false);

        pose.translate(0, 0, -1);

        guiGraphics.drawString(this.font, string, pX + 1, pY + 1, light, false);
        guiGraphics.drawString(this.font, string, pX + 1, pY, light, false);
        guiGraphics.drawString(this.font, string, pX, pY + 1, light, false);
        guiGraphics.drawString(this.font, string, pX + 2, pY + 1, light, false);
        guiGraphics.drawString(this.font, string, pX - 1, pY + 1, light, false);
        guiGraphics.drawString(this.font, string, pX - 1, pY, light, false);
        guiGraphics.drawString(this.font, string, pX - 1, pY - 1, light, false);
        pose.popPose();
    }

    private void setMessage(String message) {
        this.messages[this.line] = message;
        this.text = this.text.withMessage(this.line, Component.literal(message));
    }

    private void onDone() {
        this.minecraft.setScreen(null);
    }

    private void onEngrave() {
        //TODO: Engrave sound
        this.tablet.setText(this.text);
        this.engraveOnClose = true;
        this.minecraft.setScreen(null);
    }
}
