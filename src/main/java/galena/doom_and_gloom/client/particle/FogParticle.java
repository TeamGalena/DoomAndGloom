package galena.doom_and_gloom.client.particle;

import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class FogParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final double initialXd;
    private int delay;

    protected FogParticle(SpriteSet sprites, ClientLevel level, double x, double y, double z, double xd) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.initialXd = 0.02 * Math.abs(xd);
        this.xd = -initialXd;
        this.yd = 0.0;
        this.zd = 0.0;
        this.quadSize = 1F;
        this.alpha = 0F;
        this.delay = level.random.nextInt(20);
        setSpriteFromAge(sprites);
        setSize(2F, 3F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (delay-- <= 0 && age++ >= lifetime) {
            remove();
            return;
        }

        float halfLife = lifetime / 2F;
        if (age > halfLife) {
            alpha = 1F - (age - halfLife) / halfLife;
        } else {
            alpha = age / halfLife;
        }

        setSpriteFromAge(sprites);

        var collisionBox = getBoundingBox().expandTowards(0, -0.5, 0);

        Vec3 collision = Entity.collideBoundingBox(null, new Vec3(xd, yd, zd), collisionBox, level, List.of());
        var stopped = collision.x != xd;

        yd = stopped ? initialXd : 0.0;

        if (collision.x != 0 || collision.y != 0 || collision.z != 0) {
            setBoundingBox(getBoundingBox().move(collision.x, collision.y, collision.z));
            setLocationFromBoundingbox();
        }
    }

    public static ParticleEngine.SpriteParticleRegistration<SimpleParticleType> provider(int lifetime) {
        return sprites -> new Provider(sprites, lifetime);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final int lifetime;

        private Provider(SpriteSet sprites, int lifetime) {
            this.sprites = sprites;
            this.lifetime = lifetime;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            var particle = new FogParticle(sprites, level, x, y, z, xSpeed);
            particle.setLifetime(lifetime);
            return particle;
        }
    }

}
