package github.meloweh.wolfcompanion.mixin;

import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin {
    private boolean customRenderFlag;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(CallbackInfo ci) {
        // Set the customRenderFlag based on logic
        this.customRenderFlag = true; // example condition
    }

    public boolean getCustomRenderFlag() {
        return customRenderFlag;
    }
}

