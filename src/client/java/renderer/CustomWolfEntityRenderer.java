package renderer;

//import github.meloweh.wolfcompanion.model.ExampleChestModel;
import github.meloweh.wolfcompanion.model.WolfChestModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

        public CustomFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context) {
            super(context);
            //this.chestModel = new ExampleChestModel(context.getLayerModelPart(ExampleChestModel.LAYER_LOCATION));
            final ModelPart chestRoot = WolfChestModel.getTexturedModelData().createModel();//ChestModelUtil.createChestRoot();
            this.chestModel = new WolfChestModel(chestRoot);
        }


        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            matrices.push();

            matrices.translate(-0.3, -0.37, 0.26);
            //matrices.scale(0.35f, 0.35f, 0.35f); // Scale to fit on the wolf

            // Rotate to the wolf side
            matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(90)));

            //VertexConsumer vertexConsumer = vertexConsumers.getBuffer(chestModel.getRenderLayer());

            // Render the chest model
            chestModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfChestModel.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);

            matrices.pop();

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
