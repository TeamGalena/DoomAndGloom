package galena.doom_and_gloom.data;

import galena.doom_and_gloom.content.block.SepulcherBlock;
import galena.doom_and_gloom.data.provider.DGBlockLootProvider;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGEntityTypes;
import galena.doom_and_gloom.index.DGLootInjects;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class DGLootTables extends LootTableProvider {

    public DGLootTables(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Set.of(), List.of(
            new SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK),
            new SubProviderEntry(EntityLoot::new, LootContextParamSets.ENTITY),
            new SubProviderEntry($ -> new InjectedLoot(), LootContextParamSets.BLOCK)
        ), provider);
    }

    @Override
    protected void validate(WritableRegistry<LootTable> registry, ValidationContext context, ProblemReporter.Collector collector) {
    }

    public static class BlockLoot extends DGBlockLootProvider {

        protected BlockLoot(HolderLookup.Provider provider) {
            super(provider);
        }

        protected void generate() {
            add(DGBlocks.SEPULCHER.get(), it -> createSingleItemTable(it)
                .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(DGBlocks.BONE_PILE.get()))
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(it).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SepulcherBlock.LEVEL, SepulcherBlock.READY)))
                )
            );
            dropSelf(DGBlocks.BONE_PILE);
            dropNothing(DGBlocks.ROTTING_FLESH);
            stoneTablet(DGBlocks.STONE_TABLET);
            stoneTablet(DGBlocks.ENGRAVED_STONE_TABLET);
            stoneTablet(DGBlocks.CRACKED_STONE_TABLET);
            dropOther(DGBlocks.BURIAL_DIRT, Blocks.DIRT);
            vigilCandle(DGBlocks.VIGIL_CANDLE, null);
            DGBlocks.COLORED_VIGIL_CANDLES.forEach((color, block) ->
                vigilCandle(block, color)
            );
        }
    }

    public static class EntityLoot extends EntityLootSubProvider {

        public EntityLoot(HolderLookup.Provider provider) {
            super(FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        public void generate() {
            add(DGEntityTypes.HOLLER.get(), LootTable.lootTable());
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return Stream.of(DGEntityTypes.HOLLER.get());
        }
    }

    public static class InjectedLoot implements LootTableSubProvider {

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            consumer.accept(DGLootInjects.PYRAMID_BONES, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.2F))
                    .add(LootItem.lootTableItem(DGBlocks.BONE_PILE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                    )
                )
            );
        }

    }

}
