package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.config.WolfCompanionConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TamableAnimal.class)
public class TameableEntityMixin {
    @Unique
    private TamableAnimal self;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        this.self = (TamableAnimal) (Object) this;
    }

    @Inject(method = "shouldTryTeleportToOwner", at = @At("TAIL"), cancellable = true)
    public void shouldTryTeleportToOwner(CallbackInfoReturnable<Boolean> cir) {
        if (this.self instanceof Wolf wolf) {
            LivingEntity livingEntity = wolf.getOwner();
            if (WolfCompanionConfig.current().allowTeleport) {
                cir.setReturnValue(livingEntity != null && wolf.distanceToSqr(wolf.getOwner()) >= WolfCompanionConfig.current().teleportAtDistance);
            } else {
                cir.setReturnValue(false);
            }

        }

    }
}
