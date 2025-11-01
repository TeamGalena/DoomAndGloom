package galena.doom_and_gloom.client;

import galena.doom_and_gloom.index.DGEffects;
import galena.doom_and_gloom.index.DGSoundEvents;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.entity.player.Player;

public class FogAmbientSoundHandler implements AmbientSoundHandler {

    private final Player player;
    private final SoundManager soundManager;
    private BiomeAmbientSoundsHandler.LoopSoundInstance sound;

    public FogAmbientSoundHandler(Player player, SoundManager soundManager) {
        this.player = player;
        this.soundManager = soundManager;
    }

    private void startPlaying() {
        if (sound != null) return;

        sound = new BiomeAmbientSoundsHandler.LoopSoundInstance(DGSoundEvents.FOG_AMBIENCE.get());
        soundManager.play(sound);

        sound.fadeIn();
    }

    private void stopPlaying() {
        if (sound == null) return;
        sound.fadeOut();
    }

    @Override
    public void tick() {
        if (sound != null && sound.isStopped()) {
            sound = null;
        }

        if (player.hasEffect(DGEffects.FOG.get())) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
}
