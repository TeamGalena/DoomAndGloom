package galena.doom_and_gloom.forge;

import com.mojang.blaze3d.systems.RenderSystem;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.fog.FogRendering;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE, modid = DoomAndGloom.MOD_ID, value = Dist.CLIENT)
public class FogRenderingEvents {

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) FogRendering.clientTick();
    }

    @SubscribeEvent
    public static void fogEffectFog(ViewportEvent.RenderFog event) {

        float[] nearFar = FogRendering.modifyPlanes(event.getNearPlaneDistance(), event.getFarPlaneDistance(), //near far wherever we are
                event.getMode(), event.getFogShape(), event.getType(), (float) event.getPartialTick());

        if (nearFar != null) {
            event.setNearPlaneDistance(nearFar[0]);
            event.setFarPlaneDistance(nearFar[1]);
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public static void fogEffectColor(ViewportEvent.ComputeFogColor event) {
        float[] newColor = FogRendering.modifyFogColor(
                event.getRed(), event.getGreen(), event.getBlue(),
                (float) event.getPartialTick());
        if (newColor != null) {
            event.setRed(newColor[0]);
            event.setGreen(newColor[1]);
            event.setBlue(newColor[2]);
        }
    }

}
