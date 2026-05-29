package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.effects.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ClearAllEffectsConsumeEffectMixin {
    private static final ThreadLocal<MobEffectInstance> SAVED = new ThreadLocal<>();

    @Inject(method = "removeAllEffects", at = @At("HEAD"))
    private void mod$save(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide()) {
            SAVED.remove();
            return;
        }

        for (int i = 1; i < ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY.length; i++) {
            MobEffectInstance inst = self.getEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i]);
            if (inst != null) {
                SAVED.set(new MobEffectInstance(
                        ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i],
                        inst.getDuration(),
                        inst.getAmplifier(),
                        inst.isAmbient(),
                        inst.isVisible(),
                        inst.showIcon(),
                        null
                ));
                return;
            }
        }

        SAVED.remove();
    }

    @Inject(method = "removeAllEffects", at = @At("RETURN"))
    private void mod$restore(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        MobEffectInstance saved = SAVED.get();
        SAVED.remove();
        if (saved != null && !self.level().isClientSide()) {
            self.addEffect(saved);
        }
    }
}
