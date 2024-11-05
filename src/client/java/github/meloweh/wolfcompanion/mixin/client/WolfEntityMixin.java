package github.meloweh.wolfcompanion.mixin.client;

import accessor.WolfEntityModelAccessor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin {
    ModelPart wolfTorso;

    // Inject into the end of the tick method
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(CallbackInfo ci) {
        // Custom behavior here
        // For example, print something to the console or add a new behavior
        //System.out.println("WolfEntity tick ended.");

        // Additional functionality can be added here
        // For example, applying custom effects, logging data, etc.

    }
}
