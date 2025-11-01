package galena.doom_and_gloom.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.client.OModelLayers;
import galena.doom_and_gloom.client.model.DirtMoundModel;
import galena.doom_and_gloom.content.entity.DirtMound;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DirtMoundRenderer extends EntityRenderer<DirtMound> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(DoomAndGloom.MOD_ID, "textures/entity/dirt_mound.png");

    private final EntityModel<DirtMound> model;

    public DirtMoundRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new DirtMoundModel(context.bakeLayer(OModelLayers.DIRT_MOUND));
    }

    @Override
    public void render(DirtMound entity, float pEntityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        pose.pushPose();

        RenderType rendertype = this.model.renderType(getTextureLocation(entity));
        VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
        pose.scale(-1.0F, -1.0F, 1.0F);
        pose.translate(0.0F, -1.501F, 0.0F);
        this.model.renderToBuffer(pose, vertexconsumer, packedLight, 655360, 1.0F, 1.0F, 1.0F, 1.0F);

        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DirtMound entity) {
        return TEXTURE;
    }

}
