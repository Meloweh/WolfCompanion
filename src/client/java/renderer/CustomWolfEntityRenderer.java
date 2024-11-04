package renderer;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.model.ExampleChestModel;
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

public class CustomWolfEntityRenderer extends WolfEntityRenderer {
    public CustomWolfEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        // Add your custom feature renderer
        System.out.println("CustomWolfEntityRenderer initialized!");
        this.addFeature(new CustomFeatureRenderer(this));
    }

    // Custom feature renderer class
    private static class CustomFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
        private final ExampleChestModel chestModel;

        public CustomFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context) {
            super(context);
            //this.chestModel = new ExampleChestModel(context.getLayerModelPart(ExampleChestModel.LAYER_LOCATION));
            final ModelPart chestRoot = ChestModelUtil.createChestRoot();
            this.chestModel = new ExampleChestModel(chestRoot);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            matrices.push();

            // Position and scale the chest model on the wolf’s back
            matrices.translate(0.22, 0.8, 0.4); // Adjust as needed to center on wolf's back
            matrices.scale(0.35f, 0.35f, 0.35f); // Scale to fit on the wolf

            // Bind texture if needed
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(chestModel.getRenderLayer());

            // Render the chest model
            //chestModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            chestModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(ExampleChestModel.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);

            matrices.pop();
        }
    }
}
