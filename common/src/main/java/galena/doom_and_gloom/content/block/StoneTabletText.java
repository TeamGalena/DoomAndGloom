package galena.doom_and_gloom.content.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

// once again copied from sign text. Just needed for the amount of lines
public class StoneTabletText {
    public static final int LINES = 13;

    private static final Codec<Component[]> LINES_CODEC = ComponentSerialization.FLAT_CODEC
            .listOf()
            .comapFlatMap(
                    list -> Util.fixedSize(list, LINES)
                            .map(listx -> listx.toArray(new Component[0])),
                    Arrays::asList
            );
    public static final Codec<StoneTabletText> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            LINES_CODEC.fieldOf("messages").forGetter(signText -> signText.messages),
                            LINES_CODEC.optionalFieldOf("filtered_messages").forGetter(StoneTabletText::getOnlyFilteredMessages)
                    )
                    .apply(instance, StoneTabletText::load)
    );
    private final Component[] messages;
    private final Component[] filteredMessages;
    @Nullable
    private FormattedCharSequence[] renderMessages;
    private boolean renderMessagedFiltered;

    public StoneTabletText() {
        this(emptyMessages(), emptyMessages());
    }

    public StoneTabletText(Component[] messages, Component[] filteredMessages) {
        this.messages = messages;
        this.filteredMessages = filteredMessages;
    }

    public int getLineCount() {
        return messages.length;
    }

    private static Component[] emptyMessages() {
        Component[] components = new Component[LINES];
        Arrays.fill(components, CommonComponents.EMPTY);
        return components;
    }

    private static StoneTabletText load(Component[] messages, Optional<Component[]> filteredMessages) {
        Component[] components = filteredMessages.orElseGet(StoneTabletText::emptyMessages);
        populateFilteredMessagesWithRawMessages(messages, components);
        return new StoneTabletText(messages, components);
    }

    private static void populateFilteredMessagesWithRawMessages(Component[] messages, Component[] filteredMessages) {
        for (int i = 0; i < LINES; ++i) {
            if (filteredMessages[i].equals(CommonComponents.EMPTY)) {
                filteredMessages[i] = messages[i];
            }
        }
    }

    public Component getMessage(int index, boolean isFiltered) {
        return this.getMessages(isFiltered)[index];
    }

    public StoneTabletText withMessage(int index, Component text) {
        return this.withMessage(index, text, text);
    }

    public StoneTabletText withMessage(int index, Component text, Component filteredText) {
        Component[] components = Arrays.copyOf(this.messages, this.messages.length);
        Component[] components2 = Arrays.copyOf(this.filteredMessages, this.filteredMessages.length);
        components[index] = text;
        components2[index] = filteredText;
        return new StoneTabletText(components, components2);
    }

    public boolean hasMessage(Player player) {
        return Arrays.stream(this.getMessages(player.isTextFilteringEnabled())).anyMatch(component -> !component.getString().isEmpty());
    }

    public Component[] getMessages(boolean isFiltered) {
        return isFiltered ? this.filteredMessages : this.messages;
    }

    public FormattedCharSequence[] getRenderMessages(boolean renderMessagesFiltered, Function<Component, FormattedCharSequence> formatter) {
        if (this.renderMessages == null || this.renderMessagedFiltered != renderMessagesFiltered) {
            this.renderMessagedFiltered = renderMessagesFiltered;
            this.renderMessages = new FormattedCharSequence[LINES];

            for (int i = 0; i < LINES; ++i) {
                this.renderMessages[i] = formatter.apply(this.getMessage(i, renderMessagesFiltered));
            }
        }

        return this.renderMessages;
    }

    private Optional<Component[]> getOnlyFilteredMessages() {
        Component[] components = new Component[LINES];
        boolean bl = false;

        for (int i = 0; i < LINES; ++i) {
            Component component = this.filteredMessages[i];
            if (!component.equals(this.messages[i])) {
                components[i] = component;
                bl = true;
            } else {
                components[i] = CommonComponents.EMPTY;
            }
        }

        return bl ? Optional.of(components) : Optional.empty();
    }

    public boolean hasAnyClickCommands(Player player) {
        for (Component component : this.getMessages(player.isTextFilteringEnabled())) {
            Style style = component.getStyle();
            ClickEvent clickEvent = style.getClickEvent();
            if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                return true;
            }
        }

        return false;
    }
}
