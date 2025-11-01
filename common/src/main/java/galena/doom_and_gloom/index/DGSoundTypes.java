package galena.doom_and_gloom.index;

import net.mehvahdjukaar.moonlight.api.misc.ModSoundType;
import net.minecraft.world.level.block.SoundType;

public class DGSoundTypes {

    public static final SoundType BONE_PILE = new ModSoundType(1.5F, 1.0F, DGSoundEvents.BONE_PILE_BREAK, DGSoundEvents.BONE_PILE_STEP, DGSoundEvents.BONE_PILE_PLACE, DGSoundEvents.BONE_PILE_HIT, DGSoundEvents.BONE_PILE_FALL);
    public static final SoundType SEPULCHER = new ModSoundType(1.0F, 1.0F, DGSoundEvents.SEPULCHER_BREAK, DGSoundEvents.SEPULCHER_STEP, DGSoundEvents.SEPULCHER_PLACE, DGSoundEvents.SEPULCHER_HIT, DGSoundEvents.SEPULCHER_FALL);

}
