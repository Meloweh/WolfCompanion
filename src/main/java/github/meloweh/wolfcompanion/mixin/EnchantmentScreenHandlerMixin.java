package github.meloweh.wolfcompanion.mixin;

import com.google.common.collect.Lists;
import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.util.EnchantmentHelperHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
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

import net.minecraft.util.math.random.Random;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {
    @Final
    @Shadow
    private Random random;

    @Final
    @Shadow
    private Property seed;

    @Unique
    public List<EnchantmentLevelEntry> generateEnchantments(Random random, ItemStack stack, int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments) {
        List<EnchantmentLevelEntry> list = Lists.newArrayList();
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return list;
        } else {
            level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
            List<EnchantmentLevelEntry> list2 = EnchantmentHelperHelper.getPossibleWolfArmorEntries(level, possibleEnchantments);
            //List<EnchantmentLevelEntry> list2 = EnchantmentHelper.getPossibleEntries(level, stack, possibleEnchantments);
            //System.out.println("A");
            //list2.forEach(e -> System.out.println(e.enchantment.value().description().getString()));
            Optional<EnchantmentLevelEntry> var10000 = Weighting.getRandom(random, list2);
            var10000.ifPresent(list::add);  // Explicitly using a lambda expression

            while (random.nextInt(50) <= level) {
                var10000 = Weighting.getRandom(random, list2);
                var10000.ifPresent(list::add);  // Explicitly using a lambda expression
                level /= 2;
            }

            System.out.println("B");
            list.forEach(e -> System.out.println(e.enchantment.value().description().getString()));
            System.out.println("B2");


            return list;
        }
    }


    @Inject(method = "generateEnchantments", at = @At("HEAD"), cancellable = true)
    public void changeGenerateEnchantments(DynamicRegistryManager registryManager, ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (stack.isOf(Items.WOLF_ARMOR)) {
            this.random.setSeed((long) (this.seed.get() + slot));
//        registryManager.get(RegistryKeys.ENCHANTMENT).getIndexedEntries().forEach(e -> {
//            System.out.println(e.value().description().getString());
//        });
            List<RegistryEntry<Enchantment>> enchantments = new ArrayList<>();
            registryManager.get(RegistryKeys.ENCHANTMENT).getIndexedEntries().forEach(e -> {
                if (WolfCompanion.isSameEnchantment(e.value(), Enchantments.UNBREAKING)) {
                    enchantments.add(e);
                }
                if (WolfCompanion.isSameEnchantment(e.value(), Enchantments.MENDING)) {
                    enchantments.add(e);
                }
            });
            Stream<RegistryEntry<Enchantment>> enchantmentStream = enchantments.stream();
            List<EnchantmentLevelEntry> list = generateEnchantments(this.random, stack, level, enchantmentStream);

            cir.setReturnValue(list);
            cir.cancel();
        }
    }
}
