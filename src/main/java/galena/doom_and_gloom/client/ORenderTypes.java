package galena.doom_and_gloom.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import galena.doom_and_gloom.DoomAndGloom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DoomAndGloom.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
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

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        try {
            ShaderInstance shader = new ShaderInstance(event.getResourceProvider(),
                    DoomAndGloom.modLoc("rendertype_entity_translucent_additive"), DefaultVertexFormat.NEW_ENTITY);

            event.registerShader(shader, NO_ALPHA_CUTOFF_SHADER::set);
        } catch (Exception e) {
            DoomAndGloom.LOGGER.error("Failed to register shader", e);
        }

    }
}
