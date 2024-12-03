package renderer;

import accessor.WolfEntityModelAccessor;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.model.WolfBagModelV2;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;

public class WolfStripFeatureRenderer extends FeatureRenderer<WolfEntityRenderState, WolfEntityModel> {
    final private ModelPart wolfHead;
    private final ItemRenderer heldItemRenderer;

    public WolfStripFeatureRenderer(FeatureRendererContext<WolfEntityRenderState, WolfEntityModel> featureRendererContext, ItemRenderer itemRenderer) {
        super(featureRendererContext);

        final WolfEntityModel model = featureRendererContext.getModel();
        this.wolfHead = ((WolfEntityModelAccessor) model).getHead();
        this.heldItemRenderer = itemRenderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntityRenderState state, float limbAngle, float limbDistance) {
        final BakedModel bakedModel = state.getMainHandItemModel();
        final ItemStack itemStack = state.getMainHandStack();

        if (bakedModel != null && !itemStack.isEmpty()) {
            boolean bl = false;
            boolean bl2 = state.baby;
            matrices.push();
            float m;
            if (bl2) {
                m = 0.75F;
                matrices.scale(0.75F, 0.75F, 0.75F);
                matrices.translate(0.0F, 0.5F, 0.209375F);
            }

            matrices.translate(wolfHead.pivotX / 16.0F, wolfHead.pivotY / 16.0F, wolfHead.pivotZ / 16.0F);
            m = state.begAnimationProgress;
            //if (state.isWet())
            m = state.shakeProgress;

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(limbAngle));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(limbDistance));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotation(m));
            if (state.baby) {
                if (bl) {
                    matrices.translate(0.4F, 0.26F, 0.15F);
                } else {
                    matrices.translate(0.06F, 0.26F, -0.5F);
                }
            } else if (bl) {
                matrices.translate(0.46F, 0.26F, 0.22F);
            } else {
                //matrices.translate(0.06F, 0.83F, -0.4F);
                matrices.translate(0.06F, 0.13F, -0.4F);
            }

            //if (!state.isSitting())
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            if (bl) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
            }

            //ItemStack itemStack = state.getMainHandStack();

            this.heldItemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, bakedModel);
            matrices.pop();
        }
    }
}
