package github.meloweh.wolfcompanion.mixin;

import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalArmorItem.class)
public class WolfArmorMixin {
    @Inject(method = "isEnchantable", at = @At("RETURN"), cancellable = true)
    public void makeWolfArmorEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        final AnimalArmorItem self = (AnimalArmorItem) (Object) this;

        if (self.getType().equals(AnimalArmorItem.Type.CANINE)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }


}
