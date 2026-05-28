package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.util.ConfigManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TameableEntity.class)
public class TameableEntityMixin {
    @Unique
    private TameableEntity self;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        this.self = (TameableEntity) (Object) this;
    }

    /*@Inject(method = "tryTeleportToOwner", at = @At("HEAD"), cancellable = true)
    public void tryTeleportToOwner(CallbackInfo ci) {
        ci.cancel();
    }*/

    @Inject(method = "shouldTryTeleportToOwner", at = @At("TAIL"), cancellable = true)
    public void shouldTryTeleportToOwner(CallbackInfoReturnable<Boolean> cir) {
        if (this.self instanceof WolfEntity wolf) {
            LivingEntity livingEntity = wolf.getOwner();
            //cir.setReturnValue(livingEntity != null && wolf.squaredDistanceTo(wolf.getOwner()) >= (double)144.0F);
            if (ConfigManager.config.allowTeleport) {
                cir.setReturnValue(livingEntity != null && wolf.squaredDistanceTo(wolf.getOwner()) >= ConfigManager.config.teleportAtDistance);
            } else {
                cir.setReturnValue(false);
            }

        }

    }
}
