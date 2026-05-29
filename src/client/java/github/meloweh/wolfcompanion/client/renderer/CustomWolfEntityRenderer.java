package github.meloweh.wolfcompanion.client.renderer;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.client.accessor.WolfEntityRenderStateProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.world.entity.animal.wolf.Wolf;

public final class CustomWolfEntityRenderer extends WolfRenderer {
    public CustomWolfEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new WolfBagFeatureRenderer(this));
        this.addLayer(new WolfItemFeatureRenderer(this));
    }

    @Override
    public void extractRenderState(Wolf wolfEntity, WolfRenderState wolfEntityRenderState, float f) {
        super.extractRenderState(wolfEntity, wolfEntityRenderState, f);
        WolfEntityProvider w = (WolfEntityProvider) wolfEntity;
        final WolfEntityRenderStateProvider provider = (WolfEntityRenderStateProvider) wolfEntityRenderState;
        provider.updateRenderState__(wolfEntity, provider, this.itemModelResolver);
        provider.setChestEquipped__(w.hasChestEquipped());
        provider.setEntityId__(wolfEntity.getId());
    }
}
