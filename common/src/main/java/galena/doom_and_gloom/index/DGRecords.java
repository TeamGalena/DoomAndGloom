package galena.doom_and_gloom.index;

import galena.doom_and_gloom.DoomAndGloom;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

public class DGRecords {

    public static final ResourceKey<JukeboxSong> AFTERLIFE = ResourceKey.create(Registries.JUKEBOX_SONG, DoomAndGloom.modLoc("afterlife"));

    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        context.register(AFTERLIFE,  new JukeboxSong(DGSoundEvents.MUSIC_DISC_AFTERLIFE, Component.translatable("item.doom_and_gloom.music_disc_afterlife.desc"), 155F, 13));
    }

}
