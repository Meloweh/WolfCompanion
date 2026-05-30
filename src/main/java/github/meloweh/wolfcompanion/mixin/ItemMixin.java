package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.registry.ModItems;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getEnchantmentValue", at = @At("HEAD"), cancellable = true)
    private void increaseWolfArmorEnchantability(CallbackInfoReturnable<Integer> cir) {
        Item self = (Item) (Object) this;

        if (self == ModItems.WOLF_ARMOR) {
            cir.setReturnValue(15);
            cir.cancel();
        }
    }
}
