package github.meloweh.wolfcompanion.mixin.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntityRenderer.class)
public abstract class WolfEntityRendererMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void addCustomLayer(EntityRendererFactory.Context context, CallbackInfo ci) {

        WolfEntityRenderer wolfRenderer = (WolfEntityRenderer) (Object) this;
        //wolfRenderer.render();
        //wolfRenderer.addFeature(new WolfPixelFeatureRenderer(wolfRenderer));
        //this.(new WolfChestFeatureRenderer((WolfEntityRenderer) (Object) this, new CustomChestRenderer(context)));
    }
}

