package github.meloweh.wolfcompanion.mixin;

import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AnimalArmorItem.class)
public class WolfArmorMixin {
//    @Inject(method = "isEnchantable", at = @At("RETURN"), cancellable = true)
//    public void makeWolfArmorEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
//        final AnimalArmorItem self = (AnimalArmorItem) (Object) this;
//
//        if (self.getType().equals(AnimalArmorItem.Type.CANINE)) {
//            cir.setReturnValue(true);
//            cir.cancel();
//        }
//    }

    @ModifyArg(method = "<init>(Lnet/minecraft/item/equipment/ArmorMaterial;Lnet/minecraft/item/AnimalArmorItem$Type;Lnet/minecraft/item/Item$Settings;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;<init>(Lnet/minecraft/item/Item$Settings;)V"))
    private static Item.Settings makeEnchantable(Item.Settings settings) {
        settings.enchantable(15);
        return settings;
    }


}
