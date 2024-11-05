package renderer;

//import github.meloweh.wolfcompanion.model.ExampleChestModel;
import accessor.WolfEntityModelAccessor;
import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.model.WolfBagModel;
import github.meloweh.wolfcompanion.model.WolfChestModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import org.joml.Quaternionf;

public class CustomWolfEntityRenderer extends WolfEntityRenderer {
    public CustomWolfEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        // Add your custom feature renderer
        System.out.println("CustomWolfEntityRenderer initialized!");
        this.addFeature(new CustomFeatureRenderer(this));
    }

    // Custom feature renderer class
    private static class CustomFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
        private final WolfChestModel chestModel;
        private final WolfBagModel bagModel;
        final private ModelPart wolfTorso;

        public CustomFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context) {
            super(context);

            WolfEntityModel<WolfEntity> model = context.getModel();

            this.wolfTorso = ((WolfEntityModelAccessor) model).getTorso();

            WolfCompanion.LOGGER.warn(this.wolfTorso.pitch + " " + this.wolfTorso.yaw + " " + this.wolfTorso.roll + " " + this.wolfTorso.pivotX + " " + this.wolfTorso.pivotY + " " + this.wolfTorso.pivotZ);

            //this.chestModel = new ExampleChestModel(context.getLayerModelPart(ExampleChestModel.LAYER_LOCATION));
            final ModelPart chestRoot = WolfChestModel.getTexturedModelData().createModel();//ChestModelUtil.createChestRoot();
            this.chestModel = new WolfChestModel(chestRoot);
            
            final ModelPart bagRoot = WolfBagModel.getTexturedModelData().createModel();
            this.bagModel = new WolfBagModel(bagRoot);
        }


        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            this.wolfTorso.hidden = entity.isSitting();
            if (!entity.isTamed()) return;

            //WolfCompanion.LOGGER.warn(this.wolfTorso.pitch + " " + this.wolfTorso.yaw + " " + this.wolfTorso.roll + " " + this.wolfTorso.pivotX + " " + this.wolfTorso.pivotY + " " + this.wolfTorso.pivotZ);

            matrices.push();

// Translate based on the torso's pivot values to align the backpack with the wolf’s back
            matrices.translate(-0.3 + this.wolfTorso.pivotX, -0.37 + this.wolfTorso.pivotY - 14, 0.26 + this.wolfTorso.pivotZ);

// Adjust rotation based on torso angles (pitch, yaw, roll)
            matrices.multiply(new Quaternionf().rotateX(this.wolfTorso.pitch)); // Tilt forward/backward
            matrices.multiply(new Quaternionf().rotateY(this.wolfTorso.yaw));   // Rotate left/right
            matrices.multiply(new Quaternionf().rotateZ(this.wolfTorso.roll));  // Rotate along body’s axis

// Render the chest model with the new transformations
            chestModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfChestModel.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);

            matrices.pop();

            matrices.push();
            matrices.translate(-0.3, -0.37, 0.26);
            matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(90)));
            bagModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfBagModel.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);
            matrices.pop();


            /*matrices.push();

            matrices.translate(-0.3, -0.37 + this.wolfTorso.pivotY - 14, 0.26);
            //matrices.scale(0.35f, 0.35f, 0.35f); // Scale to fit on the wolf

            // Rotate to the wolf side
            matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(90)));

            //VertexConsumer vertexConsumer = vertexConsumers.getBuffer(chestModel.getRenderLayer());

            // Render the chest model
            chestModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfChestModel.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);

            matrices.pop();*/

            matrices.push();

            matrices.translate(0.3, -0.37, 0.45);
            //matrices.scale(0.35f, 0.35f, 0.35f); // Scale to fit on the wolf

            // Rotate to the wolf side
            matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(-90)));

            //VertexConsumer vertexConsumerLeft = vertexConsumers.getBuffer(chestModel.getRenderLayer());

            // Render the chest model
            chestModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfChestModel.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);

            matrices.pop();
        }
    }
}
