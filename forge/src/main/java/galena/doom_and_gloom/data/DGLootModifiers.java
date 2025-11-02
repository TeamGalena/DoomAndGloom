package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class DGLootModifiers extends GlobalLootModifierProvider {

    public DGLootModifiers(PackOutput output) {
        super(output, DoomAndGloom.MOD_ID);
    }

    @Override
    protected void start() {
        /*
        add(
                "bone_piles_in_desert_temples",
                new ModLootModifiers.AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(BuiltInLootTables.DESERT_PYRAMID).build(),
                        LootItemRandomChanceCondition.randomChance(0.4F).build()
                }, new ItemStack(DGBlocks.BONE_PILE.get(), 2))
        );
         */
    }

}
