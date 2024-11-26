package github.meloweh.wolfcompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
//    @ModifyReturnValue(method = "isAcceptableItem", at = @At("RETURN"))
//    public boolean isAcceptableItem(boolean original, ItemStack itemStack) {
//        final Enchantment enchantment = (Enchantment) (Object) this;
//
//        //if ()
//    }
}
