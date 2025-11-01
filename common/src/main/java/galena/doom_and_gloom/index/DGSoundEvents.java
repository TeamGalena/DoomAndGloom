package galena.doom_and_gloom.index;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;
import static net.mehvahdjukaar.moonlight.api.platform.RegHelper.registerSound;

import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.minecraft.sounds.SoundEvent;

public class DGSoundEvents {

    public static final RegSupplier<SoundEvent> MUSIC_DISC_AFTERLIFE = registerSound(modLoc("music.disc.afterlife"));

    public static final RegSupplier<SoundEvent> BONE_PILE_BREAK = registerSound(modLoc("block.bone_pile.break"));
    public static final RegSupplier<SoundEvent> BONE_PILE_STEP = registerSound(modLoc("block.bone_pile.step"));
    public static final RegSupplier<SoundEvent> BONE_PILE_FALL = registerSound(modLoc("block.bone_pile.fall"));
    public static final RegSupplier<SoundEvent> BONE_PILE_HIT = registerSound(modLoc("block.bone_pile.hit"));
    public static final RegSupplier<SoundEvent> BONE_PILE_PLACE = registerSound(modLoc("block.bone_pile.place"));

    public static final RegSupplier<SoundEvent> SEPULCHER_BREAK = registerSound(modLoc("block.sepulcher.break"));
    public static final RegSupplier<SoundEvent> SEPULCHER_STEP = registerSound(modLoc("block.sepulcher.step"));
    public static final RegSupplier<SoundEvent> SEPULCHER_FALL = registerSound(modLoc("block.sepulcher.fall"));
    public static final RegSupplier<SoundEvent> SEPULCHER_HIT = registerSound(modLoc("block.sepulcher.hit"));
    public static final RegSupplier<SoundEvent> SEPULCHER_PLACE = registerSound(modLoc("block.sepulcher.place"));
    public static final RegSupplier<SoundEvent> SEPULCHER_CORPSE_STUFFED = registerSound(modLoc("block.sepulcher.corpse_stuffed"));
    public static final RegSupplier<SoundEvent> SEPULCHER_FILLED = registerSound(modLoc("block.sepulcher.filled"));
    public static final RegSupplier<SoundEvent> SEPULCHER_ROTTING = registerSound(modLoc("block.sepulcher.rotting"));
    public static final RegSupplier<SoundEvent> SEPULCHER_SEALING = registerSound(modLoc("block.sepulcher.sealing"));
    public static final RegSupplier<SoundEvent> SEPULCHER_UNSEALING = registerSound(modLoc("block.sepulcher.unsealing"));
    public static final RegSupplier<SoundEvent> SEPULCHER_HARVEST = registerSound(modLoc("block.sepulcher.harvest"));

    //TODO: add these 2
    public static final RegSupplier<SoundEvent> GRAVETENDER_WORK = registerSound(modLoc("entity.villager.work_gravetender"));

    public static final RegSupplier<SoundEvent> STONE_TABLET_ENGRAVE = registerSound(modLoc("block.stone_tablet.engrave"));

    public static final RegSupplier<SoundEvent> HOLLER_DEATH = registerSound(modLoc("entity.holler_death"));
    public static final RegSupplier<SoundEvent> HOLLER_HURTS = registerSound(modLoc("entity.holler_hurts"));
    public static final RegSupplier<SoundEvent> HOLLER_HOLLERS = registerSound(modLoc("entity.holler_hollers"));
    public static final RegSupplier<SoundEvent> HOLLER_SHRIEKS = registerSound(modLoc("entity.holler_shrieks"));

    public static final RegSupplier<SoundEvent> FOG_AMBIENCE = registerSound(modLoc("ambient.fog"));

    public static void init() {
        // Loads this class
    }

}
