package galena.doom_and_gloom.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class BoneFragmentParticle extends TextureSheetParticle {

    protected BoneFragmentParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z);
        this.gravity = 0.04F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (lifetime-- <= 0) {
            remove();
        } else {
            yd -= gravity;
            move(xd, yd, zd);
            xd *= 0.9800000190734863;
            yd *= 0.9800000190734863;
            zd *= 0.9800000190734863;
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            sprite = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            var particle = new BoneFragmentParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.setLifetime(level.random.nextInt(20, 20 * 4));
            particle.pickSprite(sprite);
            return particle;
        }
    }

}
