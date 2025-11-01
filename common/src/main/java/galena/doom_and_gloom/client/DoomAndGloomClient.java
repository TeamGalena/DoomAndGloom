package galena.doom_and_gloom.client;

import galena.doom_and_gloom.client.particle.BoneFragmentParticle;
import galena.doom_and_gloom.client.particle.FogParticle;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGParticleTypes;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SoulParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.Block;

public class DoomAndGloomClient {

    private static void render(Supplier<? extends Block> block, RenderType render) {
        // TODO try to handle in model?
        // ItemBlockRenderTypes.setRenderLayer(block.get(), render);
    }

    public static void init() {
        DoomAndGloomClient.registerBlockRenderers();
    }

    private static void registerBlockRenderers() {
        render(DGBlocks.SEPULCHER, RenderType.cutout());
        DGBlocks.vigilCandles().forEach(block -> render(block, RenderType.cutout()));
    }

    // TODO this is not called on fabric yet
    public static void registerParticleFactories(BiConsumer<SimpleParticleType, Function<SpriteSet, ParticleProvider<SimpleParticleType>>> event) {
        event.accept(DGParticleTypes.BONE_FRAGMENT.get(), BoneFragmentParticle.Provider::new);
        event.accept(DGParticleTypes.FOG.get(), sprites -> new FogParticle.Provider(sprites, 200));
        event.accept(DGParticleTypes.FOG_WATER.get(), sprites -> new FogParticle.Provider(sprites, 100));
        event.accept(DGParticleTypes.HOLLERING_SOUL.get(), SoulParticle.Provider::new);
    }

}
