package renderer;

import accessor.WolfEntityRenderStateProvider;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.entity.passive.WolfEntity;

public class CustomWolfEntityRenderer extends WolfEntityRenderer {
    public CustomWolfEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.addFeature(new WolfBagFeatureRenderer(this));
        this.addFeature(new WolfItemFeatureRenderer(this, context.getItemRenderer()));
    }


    @Override
    public void updateRenderState(WolfEntity wolfEntity, WolfEntityRenderState wolfEntityRenderState, float f) {
        super.updateRenderState(wolfEntity, wolfEntityRenderState, f);
        WolfEntityProvider w = (WolfEntityProvider) wolfEntity;
        final WolfEntityRenderStateProvider provider = (WolfEntityRenderStateProvider) wolfEntityRenderState;
        provider.setChestEquipped__(w.hasChestEquipped());
    }
}
