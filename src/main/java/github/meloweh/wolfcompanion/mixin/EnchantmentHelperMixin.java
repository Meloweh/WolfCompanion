package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.util.EnchantmentHelperHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin{
    @Inject(method = "getPossibleEntries", at = @At("HEAD"), cancellable = true)
    private static void injectPossibleEntries(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (stack.getItem() instanceof AnimalArmorItem) {
            List<EnchantmentLevelEntry> list = EnchantmentHelperHelper.getPossibleWolfArmorEntries(level, possibleEnchantments);
            cir.setReturnValue(list);
            cir.cancel();
        }

    }
}
