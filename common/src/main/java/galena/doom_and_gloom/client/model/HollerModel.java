package galena.doom_and_gloom.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import galena.doom_and_gloom.DGConfig;
import galena.doom_and_gloom.client.ORenderTypes;
import galena.doom_and_gloom.content.entity.holler.Holler;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class HollerModel<T extends Holler> extends EntityModel<T> implements HeadedModel {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart left_arm;

    private double yHover;
    private float alphaMult;

    private Vec3 prevPosDelta;
    private float prevRotDelta;

    public HollerModel(ModelPart root) {
        // fancy render type. Needst more experimentation. Try me out
        super(true ?  ORenderTypes.ADDITIVE_TRANSLUCENCY :
                ORenderTypes.ENTITY_TRANSLUCENT_NO_ALPHA_CUTOFF);
        //super();
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.right_arm = root.getChild("right_arm");
        this.left_arm = root.getChild("left_arm");
        this.alphaMult = DGConfig.CLIENT.fancyRenderType.get() ? 0.8f : 1;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5, -2.5F, 5, 5, 5)
                .texOffs(10, 28).addBox(-1.5F, 0, -2.5F, 3, 2, 2)
                .texOffs(12, 16).addBox(-2.5F, -5, -2.5F, 5, 5, 5, new CubeDeformation(0.5F)), PartPose.offset(0, 18, 0));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5F, 0, -1, 3, 4, 2)
                .texOffs(0, 16).addBox(-1.5F, 4, -1, 3, 5, 2, new CubeDeformation(-0.2F))
                .texOffs(0, 16).addBox(-1.5F, 2.5F, -1, 3, 2, 2, new CubeDeformation(-0.2F)), PartPose.offset(0, 18, 0));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(22, 0).addBox(-0.75F, -0.5F, -1, 2, 6, 2)
                .texOffs(2, 24).mirror().addBox(-0.75F, -0.5F, -0.9F, 5, 6, 0).mirror(false)
                .texOffs(2, 24).mirror().addBox(1.25F, -0.5F, 0.9F, 5, 6, 0).mirror(false), PartPose.offsetAndRotation(-1.75F, 18.5F, 0, 0, 0, 1.5708F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(22, 26).addBox(-6.25F, -0.5F, 0.9F, 5, 6, 0)
                .texOffs(22, 26).addBox(-4.25F, -0.5F, -0.9F, 5, 6, 0)
                .texOffs(22, 8).addBox(-1.25F, -0.5F, -1, 2, 6, 2), PartPose.offsetAndRotation(1.75F, 18.5F, 0, 0, 0, -1.5708F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void prepareMobModel(T entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        super.prepareMobModel(entity, pLimbSwing, pLimbSwingAmount, pPartialTick);

        float currentBodyRot = Mth.rotLerp(pPartialTick, entity.yBodyRotO, entity.yBodyRot);
        // gets pos delta relative to my own facing. Has to undo renderer rotation
        // luckily we only rotate on Y. Could be done better on the renderer level
        // no clue why z needs to be flipped...

        prevPosDelta = entity.getPosDelta(pPartialTick)
                .multiply(-1, 1, 1)
                .yRot(-currentBodyRot * Mth.DEG_TO_RAD);

        prevRotDelta = entity.getYRotDelta(pPartialTick);
        alphaMult = 1;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        yHover = (Math.sin(ageInTicks * 0.12) - 1.5) * 0.1;

        //we ignore arm swing and limb swing amount and use our own static ones
        //this could be improved by having some part of the anim depend on those so they increase when mob speeds up
        float armSwing = 0.1f;
        float armSwingSpeed = 0.1f;
        left_arm.zRot = -Mth.HALF_PI + Mth.sin(ageInTicks * armSwingSpeed) * armSwing;
        right_arm.zRot = Mth.HALF_PI + Mth.sin((ageInTicks * armSwingSpeed - Mth.HALF_PI / 2)) * armSwing;


        body.xRot = (Mth.cos(ageInTicks * 0.09f) + 1) * 0.08f + 0.1f;

        alphaMult = 0.8f;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {

        double alphaDecayFactor = prevPosDelta.lengthSqr() * 100f;
        alphaDecayFactor = Math.min(alphaDecayFactor, 1);
        int step = 6;

        float stepYRot = prevRotDelta / step;
        //renders back to front so first one rendes last
        for (int i = step - 1; i >= 0; i--) {
            float fract = i / (float) step;
            float actualAlpha = alpha * alphaMult * (i == 0 ? 1 :
                    (float) ((1 - fract * (1 / alphaDecayFactor))));

            if (actualAlpha <= 0) continue;
            //cubic decay
            actualAlpha = actualAlpha * actualAlpha * actualAlpha;

            poseStack.pushPose();
            //scale trail further back
            float trailScalar = 4;
            var stepDelta = prevPosDelta.scale(i / (float) step * trailScalar);
            poseStack.translate(stepDelta.x, stepDelta.y + yHover, stepDelta.z);
            poseStack.mulPose(Axis.YN.rotationDegrees(stepYRot * i));
            float scale = 1 - (0.3f * i / (float) (step));

            //centers the model
            poseStack.translate(0, 1, 0);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0, -1, 0);

            head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, actualAlpha);
            body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, actualAlpha);
            left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, actualAlpha);
            right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, actualAlpha);

            poseStack.popPose();
        }
    }

    @Override
    public ModelPart getHead() {
        return head;
    }
}