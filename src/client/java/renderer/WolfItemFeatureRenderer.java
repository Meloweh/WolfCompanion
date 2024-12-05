package renderer;

import accessor.WolfEntityModelAccessor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class WolfItemFeatureRenderer extends FeatureRenderer<WolfEntityRenderState, WolfEntityModel> {
    final private ModelPart wolfHead;

    public WolfItemFeatureRenderer(FeatureRendererContext<WolfEntityRenderState, WolfEntityModel> featureRendererContext) {
        super(featureRendererContext);

        final WolfEntityModel model = featureRendererContext.getModel();
        this.wolfHead = ((WolfEntityModelAccessor) model).getHead();
    }

    public float getShakeAnimationProgress(float tickDelta, float f) {
        float g = (tickDelta + f) / 1.8F;
        if (g < 0.0F) {
            g = 0.0F;
        } else if (g > 1.0F) {
            g = 1.0F;
        }

        return MathHelper.sin(g * 3.1415927F) * MathHelper.sin(g * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
    }

    public float getBegAnimationProgress(float tickDelta) {
        return tickDelta * 0.15F * 3.1415927F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntityRenderState state, float limbAngle, float limbDistance) {
        final ItemRenderState itemRenderState = state.itemRenderState;
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
            m = state.begAnimationProgress + getShakeAnimationProgress(state.shakeProgress, 0f);
            //m = state.shakeProgress;

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
                matrices.translate(0.06F, 0.13F, -0.4F);
            }

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            if (bl) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
            }

            itemRenderState.render(itemStack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, bakedModel);
            matrices.pop();
        }
    }
}
