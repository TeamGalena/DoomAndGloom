package galena.doom_and_gloom.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import galena.doom_and_gloom.fabric.FogParams;
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

    @Shadow private static float fogRed;

    @Shadow private static float fogGreen;

    @Shadow private static float fogBlue;

    @Inject(method = "setupFog", at = @At(value = "TAIL"))
    private static void polytone$modifyFogShape(Camera camera, FogRenderer.FogMode fogMode,
                                                float farPlaneDistance, boolean shouldCreateFog,
                                                float partialTick, CallbackInfo ci, @Local FogType fogType) {
        if (fogMode == FogRenderer.FogMode.FOG_TERRAIN && fogType == FogType.NONE) {

            FogParams oldParams = new FogParams(RenderSystem.getShaderFogStart(),
                    RenderSystem.getShaderFogEnd(), fogRed, fogGreen, fogBlue);

            RenderSystem.setShaderFogStart(newFog.x);
            RenderSystem.setShaderFogEnd(newFog.y);

            fogRed = ,
            fogGreen = ,
            fogBlue = ,

        }
    }

}
