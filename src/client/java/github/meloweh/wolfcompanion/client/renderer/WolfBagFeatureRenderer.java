package github.meloweh.wolfcompanion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import github.meloweh.wolfcompanion.client.accessor.WolfEntityModelAccessor;
import github.meloweh.wolfcompanion.client.accessor.WolfEntityRenderStateProvider;
import github.meloweh.wolfcompanion.client.model.WolfBagModelV2;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public final class WolfBagFeatureRenderer extends RenderLayer<WolfRenderState, WolfModel> {
    final private ModelPart wolfTorso;
    final private WolfBagModelV2 bagModelV2;

    public WolfBagFeatureRenderer(RenderLayerParent<WolfRenderState, WolfModel> featureRendererContext) {
        super(featureRendererContext);

        WolfModel model = featureRendererContext.getModel();

        this.wolfTorso = ((WolfEntityModelAccessor) model).getTorso();

        final ModelPart bagRootV2 = WolfBagModelV2.getTexturedModelData().bakeRoot();
        this.bagModelV2 = new WolfBagModelV2(bagRootV2);

    }

    private final Map<Long, WolfBagModelV2> cache = new HashMap<>();

    private WolfBagModelV2 getBagModel(long entityId) {
        return cache.computeIfAbsent(entityId, id -> new WolfBagModelV2(WolfBagModelV2.getTexturedModelData().bakeRoot()));
    }

    @Override
    public void submit(PoseStack matrices,
                       SubmitNodeCollector queue,
                       int light,
                       WolfRenderState state,
                       float limbAngle,
                       float limbDistance) {

        final WolfEntityRenderStateProvider provider = (WolfEntityRenderStateProvider) state;
        if (!provider.hasChestEquipped__()) return;

        matrices.pushPose();

        final WolfBagModelV2 bag = getBagModel(provider.getEntityId__());
        bag.copyTransform(wolfTorso);

        queue.submitModelPart(
                bag.root(),
                matrices,
                RenderTypes.entitySolid(WolfBagModelV2.TEXTURE_LOCATION),
                light,
                OverlayTexture.NO_OVERLAY,
                null
        );

        bagModelV2.resetPose();

        if (state.bodyArmorItem.isEmpty()) {
            queue.order(1)
                    .submitModel(
                            this.getParentModel(),
                            state,
                            matrices,
                            RenderTypes.entityCutout(WolfBagModelV2.STRAP_LAYER_TEXTURE),
                            light,
                            OverlayTexture.NO_OVERLAY, -1, (TextureAtlasSprite)null,
                            state.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);

        }

        matrices.popPose();
    }
}
