package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.block.SepulcherBlock;
import galena.doom_and_gloom.data.provider.OBlockLootProvider;
import galena.doom_and_gloom.index.OBlocks;
import galena.doom_and_gloom.index.OEntityTypes;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OLootTables extends LootTableProvider {

    public OLootTables(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(EntityLoot::new, LootContextParamSets.ENTITY)
        ));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext tracker) {
    }

    public static class BlockLoot extends OBlockLootProvider {

        protected void generate() {
            add(OBlocks.SEPULCHER.get(), it -> createSingleItemTable(it)
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(OBlocks.BONE_PILE.get()))
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(it).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SepulcherBlock.LEVEL, SepulcherBlock.READY)))
                    )
            );
            dropSelf(OBlocks.BONE_PILE);
            dropNothing(OBlocks.ROTTING_FLESH);
            dropSelf(OBlocks.STONE_TABLET);
            dropSelf(OBlocks.ENGRAVED_STONE_TABLET);
            dropSelf(OBlocks.CRACKED_STONE_TABLET);
            dropOther(OBlocks.BURIAL_DIRT, Blocks.DIRT);
            OBlocks.vigilCandles().forEach(this::vigilCandle);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return DoomAndGloom.REGISTRY_HELPER.getBlockSubHelper().getDeferredRegister().getEntries().stream().map(Supplier::get).collect(Collectors.toList());
        }
    }

    public static class EntityLoot extends EntityLootSubProvider {

        public EntityLoot() {
            super(FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        public void generate() {
            add(OEntityTypes.HOLLER.get(), LootTable.lootTable());
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return OEntityTypes.ENTITIES.getEntries().stream().map(Supplier::get);
        }
    }
}
