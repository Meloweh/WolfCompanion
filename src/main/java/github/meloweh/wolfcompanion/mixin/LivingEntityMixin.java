package github.meloweh.wolfcompanion.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.StackReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    /*@Inject(method = "getStackReference*", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<StackReference> cir) {
        cir.setReturnValue(cir.getReturnValue());
    }*/
    /*@Shadow
    private static StackReference getStackReference(LivingEntity entity, EquipmentSlot slot) {
        System.out.println("getStackReference should not appear");
        return null;
    }*/

    //@Shadow
    //protected abstract void dropInventory();

    @Accessor("dropInventory")
    protected abstract void publicDropInventory();

}

