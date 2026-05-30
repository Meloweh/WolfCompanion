package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.util.EnchantmentHelperHelper;
import github.meloweh.wolfcompanion.registry.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin{
    @Inject(method = "getAvailableEnchantmentResults", at = @At("HEAD"), cancellable = true)
    private static void injectPossibleEntries(int level, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (stack.is(ModItems.WOLF_ARMOR)) {
            List<EnchantmentInstance> list = EnchantmentHelperHelper.getPossibleWolfArmorEntries(level, allowTreasure);
            cir.setReturnValue(list);
            cir.cancel();
        }

    }
}
