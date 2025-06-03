package galena.doom_and_gloom.index;

import com.google.common.collect.ImmutableSet;
import galena.doom_and_gloom.DoomAndGloom;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.DeferredRegister;

public class OVillagerTypes {

    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, DoomAndGloom.MOD_ID);

    public static final Supplier<VillagerProfession> GRAVETENDER =
            VILLAGER_PROFESSIONS.register("gravetender",
                    registerVillager("gravetender", OPoi.GRAVETENDER_POI_KEY,
                            OSoundEvents.GRAVETENDER_WORK));

    private static Supplier<VillagerProfession> registerVillager(String name, ResourceKey<PoiType> jobSite,
                                                                       Supplier<SoundEvent> workSound) {
        return  () -> new VillagerProfession(name,
                (holder) -> holder.is(jobSite),
                (holder) -> holder.is(jobSite),
                ImmutableSet.of(), ImmutableSet.of(), workSound.get());
    }
}
