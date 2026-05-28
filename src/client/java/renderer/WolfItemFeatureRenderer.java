package renderer;

import accessor.WolfEntityModelAccessor;
import accessor.WolfEntityRenderStateProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import github.meloweh.wolfcompanion.model.WolfBagModelV2;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class WolfItemFeatureRenderer extends RenderLayer<WolfRenderState, WolfModel>  {
    final private ModelPart wolfHead;

    public WolfItemFeatureRenderer(RenderLayerParent<WolfRenderState, WolfModel> context) {
        super(context);

        final WolfModel model = context.getModel();
        this.wolfHead = ((WolfEntityModelAccessor) model).getHead();
    }

    public float getShakeAnimationProgress(float tickDelta, float f) {
        float g = (tickDelta + f) / 1.8F;
        if (g < 0.0F) {
            g = 0.0F;
        } else if (g > 1.0F) {
            g = 1.0F;
        }

        return Mth.sin(g * 3.1415927F) * Mth.sin(g * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
    }

    public float getBegAnimationProgress(float tickDelta) {
        return tickDelta * 0.15F * 3.1415927F;
    }

    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, WolfRenderState state, float limbAngle, float limbDistance) {

    //}

    //@Override
    //public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntityRenderState state, float limbAngle, float limbDistance) {
        final WolfEntityRenderStateProvider customState = (WolfEntityRenderStateProvider) state;
        final ItemStackRenderState itemRenderState = customState.getItemRenderState__(); //state.itemRenderState;

        //final ItemStack itemStack = customState.getWolf__().getMainHandStack();

        if (!itemRenderState.isEmpty()) {
            boolean bl = false;
            boolean bl2 = state.isBaby;
            matrices.pushPose();
            float m;
            if (bl2) {
                m = 0.75F;
                matrices.scale(0.75F, 0.75F, 0.75F);
                matrices.translate(0.0F, 0.5F, 0.209375F);
            }

            matrices.translate(wolfHead.x / 16.0F, wolfHead.y / 16.0F, wolfHead.z / 16.0F);
            m = state.headRollAngle + getShakeAnimationProgress(state.shakeAnim, 0f);
            //m = state.shakeProgress;

            matrices.mulPose(Axis.YP.rotationDegrees(limbAngle));
            matrices.mulPose(Axis.XP.rotationDegrees(limbDistance));
            matrices.mulPose(Axis.ZP.rotation(m));
            if (state.isBaby) {
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

            matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
            if (bl) {
                matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }

            itemRenderState.submit(matrices, queue, light, OverlayTexture.NO_OVERLAY, state.outlineColor);

            matrices.popPose();
        }
    }
}
