package github.meloweh.wolfcompanion.mixin;

import com.google.common.collect.Lists;
import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.util.EnchantmentHelperHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Mixin(EnchantmentMenu.class)
public class EnchantmentScreenHandlerMixin {
    @Final
    @Shadow
    private RandomSource random;

    @Final
    @Shadow
    private DataSlot enchantmentSeed;

    @Unique
    public List<EnchantmentInstance> generateEnchantments(RandomSource random, ItemStack stack, int level, Stream<Holder<Enchantment>> possibleEnchantments) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        Enchantable enchantableComponent = stack.get(DataComponents.ENCHANTABLE);
        if (enchantableComponent == null) {
            return list;
        } else {
            level += 1 + random.nextInt(enchantableComponent.value() / 4 + 1) + random.nextInt(enchantableComponent.value() / 4 + 1);
            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            level = Mth.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
            List<EnchantmentInstance> list2 = EnchantmentHelperHelper.getPossibleWolfArmorEntries(level, possibleEnchantments);
            Optional<EnchantmentInstance> var10000 = WeightedRandom.getRandomItem(random, list2, EnchantmentInstance::weight);
            var10000.ifPresent(list::add);  // Explicitly using a lambda expression

            while (random.nextInt(50) <= level) {
                var10000 = WeightedRandom.getRandomItem(random, list2, EnchantmentInstance::weight);
                var10000.ifPresent(list::add);
                level /= 2;
            }

            return list;
        }
    }

//    @Unique
//    public List<EnchantmentLevelEntry> generateEnchantments(Random random, ItemStack stack, int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments) {
//        List<EnchantmentLevelEntry> list = Lists.newArrayList();
//        Item item = stack.getItem();
//        int i = item.getEnchantability();
//        if (i <= 0) {
//            return list;
//        } else {
//            level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
//            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
//            level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
//            List<EnchantmentLevelEntry> list2 = EnchantmentHelperHelper.getPossibleWolfArmorEntries(level, possibleEnchantments);
//            Optional<EnchantmentLevelEntry> var10000 = Weighting.getRandom(random, list2);
//            var10000.ifPresent(list::add);  // Explicitly using a lambda expression
//
//            while (random.nextInt(50) <= level) {
//                var10000 = Weighting.getRandom(random, list2);
//                var10000.ifPresent(list::add);
//                level /= 2;
//            }
//
//            return list;
//        }
//    }


    @Inject(method = "getEnchantmentList", at = @At("HEAD"), cancellable = true)
    public void changeGenerateEnchantments(RegistryAccess registryManager, ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (stack.is(Items.WOLF_ARMOR)) {
            this.random.setSeed(this.enchantmentSeed.get() + slot);

            List<Holder<Enchantment>> enchantments = new ArrayList<>();
            registryManager.lookupOrThrow(Registries.ENCHANTMENT).asHolderIdMap().forEach(e -> {
                if (WolfCompanion.isSameEnchantment(e.value(), Enchantments.UNBREAKING)) {
                    enchantments.add(e);
                }
                if (WolfCompanion.isSameEnchantment(e.value(), Enchantments.MENDING)) {
                    enchantments.add(e);
                }
            });
            Stream<Holder<Enchantment>> enchantmentStream = enchantments.stream();
            List<EnchantmentInstance> list = generateEnchantments(this.random, stack, level, enchantmentStream);

            cir.setReturnValue(list);
            cir.cancel();
        }
    }
}
