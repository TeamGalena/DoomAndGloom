package galena.doom_and_gloom.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.index.DGEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Unique
    private static final ResourceLocation MOON_LOCATION = DoomAndGloom.modLoc("textures/environment/moon_phases_fog.png");

    @Unique
    private static final ResourceLocation SUN_LOCATION = DoomAndGloom.modLoc("textures/environment/sun_fog.png");

    @Inject(
            method = "renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FDDD)V",
            cancellable = true,
            at = @At("HEAD")
    )
    private void disableClouds(PoseStack pose, Matrix4f matrix, float p_254364_, double p_253843_, double p_253663_, double p_253795_, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(DGEffects.FOG.get())) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;SUN_LOCATION:Lnet/minecraft/resources/ResourceLocation;")
    )
    private ResourceLocation replaceSun(ResourceLocation original) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(DGEffects.FOG.get())) return SUN_LOCATION;
        else return original;
    }

    @ModifyExpressionValue(
            method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;MOON_LOCATION:Lnet/minecraft/resources/ResourceLocation;")
    )
    private ResourceLocation replaceMoon(ResourceLocation original) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(DGEffects.FOG.get())) return MOON_LOCATION;
        else return original;
    }

    @ModifyExpressionValue(
            method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V",
            at =@At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F")
    )
    private float hideStars(float original) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(DGEffects.FOG.get())) return 0F;
        else return original;
    }

}
