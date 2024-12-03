package renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;

public class CustomWolfEntityRenderer extends WolfEntityRenderer {
    public CustomWolfEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.addFeature(new WolfBagFeatureRenderer(this));
        this.addFeature(new WolfItemFeatureRenderer(this, context.getItemRenderer()));
    }
}
