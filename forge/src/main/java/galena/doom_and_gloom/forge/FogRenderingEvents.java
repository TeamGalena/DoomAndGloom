package galena.doom_and_gloom.forge;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.fog.FogRendering;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = DoomAndGloom.MOD_ID, value = Dist.CLIENT)
public class FogRenderingEvents {

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        FogRendering.clientTick();
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
