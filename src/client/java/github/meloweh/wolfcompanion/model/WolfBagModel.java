package github.meloweh.wolfcompanion.model;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

import static java.lang.Math.PI;

// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class WolfBagModel extends Model {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(WolfCompanion.id("wolf_bag"), "main");
    public static final Identifier TEXTURE_LOCATION = WolfCompanion.id("textures/entity/wolf_bag.png");

    private final static float INITIAL_Z_ROTATION = 0.139626F;

    float f = 0;

    private final ModelPart main;
    public WolfBagModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.main = root.getChild("main");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create()
                .uv(0, 0).cuboid(2.6667F, -1.5F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(1, 6).cuboid(-2.3333F, -1.5F, -2.0F, 5.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(1, 13).cuboid(-2.3333F, 0.5F, -2.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(3, 20).cuboid(-2.3333F, 1.5F, -1.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 24).cuboid(-3.3333F, -1.5F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 27).cuboid(-2.3333F, -2.5F, -1.0F, 5.0F, 4.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5f, 6f, 2f, 1.5707964F, 1.5707964F, 1.5707964F));
        return TexturedModelData.of(modelData, 64, 32);
    }

    protected static void setRotation(ModelPart model,
                                      float xRotation,
                                      float yRotation,
                                      float zRotation) {
        //model.rotate(new Vector3f(xRotation, yRotation, zRotation));
        model.pitch = yRotation;
        model.yaw = xRotation;
        model.roll = 1.5707964F + zRotation;
    }

    public void animateModel(WolfEntity wolfEntity, ModelPart torso, float limbSwing, float limbSwingAmount, float partialTickTime) {
        if (wolfEntity.isInSittingPose())
            this.main.setPivot(torso.pivotX, torso.pivotY, torso.pivotZ);
        else this.main.setPivot(0, torso.pivotY, torso.pivotZ);
        this.main.yaw = -torso.pitch;
        this.main.roll = 1.5707964F + torso.roll; /*wolfEntity.getShakeAnimationProgress(partialTickTime, -0.16F) + MathHelper.sin(limbSwing * 1.2F) * 0.15F * limbSwingAmount*/;
        this.main.pitch = -1.5707964F;

        /*
        float rotationPointY = 14;
        float rotationPointZ = 2;

        float rotateAngleX = 0;

        if (wolfEntity.isSitting()) {
            rotationPointY = 18;
            rotationPointZ = 0;
            rotateAngleX = (float) (PI / -4);
        }

        //@Nonnull EntityLivingBase entity,
        //                                    float limbSwing,
        //                                    float limbSwingAmount,
        //                                    float partialTickTime

        this.main.setPivot(0, rotationPointY, rotationPointZ);

        setRotation(this.main,
                rotateAngleX,
                rotateAngleX,
                wolfEntity.getShakeAnimationProgress(partialTickTime, -0.16F) - INITIAL_Z_ROTATION + MathHelper.cos(limbSwing * 1.2F) * 0.15F * limbSwingAmount);

        //setRotation(backpackLeftTop,rotateAngleX,0,entityWolfArmored.getShakeAngle(partialTickTime, -0.16F) + INITIAL_Z_ROTATION + MathHelper.sin(limbSwing * 1.2F) * 0.15F * limbSwingAmount);
*/
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        this.main.render(matrices, vertexConsumer, light, overlay, color);
    }

    public RenderLayer getRenderLayer() {
        return RenderLayer.getEntitySolid(WolfCompanion.id("textures/entity/wolf_bag.png"));
    }

    public void flipRotation(WolfEntity wolfEntity, ModelPart torso) {
        //if (wolfEntity.isInSittingPose())
        //    this.main.setPivot(torso.pivotX, torso.pivotY, torso.pivotZ);
        this.main.roll *= -1;
        if (wolfEntity.isInSittingPose()) this.main.yaw = 2 * this.main.yaw + this.main.yaw;
        this.main.pitch *= -1;
    }

    public void copyTransform(ModelPart part) {
        this.main.copyTransform(part);
    }

    public void offset(float x, float y, float z) {
        this.main.translate(new Vector3f(x, y, z));
    }

    public void rotate(float x, float y, float z) {
        this.main.rotate(new Vector3f(x, y, z));
    }
}