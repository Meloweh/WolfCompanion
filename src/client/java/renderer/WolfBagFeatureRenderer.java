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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class WolfBagFeatureRenderer extends FeatureRenderer<WolfEntityRenderState, WolfEntityModel> {
    final private ModelPart wolfTorso;
    final private ModelPart wolfHead;
    final private WolfBagModelV2 bagModelV2;
    private final ItemRenderer heldItemRenderer;

    public WolfBagFeatureRenderer(FeatureRendererContext<WolfEntityRenderState, WolfEntityModel> featureRendererContext, ItemRenderer itemRenderer) {
        super(featureRendererContext);

        WolfEntityModel model = featureRendererContext.getModel();

        this.wolfTorso = ((WolfEntityModelAccessor) model).getTorso();
        //this.wolfHead = ((WolfEntityModelAccessor) model).getRealHead();
        this.wolfHead = ((WolfEntityModelAccessor) model).getHead();

        final ModelPart bagRootV2 = WolfBagModelV2.getTexturedModelData().createModel();
        this.bagModelV2 = new WolfBagModelV2(bagRootV2);

        this.heldItemRenderer = itemRenderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntityRenderState state, float limbAngle, float limbDistance) {
        //if (!state.isTamed() || !((WolfEntityProvider)state).hasChestEquipped()) return;

        if (((WolfEntityProvider) state).hasChestEquipped()) {
            matrices.push();
            bagModelV2.copyTransform(wolfTorso);
            bagModelV2.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfBagModelV2.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);
            if (!state.bodyArmor.isEmpty()) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(WolfBagModelV2.STRAP_LAYER_TEXTURE));
                this.getContextModel().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            }

            matrices.pop();
        }


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
        //m = wolfHead.getHeadRoll(h);
        //m = foxEntity.getHeadRoll(h);
        m = state.getBegAnimationProgress(partialTicks);
        //if (state.isWet())
        m = state.getShakeAnimationProgress(partialTicks, 0f) + state.getBegAnimationProgress(partialTicks);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(netHeadYaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(headPitch));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(m));
        //matrices.multiply(RotationAxis.POSITIVE_Y.rotation(m));
        //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m));
        if (state.isBaby()) {
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

        ItemStack itemStack = state.getEquippedStack(EquipmentSlot.MAINHAND);
        //itemStack = Items.COOKED_BEEF.getDefaultStack();

        this.heldItemRenderer.renderItem(state, itemStack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light);
        matrices.pop();
    }
}
