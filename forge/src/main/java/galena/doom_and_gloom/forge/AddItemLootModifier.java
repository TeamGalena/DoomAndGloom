package galena.doom_and_gloom.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

// TODO convert to bone pile only modifier?
// in 1.21 forge has a builtin add item one
public class AddItemLootModifier extends LootModifier {

    public static final Codec<AddItemLootModifier> CODEC = RecordCodecBuilder.create(builder ->
        codecStart(builder).and(
                ItemStack.CODEC.fieldOf("item").forGetter((AddItemLootModifier it) -> it.stack)
        ).apply(builder, AddItemLootModifier::new)
    );

    private final ItemStack stack;

    public AddItemLootModifier(LootItemCondition[] conditions, ItemStack stack) {
        super(conditions);
        this.stack = stack;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.add(stack);
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
