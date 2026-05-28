package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.effects.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClearAllStatusEffectsConsumeEffect.class)
public class ClearAllEffectsConsumeEffectMixin {
    private static final ThreadLocal<MobEffectInstance> SAVED = new ThreadLocal<>();

    @Inject(method = "apply", at = @At("HEAD"))
    private void mod$save(Level world, ItemStack stack, LivingEntity user, CallbackInfoReturnable<Boolean> cir) {

        MobEffectInstance inst = null;// = user.getStatusEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY);

        for (int i = 1; i < ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY.length; i++) {
            inst = user.getEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i]);
            if (inst == null) continue;
            SAVED.set(new MobEffectInstance(
                    ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i],
                    inst.getDuration(),
                    inst.getAmplifier(),
                    inst.isAmbient(),
                    inst.isVisible(),
                    inst.showIcon(),
                    null
            ));

        }

        if (inst == null) {
            SAVED.remove();
        }
    }

    @Inject(method = "apply", at = @At("TAIL"))
    private void mod$restore(Level world, ItemStack stack, LivingEntity user, CallbackInfoReturnable<Boolean> cir) {
        MobEffectInstance saved = SAVED.get();
        SAVED.remove();
        if (saved != null) {
            user.addEffect(saved);
        }
    }
}

