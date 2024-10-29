package galena.doom_and_gloom.data.provider;

import com.teamabnormals.blueprint.core.data.client.BlueprintItemModelProvider;
import galena.doom_and_gloom.DoomAndGloom;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class OItemModelProvider extends BlueprintItemModelProvider {

    public OItemModelProvider(PackOutput output, ExistingFileHelper help) {
        super(output, DoomAndGloom.MOD_ID, help);
    }

    protected String blockName(Supplier<? extends Block> block) {
        return ForgeRegistries.BLOCKS.getKey(block.get()).getPath();
    }

    public ItemModelBuilder block(Supplier<? extends Block> block) {
        return block(block, blockName(block));
    }

    public ItemModelBuilder block(Supplier<? extends Block> block, String name) {
        return withExistingParent(blockName(block), modLoc("block/" + name));
    }

    public ItemModelBuilder normalItem(Supplier<? extends ItemLike> supplier) {
        var item = supplier.get().asItem();
        var id = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
        return withExistingParent(id.getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + id.getPath()));
    }

    public ItemModelBuilder toolItem(Supplier<? extends Item> item) {
        return withExistingParent(ForgeRegistries.ITEMS.getKey(item.get()).getPath(), mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/" + ForgeRegistries.ITEMS.getKey(item.get()).getPath()));
    }
}
