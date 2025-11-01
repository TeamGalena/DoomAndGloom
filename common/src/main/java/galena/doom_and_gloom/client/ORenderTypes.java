package galena.doom_and_gloom.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import galena.doom_and_gloom.DoomAndGloom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

public abstract class ORenderTypes extends RenderType {
    //TODO: check if iris is on. If on use default render type
    private static final AtomicReference<ShaderInstance> NO_ALPHA_CUTOFF_SHADER = new AtomicReference<>();

    protected static final ShaderStateShard NO_ALPHA_CUTOFF_SHARD = new ShaderStateShard(NO_ALPHA_CUTOFF_SHADER::get);

    protected static final TransparencyStateShard ADDITIVE_TRANSPARENCY = new TransparencyStateShard("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static final Function<ResourceLocation, RenderType> ADDITIVE_TRANSLUCENCY = Util.memoize((t) -> {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setShaderState(NO_ALPHA_CUTOFF_SHARD)
                .setTextureState(new RenderStateShard.TextureStateShard(t, false, false))
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
        return create("doom_and_gloom_entity_additive_translucency", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
                256, true, true, rendertype$compositestate);
    });

    public static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_NO_ALPHA_CUTOFF = Util.memoize((r) -> {
        CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setShaderState(NO_ALPHA_CUTOFF_SHARD)
                .setTextureState(new RenderStateShard.TextureStateShard(r, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
        return create("doom_and_gloom_entity_translucent_no_alpha_cutoff", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$compositestate);
    });

    public ORenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    // TODO this is not called on fabric yet
    public static void registerShaders(ResourceProvider resources, BiConsumer<ShaderInstance, Consumer<ShaderInstance>> event) {
        try {
            // TODO this is a resource location on forge, right now it tries to look in the "minecraft" folder instead
            var shader = new ShaderInstance(resources, "rendertype_entity_translucent_additive", DefaultVertexFormat.NEW_ENTITY);
            event.accept(shader, NO_ALPHA_CUTOFF_SHADER::set);
        } catch (Exception e) {
            DoomAndGloom.LOGGER.error("Failed to register shader", e);
        }
    }
}
