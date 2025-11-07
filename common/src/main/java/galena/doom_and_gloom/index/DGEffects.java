package galena.doom_and_gloom.index;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;
import static net.mehvahdjukaar.moonlight.api.platform.RegHelper.registerEffect;

import galena.doom_and_gloom.content.effect.FogEffect;
import galena.doom_and_gloom.content.effect.WardingEffect;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.minecraft.world.effect.MobEffect;

public class DGEffects {

    public static final RegSupplier<MobEffect> FOG = registerEffect(modLoc("fog"), FogEffect::new);
    public static final RegSupplier<MobEffect> WARDING = registerEffect(modLoc("warding"), WardingEffect::new);

    public static void init() {
        // Loads this class
    }

}
