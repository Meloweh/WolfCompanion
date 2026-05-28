package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.effects.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ClearAllEffectsConsumeEffectMixin {
    private static final ThreadLocal<StatusEffectInstance> SAVED = new ThreadLocal<>();

    @Inject(method = "clearStatusEffects", at = @At("HEAD"))
    private void mod$save(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getWorld().isClient) {
            SAVED.remove();
            return;
        }

        for (int i = 1; i < ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY.length; i++) {
            StatusEffectInstance inst = self.getStatusEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i]);
            if (inst != null) {
                SAVED.set(new StatusEffectInstance(
                        ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i],
                        inst.getDuration(),
                        inst.getAmplifier(),
                        inst.isAmbient(),
                        inst.shouldShowParticles(),
                        inst.shouldShowIcon(),
                        null
                ));
                return;
            }
        }

        SAVED.remove();
    }

    @Inject(method = "clearStatusEffects", at = @At("RETURN"))
    private void mod$restore(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        StatusEffectInstance saved = SAVED.get();
        SAVED.remove();

        if (saved != null && !self.getWorld().isClient) {
            self.addStatusEffect(saved);
        }
    }
}
