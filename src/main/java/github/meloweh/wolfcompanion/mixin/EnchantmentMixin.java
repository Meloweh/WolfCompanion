package github.meloweh.wolfcompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.util.EnchantmentHelperHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @ModifyReturnValue(method = "canEnchant", at = @At("RETURN"))
    public boolean isAcceptableItem(boolean original, ItemStack itemStack) {
        final Enchantment enchantment = (Enchantment) (Object) this;

        if(itemStack.is(Items.WOLF_ARMOR)) {
            return EnchantmentHelperHelper.getWolfArmorEnchantments().stream()
                    .anyMatch(e -> WolfCompanion.isSameEnchantment(enchantment, e));
        }

        return original;
    }
}
