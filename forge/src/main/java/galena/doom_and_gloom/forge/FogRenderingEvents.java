package galena.doom_and_gloom.forge;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.FogRendering;
import java.awt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE, modid = DoomAndGloom.MOD_ID, value = Dist.CLIENT)
public class FogRenderingEvents {

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
