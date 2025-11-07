package galena.doom_and_gloom.index;

import static galena.doom_and_gloom.DoomAndGloom.modLoc;
import static net.mehvahdjukaar.moonlight.api.platform.RegHelper.registerParticle;

import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.minecraft.core.particles.SimpleParticleType;

public class DGParticleTypes {

    public static final RegSupplier<SimpleParticleType> BONE_FRAGMENT = registerParticle(modLoc("bone_fragment"));
    public static final RegSupplier<SimpleParticleType> FOG = registerParticle(modLoc("fog"));
    public static final RegSupplier<SimpleParticleType> FOG_WATER = registerParticle(modLoc("fog_water"));
    public static final RegSupplier<SimpleParticleType> HOLLERING_SOUL = registerParticle(modLoc("hollering_soul"));

    public static void init() {
        // Loads this class
    }

}
