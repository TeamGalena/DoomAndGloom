package galena.doom_and_gloom.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import galena.doom_and_gloom.client.fog.FogRendering;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Shadow
    private static float fogRed;

    @Shadow
    private static float fogGreen;

    @Shadow
    private static float fogBlue;

    @Inject(method = "setupFog", at = @At(value = "TAIL"))
    private static void dg$modifyFogShape(Camera camera, FogRenderer.FogMode fogMode,
                                          float renderDistance, boolean shouldCreateFog,
                                          float partialTick, CallbackInfo ci, @Local FogType fogType) {

        float[] newColor = FogRendering.modifyFogColor(
                fogRed, fogGreen, fogBlue, partialTick);
        if (newColor != null) {
            fogRed = newColor[0];
            fogGreen = newColor[1];
            fogBlue = newColor[2];
        }

        float start = RenderSystem.getShaderFogStart();
        float end = RenderSystem.getShaderFogEnd();
        FogShape fogShape = RenderSystem.getShaderFogShape();

        float[] nearFar = FogRendering.modifyPlanes(start, end, //near far wherever we are
                fogMode, fogShape, fogType, partialTick);

        if (nearFar != null) {
            RenderSystem.setShaderFogStart(nearFar[0]);
            RenderSystem.setShaderFogEnd(nearFar[1]);
        }
    }
}

