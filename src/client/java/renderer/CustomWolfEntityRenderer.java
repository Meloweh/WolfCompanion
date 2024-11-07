package renderer;

//import github.meloweh.wolfcompanion.model.ExampleChestModel;
import accessor.WolfEntityModelAccessor;
import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.model.WolfBagModel;
import github.meloweh.wolfcompanion.model.WolfBagModelV2;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

public class CustomWolfEntityRenderer extends WolfEntityRenderer {
    //static float degree = 45;
    public CustomWolfEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        // Add your custom feature renderer
        System.out.println("CustomWolfEntityRenderer initialized!");
        this.addFeature(new CustomFeatureRenderer(this));
    }

    static float f = 0;

    // Custom feature renderer class
    private static class CustomFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
        private final WolfChestModel chestModel;
        private final WolfBagModel bagModel;
        final private ModelPart wolfTorso;
        final private WolfBagModelV2 bagModelV2;

        public CustomFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context) {
            super(context);

            WolfEntityModel<WolfEntity> model = context.getModel();


            this.wolfTorso = ((WolfEntityModelAccessor) model).getTorso();

            //this.chestModel = new ExampleChestModel(context.getLayerModelPart(ExampleChestModel.LAYER_LOCATION));
            final ModelPart chestRoot = WolfChestModel.getTexturedModelData().createModel();//ChestModelUtil.createChestRoot();
            this.chestModel = new WolfChestModel(chestRoot);
            
            final ModelPart bagRoot = WolfBagModel.getTexturedModelData().createModel();
            this.bagModel = new WolfBagModel(bagRoot);

            final ModelPart bagRootV2 = WolfBagModelV2.getTexturedModelData().createModel();
            this.bagModelV2 = new WolfBagModelV2(bagRootV2);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isTamed()) return;
            /*matrices.push();

            bagModel.animateModel(entity, this.wolfTorso, limbSwing, limbSwingAmount, partialTicks);
            float x = 0.175f, y = 0f, z = 0.15f;

            float degree = 89.9f;
            float fac = 0.12f;

            if (entity.isInSittingPose()) {
                y += fac * MathHelper.sin(degree);
                z += fac * MathHelper.cos(degree);
            }
            matrices.translate(x, y, z);
            bagModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfBagModel.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);
            matrices.pop();

            matrices.push();

            bagModel.flipRotation(entity, this.wolfTorso);
            matrices.scale(-1f, 1f, 1f);
            matrices.translate(x, y, z);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_FRONT);
            bagModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfBagModel.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);
            GL11.glCullFace(GL11.GL_BACK);
            GL11.glDisable(GL11.GL_CULL_FACE);
            matrices.pop();*/

/*
            matrices.push();

            bagModel.animateModel(entity, this.wolfTorso, limbSwing, limbSwingAmount, ageInTicks);
            matrices.translate(-0.15f,0.1f, 0.15f);
            //bagModel.copyTransform(wolfTorso);
            //bagModel.offset(1, 0, 0);
            //bagModel.rotate();
            bagModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfBagModel.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
            */

            matrices.push();

            //bagModelV2.animateModel(entity, this.wolfTorso, limbSwing, limbSwingAmount, ageInTicks);
            bagModelV2.copyTransform(wolfTorso);
            //GL11.glEnable(GL11.GL_CULL_FACE);
            //GL11.glCullFace(GL11.GL_FRONT);
            bagModelV2.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfBagModelV2.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);
            //GL11.glCullFace(GL11.GL_BACK);
            //GL11.glDisable(GL11.GL_CULL_FACE);
            matrices.pop();

        }
    }
}
