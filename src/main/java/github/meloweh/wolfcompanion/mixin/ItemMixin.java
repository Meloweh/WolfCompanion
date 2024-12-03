package github.meloweh.wolfcompanion.mixin;

import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Settings.class)
public class ItemMixin {
//    @Inject(method = "enchantable", at = @At("HEAD"), cancellable = true)
//    public void increaseWolfArmorEnchantability(CallbackInfoReturnable<Integer> cir) {
//        final Item self = (Item) (Object) this;
//
//        self.ench
//
//        if (self instanceof AnimalArmorItem) {
//            final AnimalArmorItem animalItem = (AnimalArmorItem) self;
//
//
//            if (animalItem.getType().equals(AnimalArmorItem.Type.CANINE)) {
//                cir.setReturnValue(15);
//                cir.cancel();
//            }
//        }
//
//    }

}
