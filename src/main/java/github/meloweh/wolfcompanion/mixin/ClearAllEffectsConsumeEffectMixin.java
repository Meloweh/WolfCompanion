package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.effects.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClearAllEffectsConsumeEffect.class)
public class ClearAllEffectsConsumeEffectMixin {
    private static final ThreadLocal<StatusEffectInstance> SAVED = new ThreadLocal<>();

    @Inject(method = "onConsume", at = @At("HEAD"))
    private void mod$save(World world, ItemStack stack, LivingEntity user, CallbackInfoReturnable<Boolean> cir) {

        StatusEffectInstance inst = null;// = user.getStatusEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY);

        for (int i = 1; i < ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY.length; i++) {
            inst = user.getStatusEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i]);
            if (inst == null) continue;
            SAVED.set(new StatusEffectInstance(
                    ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i],
                    inst.getDuration(),
                    inst.getAmplifier(),
                    inst.isAmbient(),
                    inst.shouldShowParticles(),
                    inst.shouldShowIcon(),
                    null
            ));

        }

        if (inst == null) {
            SAVED.remove();
        }
    }

    @Inject(method = "onConsume", at = @At("TAIL"))
    private void mod$restore(World world, ItemStack stack, LivingEntity user, CallbackInfoReturnable<Boolean> cir) {
        StatusEffectInstance saved = SAVED.get();
        SAVED.remove();
        if (saved != null) {
            user.addStatusEffect(saved);
        }
    }
}

