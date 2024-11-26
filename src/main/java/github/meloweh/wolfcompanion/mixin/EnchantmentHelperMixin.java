package github.meloweh.wolfcompanion.mixin;

import com.google.common.collect.Lists;
import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getPossibleEntries", at = @At("HEAD"), cancellable = true)
    private static void injectPossibleEntries(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (stack.getItem() instanceof AnimalArmorItem) {
            //System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEeee");
            List<EnchantmentLevelEntry> list = Lists.newArrayList();
            possibleEnchantments.filter((enchantment) -> {
                boolean hasThorns = WolfCompanion.isSameEnchantment(enchantment.value(), Enchantments.THORNS);
                boolean hasMending = WolfCompanion.isSameEnchantment(enchantment.value(), Enchantments.MENDING);
                boolean hasUnbreaking = WolfCompanion.isSameEnchantment(enchantment.value(), Enchantments.UNBREAKING);

                return hasThorns || hasMending || hasUnbreaking;
            }).forEach((enchantmentx) -> {
                Enchantment enchantment = (Enchantment)enchantmentx.value();

                for(int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); --j) {
                    if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
                        list.add(new EnchantmentLevelEntry(enchantmentx, j));
                        break;
                    }
                }

            });

            cir.setReturnValue(list);
            cir.cancel();  // Prevent further method execution
        }

    }
}
