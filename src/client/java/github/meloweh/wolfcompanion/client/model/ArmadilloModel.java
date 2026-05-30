package github.meloweh.wolfcompanion.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.meloweh.wolfcompanion.entity.ArmadilloEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class ArmadilloModel extends EntityModel<ArmadilloEntity> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart shell;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;

    public ArmadilloModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.shell = this.body.getChild("shell");
        this.head = root.getChild("head");
        this.tail = root.getChild("tail");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -5.0F, -6.0F, 8.0F, 7.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 19.0F, 0.0F));
        body.addOrReplaceChild("shell", CubeListBuilder.create()
                        .texOffs(0, 19)
                        .addBox(-4.5F, -6.0F, -6.5F, 9.0F, 4.0F, 13.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(40, 0)
                        .addBox(-2.5F, -2.5F, -4.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(42, 10)
                        .addBox(-3.5F, -4.0F, -2.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 10)
                        .addBox(1.5F, -4.0F, -2.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 17.0F, -6.5F));

        root.addOrReplaceChild("tail", CubeListBuilder.create()
                        .texOffs(44, 17)
                        .addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 20.0F, 6.0F, 0.45F, 0.0F, 0.0F));

        CubeListBuilder leg = CubeListBuilder.create()
                .texOffs(36, 17)
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F));
        root.addOrReplaceChild("right_front_leg", leg, PartPose.offset(-3.0F, 20.0F, -3.5F));
        root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(3.0F, 20.0F, -3.5F));
        root.addOrReplaceChild("right_hind_leg", leg, PartPose.offset(-3.0F, 20.0F, 4.0F));
        root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(3.0F, 20.0F, 4.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(ArmadilloEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);

        float tickDelta = Mth.clamp(ageInTicks - entity.tickCount, 0.0F, 1.0F);
        float shellProgress = entity.getShellProgress(tickDelta);
        boolean hiding = entity.shouldHideInShell();
        this.shell.visible = shellProgress > 0.0F;
        this.head.visible = !hiding;
        this.tail.visible = shellProgress < 0.95F;
        this.rightFrontLeg.visible = shellProgress < 0.8F;
        this.leftFrontLeg.visible = shellProgress < 0.8F;
        this.rightHindLeg.visible = shellProgress < 0.8F;
        this.leftHindLeg.visible = shellProgress < 0.8F;

        this.body.y += shellProgress * 2.0F;
        this.body.xRot = shellProgress * 0.45F;
        this.body.xScale = 1.0F - shellProgress * 0.1F;
        this.body.yScale = 1.0F - shellProgress * 0.35F;
        this.body.zScale = 1.0F - shellProgress * 0.1F;

        this.head.xRot = headPitch * Mth.DEG_TO_RAD - shellProgress * 1.0F;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.y += shellProgress * 2.0F;
        this.head.z += shellProgress * 4.0F;

        this.tail.xRot += shellProgress * 1.1F;
        this.tail.y += shellProgress * 1.5F;
        this.tail.z -= shellProgress * 4.0F;

        float walk = Math.min(limbSwingAmount, 0.8F);
        this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.2F * walk;
        this.leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.2F * walk;
        this.rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.2F * walk;
        this.leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.2F * walk;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
