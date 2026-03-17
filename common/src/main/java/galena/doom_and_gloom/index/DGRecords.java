package galena.doom_and_gloom.index;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;

import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

public class DGRecords {

    public static final RegSupplier<JukeboxSong> AFTERLIFE = register("afterlife", DGSoundEvents.MUSIC_DISC_AFTERLIFE, 155F, 13);

    private static RegSupplier<JukeboxSong> register(String name, Holder<SoundEvent> sound, float ticks, int level) {
        var id = modLoc(name);
        var translation = Component.translatable(id.toLanguageKey("item", "desc"));
        return RegHelper.register(id, () -> new JukeboxSong(sound, translation, ticks, level), Registries.JUKEBOX_SONG);
    }

}
