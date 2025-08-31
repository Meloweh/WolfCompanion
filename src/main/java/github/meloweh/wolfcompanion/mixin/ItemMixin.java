package github.meloweh.wolfcompanion.mixin;

import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getEnchantability", at = @At("HEAD"), cancellable = true)
    public void increaseWolfArmorEnchantability(CallbackInfoReturnable<Integer> cir) {
        final Item self = (Item) (Object) this;

        if (self instanceof AnimalArmorItem) {
            final AnimalArmorItem animalItem = (AnimalArmorItem) self;

            if (animalItem.getType().equals(AnimalArmorItem.Type.CANINE)) {
                cir.setReturnValue(15);
                cir.cancel();
            }
        }

    }

}
