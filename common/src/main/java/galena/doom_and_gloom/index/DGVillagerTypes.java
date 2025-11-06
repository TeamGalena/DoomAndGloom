package galena.doom_and_gloom.index;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;

import com.google.common.collect.ImmutableSet;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;

public class DGVillagerTypes {

    public static final RegSupplier<VillagerProfession> GRAVETENDER = register("gravetender",
            DGPoi.GRAVETENDER_POI, DGSoundEvents.GRAVETENDER_WORK);

    private static RegSupplier<VillagerProfession> register(String name, Supplier<PoiType> jobSite, Supplier<SoundEvent> workSound) {
        Supplier<VillagerProfession> factory = () -> new VillagerProfession(name,
                (holder) -> holder.value() == jobSite.get(),
                (holder) -> holder.value() == jobSite.get(),
                ImmutableSet.of(), ImmutableSet.of(), workSound.get());

        return RegHelper.register(modLoc(name), factory, Registries.VILLAGER_PROFESSION);
    }

    public static void init() {
        // Loads this class
    }

}
