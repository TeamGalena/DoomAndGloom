package galena.doom_and_gloom.data.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyCustomDataFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;

public abstract class DGBlockLootProvider extends BlockLootSubProvider {

    private final Collection<Block> knownBlocks = new HashSet<>();

    protected DGBlockLootProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    public void dropSelf(Supplier<? extends Block> block) {
        super.dropSelf(block.get());
    }

    public void dropOther(Supplier<? extends Block> brokenBlock, ItemLike droppedBlock) {
        super.dropOther(brokenBlock.get(), droppedBlock);
    }

    public void dropNothing(Supplier<? extends Block> block) {
        dropOther(block, Blocks.AIR);
    }

    public void vigilCandle(Supplier<? extends Block> block) {
        add(block.get(), createCandleDrops(block.get()));
    }

    public void stoneTablet(Supplier<? extends Block> block) {
        add(block.get(), LootTable.lootTable()
                .withPool(applyExplosionCondition(block.get(), LootPool.lootPool().add(
                        LootItem.lootTableItem(block.get()).apply(CopyCustomDataFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                .copy("Pixels", "BlockEntityTag.text", CopyCustomDataFunction.MergeStrategy.REPLACE)
                        )
                )))
        );
    }

    protected void add(Block block, LootTable.Builder builder) {
        super.add(block, builder);
        this.knownBlocks.add(block);
    }

    @Override
    protected final Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }

}
