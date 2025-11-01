package galena.doom_and_gloom.forge;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.DGReloadListener;
import galena.doom_and_gloom.client.DoomAndGloomClient;
import galena.doom_and_gloom.client.FogRendering;
import galena.doom_and_gloom.client.OModelLayers;
import galena.doom_and_gloom.client.ORenderTypes;
import galena.doom_and_gloom.client.model.DirtMoundModel;
import galena.doom_and_gloom.client.model.HollerModel;
import galena.doom_and_gloom.client.render.entity.DirtMoundRenderer;
import galena.doom_and_gloom.client.render.entity.HollerRender;
import galena.doom_and_gloom.index.DGEntityTypes;
import java.awt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = DoomAndGloom.MOD_ID, value = Dist.CLIENT)
public class ForgeClientEntrypoint {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(DoomAndGloomClient::init);
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new DGReloadListener());
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DGEntityTypes.HOLLER.get(), HollerRender::new);
        event.registerEntityRenderer(DGEntityTypes.DIRT_MOUND.get(), DirtMoundRenderer::new);
    }

    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(OModelLayers.HOLLER, HollerModel::createBodyLayer);
        event.registerLayerDefinition(OModelLayers.DIRT_MOUND, DirtMoundModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        DoomAndGloomClient.registerParticleFactories((type, factory) ->
                event.registerSpriteSet(type, factory::apply)
        );
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        ORenderTypes.registerShaders(event.getResourceProvider(), event::registerShader);
    }

    @EventBusSubscriber(modid = DoomAndGloom.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {

        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            FogRendering.clientTick();
        }

        @SubscribeEvent
        public static void fogEffectFog(ViewportEvent.RenderFog event) {
            FogRendering.activeEffect().flatMap(MobEffectInstance::getFactorData).ifPresent(factorData -> {
                LivingEntity entity = (LivingEntity) Minecraft.getInstance().gameRenderer.getMainCamera().getEntity();
                float f = Mth.lerp(factorData.getFactor(entity, (float) event.getPartialTick()), event.getFarPlaneDistance(), 15F);
                event.setNearPlaneDistance(event.getMode() == FogRenderer.FogMode.FOG_SKY ? -2F : f * -0.5F);
                event.setFarPlaneDistance(f);
                event.setCanceled(true);
            });
        }

        @SubscribeEvent
        public static void fogEffectColor(ViewportEvent.ComputeFogColor event) {
            var from = new Color(event.getRed(), event.getGreen(), event.getBlue());
            FogRendering.fogEffectColor(from, (float) event.getPartialTick()).ifPresent(to -> {
                event.setRed(to.getRed());
                event.setGreen(to.getGreen());
                event.setBlue(to.getBlue());
            });
        }

    }

}
