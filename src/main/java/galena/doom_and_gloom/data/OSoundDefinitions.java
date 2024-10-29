package galena.doom_and_gloom.data;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.index.OSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class OSoundDefinitions extends SoundDefinitionsProvider {

    public OSoundDefinitions(PackOutput output, ExistingFileHelper helper) {
        super(output, DoomAndGloom.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        add(OSoundEvents.MUSIC_DISC_AFTERLIFE, definition().with(
                sound(DoomAndGloom.MOD_ID + ":music/disc/afterlife").stream()
        ));

        SoundDefinition.Sound[] bonePileBreakSounds = {
                sound(DoomAndGloom.MOD_ID + ":block/bone_pile_break_0"),
                sound(DoomAndGloom.MOD_ID + ":block/bone_pile_break_1"),
                sound(DoomAndGloom.MOD_ID + ":block/bone_pile_break_2"),
                sound(DoomAndGloom.MOD_ID + ":block/bone_pile_break_3"),
                sound(DoomAndGloom.MOD_ID + ":block/bone_pile_break_4")
        };

        SoundDefinition.Sound[] bonePileStepSounds = {
                sound(DoomAndGloom.MOD_ID + ":block/bone_pile_step_0"),
                sound(DoomAndGloom.MOD_ID + ":block/bone_pile_step_1"),
                sound(DoomAndGloom.MOD_ID + ":block/bone_pile_step_2"),
                sound(DoomAndGloom.MOD_ID + ":block/bone_pile_step_3")
        };

        add(OSoundEvents.BONE_PILE_BREAK, definition().with(bonePileBreakSounds).subtitle("subtitles.block.generic.break"));
        add(OSoundEvents.BONE_PILE_PLACE, definition().with(bonePileBreakSounds).subtitle("subtitles.block.generic.place"));
        add(OSoundEvents.BONE_PILE_HIT, definition().with(bonePileStepSounds).subtitle("subtitles.block.generic.hit"));
        add(OSoundEvents.BONE_PILE_STEP, definition().with(bonePileStepSounds).subtitle("subtitles.block.generic.footsteps"));
        add(OSoundEvents.BONE_PILE_FALL, definition().with(bonePileStepSounds));

        SoundDefinition.Sound[] sepulcherBreakSounds = {
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_break_0"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_break_1"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_break_2"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_break_3")
        };

        SoundDefinition.Sound[] sepulcherStepSounds = {
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_step_0"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_step_1"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_step_2"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_step_3"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_step_4")
        };

        add(OSoundEvents.SEPULCHER_BREAK, definition().with(sepulcherBreakSounds).subtitle("subtitles.block.generic.break"));
        add(OSoundEvents.SEPULCHER_PLACE, definition().with(sepulcherBreakSounds).subtitle("subtitles.block.generic.place"));
        add(OSoundEvents.SEPULCHER_HIT, definition().with(sepulcherStepSounds).subtitle("subtitles.block.generic.hit"));
        add(OSoundEvents.SEPULCHER_STEP, definition().with(sepulcherStepSounds).subtitle("subtitles.block.generic.footsteps"));
        add(OSoundEvents.SEPULCHER_FALL, definition().with(sepulcherStepSounds));

        add(OSoundEvents.SEPULCHER_CORPSE_STUFFED, definition().with(
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_corpse_stuffed_0"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_corpse_stuffed_1")
        ).subtitle("subtitles.block.sepulcher.corpse_stuffed"));

        add(OSoundEvents.SEPULCHER_FILLED, definition().with(
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_filled_0"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_filled_1"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_filled_2"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_filled_3")
        ).subtitle("subtitles.block.sepulcher.filled"));

        add(OSoundEvents.SEPULCHER_ROTTING, definition().with(
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_rotting_0"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_rotting_1"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_rotting_2"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_rotting_3")
        ).subtitle("subtitles.block.sepulcher.rotting"));

        add(OSoundEvents.SEPULCHER_HARVEST, definition().with(
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_harvest_0"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_harvest_1"),
                sound(DoomAndGloom.MOD_ID + ":block/sepulcher_harvest_2")
        ).subtitle("subtitles.block.sepulcher.harvest"));

        add(OSoundEvents.SEPULCHER_SEALING, definition()
                .with(sound(DoomAndGloom.MOD_ID + ":block/sepulcher_sealing"))
                .subtitle("subtitles.block.sepulcher.sealing"));

        add(OSoundEvents.SEPULCHER_UNSEALING, definition()
                .with(sound(DoomAndGloom.MOD_ID + ":block/sepulcher_unsealing"))
                .subtitle("subtitles.block.sepulcher.unsealing"));

        add(OSoundEvents.FOG_AMBIENCE, definition().with(
                sound(DoomAndGloom.MOD_ID + ":ambient/fog_ambience").stream()
        ));

        add(OSoundEvents.HOLLER_DEATH, definition().with(
                sound(DoomAndGloom.MOD_ID + ":entity/holler_dies_0"),
                sound(DoomAndGloom.MOD_ID + ":entity/holler_dies_1")
        ).subtitle("subtitles.entity.holler_death"));

        add(OSoundEvents.HOLLER_HURTS, definition().with(
                sound(DoomAndGloom.MOD_ID + ":entity/holler_hurts_0"),
                sound(DoomAndGloom.MOD_ID + ":entity/holler_hurts_1")
        ).subtitle("subtitles.entity.holler_hurt"));

        add(OSoundEvents.HOLLER_SHRIEKS, definition().with(
                sound(DoomAndGloom.MOD_ID + ":entity/holler_shrieks_0"),
                sound(DoomAndGloom.MOD_ID + ":entity/holler_shrieks_1"),
                sound(DoomAndGloom.MOD_ID + ":entity/holler_shrieks_2")
        ).subtitle("subtitles.entity.holler_shrieks"));

        add(OSoundEvents.HOLLER_HOLLERS, definition().with(
                sound(DoomAndGloom.MOD_ID + ":entity/holler_hollers_0"),
                sound(DoomAndGloom.MOD_ID + ":entity/holler_hollers_1"),
                sound(DoomAndGloom.MOD_ID + ":entity/holler_hollers_2"),
                sound(DoomAndGloom.MOD_ID + ":entity/holler_hollers_3")
        ).subtitle("subtitles.entity.holler_hollers"));
    }

}