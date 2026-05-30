package github.meloweh.wolfcompanion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.client.accessor.WolfEntityModelAccessor;
import github.meloweh.wolfcompanion.client.model.WolfBagModelV2;
import github.meloweh.wolfcompanion.util.WolfArmorHelper;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class CustomWolfEntityRenderer extends WolfRenderer {
    private static final ResourceLocation WOLF_ARMOR_TEXTURE =
            WolfCompanion.id("textures/entity/wolf_armor.png");
    private static final ResourceLocation WOLF_ARMOR_OVERLAY_TEXTURE =
            WolfCompanion.id("textures/entity/wolf_armor_overlay.png");
    private static final ResourceLocation WOLF_ARMOR_CRACKINESS_LOW_TEXTURE =
            WolfCompanion.id("textures/entity/wolf_armor_crackiness_low.png");
    private static final ResourceLocation WOLF_ARMOR_CRACKINESS_MEDIUM_TEXTURE =
            WolfCompanion.id("textures/entity/wolf_armor_crackiness_medium.png");
    private static final ResourceLocation WOLF_ARMOR_CRACKINESS_HIGH_TEXTURE =
            WolfCompanion.id("textures/entity/wolf_armor_crackiness_high.png");

    public CustomWolfEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new WolfCompanionFeatureRenderer(this, context.getItemInHandRenderer()));
    }

    private static final class WolfCompanionFeatureRenderer extends RenderLayer<Wolf, WolfModel<Wolf>> {
        private final ModelPart wolfTorso;
        private final ModelPart wolfHead;
        private final WolfBagModelV2 bagModel;
        private final ItemInHandRenderer heldItemRenderer;

        private WolfCompanionFeatureRenderer(RenderLayerParent<Wolf, WolfModel<Wolf>> context,
                                            ItemInHandRenderer heldItemRenderer) {
            super(context);
            WolfModel<Wolf> model = context.getModel();
            this.wolfTorso = ((WolfEntityModelAccessor) model).getTorso();
            this.wolfHead = ((WolfEntityModelAccessor) model).getHead();
            this.bagModel = new WolfBagModelV2(WolfBagModelV2.getTexturedModelData().bakeRoot());
            this.heldItemRenderer = heldItemRenderer;
        }

        @Override
        public void render(PoseStack poseStack,
                           MultiBufferSource buffers,
                           int light,
                           Wolf wolf,
                           float limbSwing,
                           float limbSwingAmount,
                           float partialTicks,
                           float ageInTicks,
                           float netHeadYaw,
                           float headPitch) {
            if (!wolf.isTame()) {
                return;
            }

            renderBag(poseStack, buffers, light, wolf);
            renderArmor(poseStack, buffers, light, wolf);
            renderHeldItem(poseStack, buffers, light, wolf, partialTicks, netHeadYaw, headPitch);
        }

        private void renderArmor(PoseStack poseStack, MultiBufferSource buffers, int light, Wolf wolf) {
            ItemStack armorStack = WolfArmorHelper.getArmorStack(wolf);
            if (!WolfArmorHelper.isWolfArmor(armorStack)) {
                return;
            }

            renderArmorTexture(poseStack, buffers, light, WOLF_ARMOR_TEXTURE);
            renderArmorTexture(poseStack, buffers, light, WOLF_ARMOR_OVERLAY_TEXTURE);

            ResourceLocation crackinessTexture = getCrackinessTexture(armorStack);
            if (crackinessTexture != null) {
                renderArmorTexture(poseStack, buffers, light, crackinessTexture);
            }
        }

        private void renderArmorTexture(PoseStack poseStack, MultiBufferSource buffers, int light, ResourceLocation texture) {
            VertexConsumer armor = buffers.getBuffer(RenderType.entityCutoutNoCull(texture));
            this.getParentModel().renderToBuffer(poseStack, armor, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        private static ResourceLocation getCrackinessTexture(ItemStack armorStack) {
            if (!armorStack.isDamageableItem() || !armorStack.isDamaged()) {
                return null;
            }

            float damageRatio = (float) armorStack.getDamageValue() / (float) armorStack.getMaxDamage();
            if (damageRatio < 0.33F) {
                return WOLF_ARMOR_CRACKINESS_LOW_TEXTURE;
            }
            if (damageRatio < 0.66F) {
                return WOLF_ARMOR_CRACKINESS_MEDIUM_TEXTURE;
            }
            return WOLF_ARMOR_CRACKINESS_HIGH_TEXTURE;
        }

        private void renderBag(PoseStack poseStack, MultiBufferSource buffers, int light, Wolf wolf) {
            if (!((WolfEntityProvider) wolf).hasChestEquipped()) {
                return;
            }

            poseStack.pushPose();
            bagModel.copyTransform(wolfTorso);
            bagModel.renderToBuffer(
                    poseStack,
                    buffers.getBuffer(RenderType.entitySolid(WolfBagModelV2.TEXTURE_LOCATION)),
                    light,
                    OverlayTexture.NO_OVERLAY,
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0F
            );

            if (!WolfArmorHelper.hasArmor(wolf)) {
                VertexConsumer straps = buffers.getBuffer(RenderType.entityCutoutNoCull(WolfBagModelV2.STRAP_LAYER_TEXTURE));
                this.getParentModel().renderToBuffer(poseStack, straps, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            poseStack.popPose();
        }

        private void renderHeldItem(PoseStack poseStack,
                                    MultiBufferSource buffers,
                                    int light,
                                    Wolf wolf,
                                    float partialTicks,
                                    float netHeadYaw,
                                    float headPitch) {
            ItemStack itemStack = wolf.getItemBySlot(EquipmentSlot.MAINHAND);
            if (itemStack.isEmpty()) {
                return;
            }

            poseStack.pushPose();
            boolean sleeping = wolf.isSleeping();
            boolean baby = wolf.isBaby();

            if (baby) {
                poseStack.scale(0.75F, 0.75F, 0.75F);
                poseStack.translate(0.0F, 0.5F, 0.209375F);
            }

            poseStack.translate(wolfHead.x / 16.0F, wolfHead.y / 16.0F, wolfHead.z / 16.0F);
            float roll = wolf.getBodyRollAngle(partialTicks, 0f) + wolf.getHeadRollAngle(partialTicks);
            poseStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));
            poseStack.mulPose(Axis.ZP.rotation(roll));

            if (baby) {
                if (sleeping) {
                    poseStack.translate(0.4F, 0.26F, 0.15F);
                } else {
                    poseStack.translate(0.06F, 0.26F, -0.5F);
                }
            } else if (sleeping) {
                poseStack.translate(0.46F, 0.26F, 0.22F);
            } else {
                poseStack.translate(0.06F, 0.13F, -0.4F);
            }

            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            if (sleeping) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }

            heldItemRenderer.renderItem(wolf, itemStack, ItemDisplayContext.GROUND, false, poseStack, buffers, light);
            poseStack.popPose();
        }
    }
}
