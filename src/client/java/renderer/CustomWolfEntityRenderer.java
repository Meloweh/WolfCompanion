package renderer;

import accessor.WolfEntityRenderStateProvider;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ModelTransformationMode;

public class CustomWolfEntityRenderer extends WolfEntityRenderer {
    public CustomWolfEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.addFeature(new WolfBagFeatureRenderer(this));
        this.addFeature(new WolfItemFeatureRenderer(this));
    }

    @Override
    public void updateRenderState(WolfEntity wolfEntity, WolfEntityRenderState wolfEntityRenderState, float f) {
        super.updateRenderState(wolfEntity, wolfEntityRenderState, f);
        //this.itemModelResolver.updateForLivingEntity(wolfEntityRenderState.headItemRenderState, wolfEntity.getMainHandStack(), ModelTransformationMode.GROUND, false, wolfEntity);
        WolfEntityProvider w = (WolfEntityProvider) wolfEntity;
        final WolfEntityRenderStateProvider provider = (WolfEntityRenderStateProvider) wolfEntityRenderState;
        provider.updateRenderState__(wolfEntity, provider, this.itemModelResolver);
        provider.setChestEquipped__(w.hasChestEquipped());
    }
}
