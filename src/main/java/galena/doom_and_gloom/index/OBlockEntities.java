package galena.doom_and_gloom.index;

import com.teamabnormals.blueprint.core.util.registry.BlockEntitySubRegistryHelper;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.block.StoneTabletBlockEntity;
import galena.doom_and_gloom.content.entity.SepulcherBlockEntity;
import galena.doom_and_gloom.content.entity.VigilCandleBlockEntity;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = DoomAndGloom.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OBlockEntities {
    public static final BlockEntitySubRegistryHelper HELPER = DoomAndGloom.REGISTRY_HELPER.getBlockEntitySubHelper();

    public static final RegistryObject<BlockEntityType<VigilCandleBlockEntity>> VIGIL_CANDLE = HELPER.createBlockEntity("vigil_candle", VigilCandleBlockEntity::new, () -> OBlocks.vigilCandles().map(RegistryObject::get).collect(Collectors.toSet()));
    public static final RegistryObject<BlockEntityType<SepulcherBlockEntity>> SEPULCHER = HELPER.createBlockEntity("sepulcher", SepulcherBlockEntity::new, () -> Set.of(OBlocks.SEPULCHER.get()));
    public static final RegistryObject<BlockEntityType<StoneTabletBlockEntity>> STONE_TABLET = HELPER.createBlockEntity("stone_tablet", StoneTabletBlockEntity::new, () -> Set.of(OBlocks.STONE_TABLET.get()));

}
