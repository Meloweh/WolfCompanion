package renderer;

import accessor.WolfEntityModelAccessor;
import accessor.WolfEntityRenderStateProvider;
import github.meloweh.wolfcompanion.model.WolfBagModelV2;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.Map;

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

    private final Map<Long, WolfBagModelV2> cache = new HashMap<>();

    private WolfBagModelV2 getBagModel(long entityId) {
        return cache.computeIfAbsent(entityId, id -> new WolfBagModelV2(WolfBagModelV2.getTexturedModelData().createModel())); // build from baked root
    }

    @Override
    public void render(MatrixStack matrices,
                       OrderedRenderCommandQueue queue,
                       int light,
                       WolfEntityRenderState state,
                       float limbAngle,
                       float limbDistance) {

        final WolfEntityRenderStateProvider provider = (WolfEntityRenderStateProvider) state;
        if (!provider.hasChestEquipped__()) return;

        matrices.push();

        // Pose the bag model relative to the wolf torso
        //bagModelV2.copyTransform(wolfTorso);

        final WolfBagModelV2 bag = getBagModel(provider.getEntityId__());
        // copy pose directly, no matrix math
        //bag.getRootPart().setTransform(wolfTorso.getTransform());
        bag.copyTransform(wolfTorso);

        // 1) Bag body (ModelPart)
        queue.submitModelPart(
                bag.getRootPart(),
                matrices,
                RenderLayers.entitySolid(WolfBagModelV2.TEXTURE_LOCATION),
                light,
                OverlayTexture.DEFAULT_UV, // correct overlay
                null                       // Sprite (none)
        );

        bagModelV2.resetTransforms();

        if (state.bodyArmor.isEmpty()) {
            // 2) Straps overlay on the wolf (full EntityModel with current pose)
            queue.getBatchingQueue(1)
                    .submitModel(
                            this.getContextModel(),
                            state,
                            matrices,
                            RenderLayers.entityCutout(WolfBagModelV2.STRAP_LAYER_TEXTURE),
                            light,
                            OverlayTexture.DEFAULT_UV, -1, (Sprite)null,
                            state.outlineColor, (ModelCommandRenderer.CrumblingOverlayCommand)null);

        }

        matrices.pop();
    }



}
