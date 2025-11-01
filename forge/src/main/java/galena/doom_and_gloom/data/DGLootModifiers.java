package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.forge.AddItemLootModifier;
import galena.doom_and_gloom.index.DGBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class DGLootModifiers extends GlobalLootModifierProvider {

    public DGLootModifiers(PackOutput output) {
        super(output, DoomAndGloom.MOD_ID);
    }

    @Override
    protected void start() {
        add(
                "bone_piles_in_desert_temples",
                new AddItemLootModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(BuiltInLootTables.DESERT_PYRAMID).build(),
                        LootItemRandomChanceCondition.randomChance(0.4F).build()
                }, new ItemStack(DGBlocks.BONE_PILE.get(), 2))
        );
    }

}
