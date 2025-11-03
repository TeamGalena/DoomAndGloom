package galena.doom_and_gloom.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.model.DirtMoundModel;
import galena.doom_and_gloom.client.model.HollerModel;
import galena.doom_and_gloom.client.particle.BoneFragmentParticle;
import galena.doom_and_gloom.client.particle.FogParticle;
import galena.doom_and_gloom.client.render.entity.DirtMoundRenderer;
import galena.doom_and_gloom.client.render.entity.HollerRender;
import galena.doom_and_gloom.index.DGBlocks;
import galena.doom_and_gloom.index.DGEntityTypes;
import galena.doom_and_gloom.index.DGParticleTypes;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.client.particle.SoulParticle;
import net.minecraft.client.renderer.RenderType;

public class DoomAndGloomClient {


    public static void init() {
        ClientHelper.addClientSetup(DoomAndGloomClient::setup);
        ClientHelper.addClientReloadListener(DGReloadListener::new, DoomAndGloom.modLoc("tablets_reloader"));
        ClientHelper.addParticleRegistration(DoomAndGloomClient::registerParticleFactories);
        ClientHelper.addEntityRenderersRegistration(DoomAndGloomClient::registerEntityRenderers);
        ClientHelper.addModelLayerRegistration(DoomAndGloomClient::registerModelLayers);
        ClientHelper.addShaderRegistration(DoomAndGloomClient::registerShaders);
    }

    private static void setup() {
        //render layers
        RenderType cutout = RenderType.cutout();
        ClientHelper.registerRenderType(DGBlocks.SEPULCHER.get(), cutout);
        DGBlocks.vigilCandles().forEach(block -> ClientHelper.registerRenderType(block.get(), cutout));
    }


    public static void registerShaders(ClientHelper.ShaderEvent event) {
        event.register(DoomAndGloom.modLoc("rendertype_entity_translucent_additive"),
                DefaultVertexFormat.NEW_ENTITY, DGRenderTypes.NO_ALPHA_CUTOFF_SHADER::assign);
    }

    private static void registerEntityRenderers(ClientHelper.EntityRendererEvent event) {
        event.register(DGEntityTypes.HOLLER.get(), HollerRender::new);
        event.register(DGEntityTypes.DIRT_MOUND.get(), DirtMoundRenderer::new);
    }

    public static void registerModelLayers(ClientHelper.ModelLayerEvent event) {
        event.register(DGModelLayers.HOLLER, HollerModel::createBodyLayer);
        event.register(DGModelLayers.DIRT_MOUND, DirtMoundModel::createBodyLayer);
    }

    private static void registerParticleFactories(ClientHelper.ParticleEvent event) {
        event.register(DGParticleTypes.BONE_FRAGMENT.get(), BoneFragmentParticle.Provider::new);
        event.register(DGParticleTypes.FOG.get(), sprites -> new FogParticle.Provider(sprites, 200));
        event.register(DGParticleTypes.FOG_WATER.get(), sprites -> new FogParticle.Provider(sprites, 100));
        event.register(DGParticleTypes.HOLLERING_SOUL.get(), SoulParticle.Provider::new);
    }


}
