package github.meloweh.wolfcompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.util.EnchantmentHelperHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @ModifyReturnValue(method = "isAcceptableItem", at = @At("RETURN"))
    public boolean isAcceptableItem(boolean original, ItemStack itemStack) {
        final Enchantment enchantment = (Enchantment) (Object) this;

        if(itemStack.isOf(Items.WOLF_ARMOR)) {
            return EnchantmentHelperHelper.getWolfArmorEnchantments().stream()
                    .anyMatch(e -> WolfCompanion.isSameEnchantment(enchantment, e));
//            if (WolfCompanion.isSameEnchantment(enchantment, Enchantments.UNBREAKING)) {
//                return true;
//            }
//            if (WolfCompanion.isSameEnchantment(enchantment, Enchantments.MENDING)) {
//                return true;
//            }
        }

        return original;
    }
}
