package github.meloweh.wolfcompanion.mixin;

import com.google.common.collect.Lists;
import github.meloweh.wolfcompanion.registry.ModItems;
import github.meloweh.wolfcompanion.util.EnchantmentHelperHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(EnchantmentMenu.class)
public class EnchantmentScreenHandlerMixin {
    @Final
    @Shadow
    private RandomSource random;

    @Final
    @Shadow
    private DataSlot enchantmentSeed;

    @Unique
    public List<EnchantmentInstance> generateEnchantments(RandomSource random, ItemStack stack, int level) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        Item item = stack.getItem();
        int enchantmentValue = item.getEnchantmentValue();
        if (enchantmentValue <= 0) {
            return list;
        } else {
            level += 1 + random.nextInt(enchantmentValue / 4 + 1) + random.nextInt(enchantmentValue / 4 + 1);
            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            level = Mth.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
            List<EnchantmentInstance> list2 = EnchantmentHelperHelper.getPossibleWolfArmorEntries(level, true);
            Optional<EnchantmentInstance> var10000 = WeightedRandom.getRandomItem(random, list2);
            var10000.ifPresent(list::add);

            while (random.nextInt(50) <= level) {
                var10000 = WeightedRandom.getRandomItem(random, list2);
                var10000.ifPresent(list::add);
                level /= 2;
            }

            return list;
        }
    }

    @Inject(method = "getEnchantmentList", at = @At("HEAD"), cancellable = true)
    public void changeGenerateEnchantments(ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (stack.is(ModItems.WOLF_ARMOR)) {
            this.random.setSeed(this.enchantmentSeed.get() + slot);
            List<EnchantmentInstance> list = generateEnchantments(this.random, stack, level);

            cir.setReturnValue(list);
            cir.cancel();
        }
    }
}
