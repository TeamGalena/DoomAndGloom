package galena.doom_and_gloom.client;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.model.DirtMoundModel;
import galena.doom_and_gloom.client.model.HollerModel;
import galena.doom_and_gloom.client.render.entity.DirtMoundRenderer;
import galena.doom_and_gloom.client.render.entity.HollerRender;
import galena.doom_and_gloom.index.OBlocks;
import galena.doom_and_gloom.index.OEffects;
import galena.doom_and_gloom.index.OEntityTypes;
import galena.doom_and_gloom.index.OParticleTypes;
import java.awt.*;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DoomAndGloom.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DoomAndGloomClient {

    private static void render(Supplier<? extends Block> block, RenderType render) {
        ItemBlockRenderTypes.setRenderLayer(block.get(), render);
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(()->{
            DoomAndGloomClient.registerBlockRenderers();
        });
    }

    private static void registerBlockRenderers() {
        render(OBlocks.SEPULCHER, RenderType.cutout());
        OBlocks.vigilCandles().forEach(block -> render(block, RenderType.cutout()));
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new OReloadListener());
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OEntityTypes.HOLLER.get(), HollerRender::new);
        event.registerEntityRenderer(OEntityTypes.DIRT_MOUND.get(), DirtMoundRenderer::new);
    }

    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(OModelLayers.HOLLER, HollerModel::createBodyLayer);
        event.registerLayerDefinition(OModelLayers.DIRT_MOUND, DirtMoundModel::createBodyLayer);
    }

    @Mod.EventBusSubscriber(modid = DoomAndGloom.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {

        private static MobEffectInstance fogEffect;

        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (!(Minecraft.getInstance().gameRenderer.getMainCamera().getEntity() instanceof Player player)) return;
            fogEffect = player.getEffect(OEffects.FOG.get());

            if (fogEffect == null) return;
            if (Minecraft.getInstance().isPaused()) return;
            var level = Minecraft.getInstance().level;
            if (level == null) return;

            var range = 24;
            var at = player.position().add((level.random.nextDouble() - 0.5) * range, level.random.nextDouble() * 4 - 2, (level.random.nextDouble() - 0.5) * range);
            var blockAt = BlockPos.containing(at.x, at.y, at.z);
            var stateAt = level.getBlockState(blockAt);

            if (!stateAt.canBeReplaced() || !stateAt.getFluidState().isEmpty()) return;

            var below = level.getBlockState(blockAt.below());

            if (below.getFluidState().is(FluidTags.WATER)) {
                addFogGroup(level, OParticleTypes.FOG_WATER.get(), blockAt, 3, 0);
            } else if (!below.canBeReplaced()) {
                addFogGroup(level, OParticleTypes.FOG.get(), blockAt, 5, 1);
            }
        }

        private static void addFogGroup(Level level, ParticleOptions type, BlockPos at, int amount, double yRange) {
            if(level.random.nextInt(amount * 2) != 0) return;

            var realAmount = amount - level.random.nextInt(2);

            for (int i = 0; i < realAmount; i++) {
                level.addParticle(type,
                        at.getX() + level.random.nextDouble() * 2 - 1, at.getY() + 0.5 + level.random.nextDouble() * yRange, at.getZ() + level.random.nextDouble() * 2 - 1,
                        level.random.nextFloat() + 0.5F, 0.0, 0.0
                );
            }
        }

        @SubscribeEvent
        public static void fogEffectFog(ViewportEvent.RenderFog event) {
            if (fogEffect != null && fogEffect.getFactorData().isPresent()) {
                LivingEntity entity = (LivingEntity) Minecraft.getInstance().gameRenderer.getMainCamera().getEntity();
                float f = Mth.lerp(fogEffect.getFactorData().get().getFactor(entity, (float) event.getPartialTick()), event.getFarPlaneDistance(), 15F);
                event.setNearPlaneDistance(event.getMode() == FogRenderer.FogMode.FOG_SKY ? -2F : f * -0.5F);
                event.setFarPlaneDistance(f);
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void fogEffectColor(ViewportEvent.ComputeFogColor event) {
            if (fogEffect != null && fogEffect.getFactorData().isPresent()) {
                var color = new Color(0x697180);
                LivingEntity entity = (LivingEntity) Minecraft.getInstance().gameRenderer.getMainCamera().getEntity();
                float factor = (fogEffect.getFactorData().get()).getFactor(entity, (float) event.getPartialTick());
                float inverseFactor = 1 - factor;
                event.setRed(color.getRed() / 255F * factor + event.getRed() * inverseFactor);
                event.setGreen(color.getGreen() / 255F * factor + event.getGreen() * inverseFactor);
                event.setBlue(color.getBlue() / 255F * factor + event.getBlue() * inverseFactor);
            }
        }

    }
}
