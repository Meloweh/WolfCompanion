package renderer;

import accessor.WolfEntityModelAccessor;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

@Deprecated
public class WolfHeldItemFeatureRenderer extends WolfEntityRenderer {
    public WolfHeldItemFeatureRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.addFeature(new CustomFeatureRenderer(this, context.getHeldItemRenderer()));
    }

    private static class CustomFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
        private final HeldItemRenderer heldItemRenderer;
        final private ModelPart wolfHead;

        public CustomFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context, HeldItemRenderer heldItemRenderer) {
            super(context);

            WolfEntityModel<WolfEntity> model = context.getModel();

            this.wolfHead = ((WolfEntityModelAccessor) model).getHead();
            this.heldItemRenderer = heldItemRenderer;
        }

        //public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, FoxEntity foxEntity,            float f,               float g,            float h,          float j,          float k, float l) {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isTamed() || !((WolfEntityProvider) entity).hasChestEquipped()) return;

            boolean bl = entity.isSleeping();
            boolean bl2 = entity.isBaby();
            matrices.push();
            float m;
            if (bl2) {
                m = 0.75F;
                matrices.scale(0.75F, 0.75F, 0.75F);
                matrices.translate(0.0F, 0.5F, 0.209375F);
            }

            matrices.translate(wolfHead.pivotX / 16.0F, wolfHead.pivotY / 16.0F, wolfHead.pivotZ / 16.0F);
            //m = wolfHead.getHeadRoll(h);
            //matrices.multiply(RotationAxis.POSITIVE_Z.rotation(m));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(netHeadYaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(headPitch));
            if (entity.isBaby()) {
                if (bl) {
                    matrices.translate(0.4F, 0.26F, 0.15F);
                } else {
                    matrices.translate(0.06F, 0.26F, -0.5F);
                }
            } else if (bl) {
                matrices.translate(0.46F, 0.26F, 0.22F);
            } else {
                matrices.translate(0.06F, 0.27F, -0.5F);
            }

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            if (bl) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
            }

            ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
            this.heldItemRenderer.renderItem(entity, itemStack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light);
            matrices.pop();

        }
    }
}
