package galena.doom_and_gloom.data.provider;

import galena.doom_and_gloom.DoomAndGloom;
import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class DGItemModelProvider extends ItemModelProvider {

    public DGItemModelProvider(PackOutput output, ExistingFileHelper help) {
        super(output, DoomAndGloom.MOD_ID, help);
    }

    protected String blockName(Supplier<? extends Block> block) {
        return BuiltInRegistries.BLOCK.getKey(block.get()).getPath();
    }

    public ItemModelBuilder block(Supplier<? extends Block> block) {
        return block(block, blockName(block));
    }

    public ItemModelBuilder block(Supplier<? extends Block> block, String name) {
        return withExistingParent(blockName(block), modLoc("block/" + name));
    }

    public ItemModelBuilder normalItem(Supplier<? extends ItemLike> supplier) {
        var item = supplier.get().asItem();
        var id = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        return withExistingParent(id.getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + id.getPath()));
    }

    public ItemModelBuilder toolItem(Supplier<? extends Item> item) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(), mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/" + BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }

    public ItemModelBuilder spawnEggItem(Supplier<? extends Item> item) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item.get()).getPath(), "minecraft:item/template_spawn_egg");
    }

}
