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
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class WolfBagFeatureRenderer extends FeatureRenderer<WolfEntityRenderState, WolfEntityModel> {
    final private ModelPart wolfTorso;
    final private WolfBagModelV2 bagModelV2;

    public WolfBagFeatureRenderer(FeatureRendererContext<WolfEntityRenderState, WolfEntityModel> featureRendererContext) {
        super(featureRendererContext);

        WolfEntityModel model = featureRendererContext.getModel();

        this.wolfTorso = ((WolfEntityModelAccessor) model).getTorso();

        final ModelPart bagRootV2 = WolfBagModelV2.getTexturedModelData().createModel();
        this.bagModelV2 = new WolfBagModelV2(bagRootV2);

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntityRenderState state, float limbAngle, float limbDistance) {
        //if (((WolfEntityProvider) state).hasChestEquipped()) {
            matrices.push();
            bagModelV2.copyTransform(wolfTorso);
            bagModelV2.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(WolfBagModelV2.TEXTURE_LOCATION)), light, OverlayTexture.DEFAULT_UV);
            if (!state.bodyArmor.isEmpty()) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(WolfBagModelV2.STRAP_LAYER_TEXTURE));
                this.getContextModel().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            }

            matrices.pop();
        //}
    }
}
