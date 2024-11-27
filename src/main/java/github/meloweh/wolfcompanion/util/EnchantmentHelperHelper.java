package github.meloweh.wolfcompanion.util;

import com.google.common.collect.Lists;
import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EnchantmentHelperHelper {
    public static List<RegistryKey<Enchantment>> getWolfArmorEnchantments() {
        final List<RegistryKey<Enchantment>> list = new ArrayList<>();
        list.add(Enchantments.MENDING);
        list.add(Enchantments.UNBREAKING);
        return list;
    }

    public static List<EnchantmentLevelEntry> getPossibleWolfArmorEntries(int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments) {
        List<EnchantmentLevelEntry> list = Lists.newArrayList();
        final List<RegistryKey<Enchantment>> wolfArmorEnchantments = getWolfArmorEnchantments();
        possibleEnchantments.filter((enchantment) -> wolfArmorEnchantments.stream().anyMatch(enchantment::matchesKey))
                .forEach((enchantmentx) -> {
                    Enchantment enchantment = (Enchantment)enchantmentx.value();

                    for(int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); --j) {
                        if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
                            list.add(new EnchantmentLevelEntry(enchantmentx, j));
                            break;
                        }
                    }

                });
        return list;
    }

//    public static List<EnchantmentLevelEntry> getPossibleWolfArmorEntries(int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments) {
//        List<EnchantmentLevelEntry> list = Lists.newArrayList();
//        final List<RegistryKey<Enchantment>> wolfArmorEnchantments = getWolfArmorEnchantments();
//        possibleEnchantments.filter((enchantment) -> wolfArmorEnchantments.stream().filter(enchantment::matchesKey).findAny().isEmpty())
//        .forEach((enchantmentx) -> {
//            Enchantment enchantment = (Enchantment)enchantmentx.value();
//
//            for(int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); --j) {
//                if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
//                    list.add(new EnchantmentLevelEntry(enchantmentx, j));
//                    break;
//                }
//            }
//
//        });
//        return list;
//    }

//    public static List<EnchantmentLevelEntry> getPossibleWolfArmorEntries(int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments) {
//        List<EnchantmentLevelEntry> list = Lists.newArrayList();
//        possibleEnchantments.filter((enchantment) -> {
//            boolean hasMending = WolfCompanion.isSameEnchantment(enchantment.value(), Enchantments.MENDING);
//            boolean hasUnbreaking = WolfCompanion.isSameEnchantment(enchantment.value(), Enchantments.UNBREAKING);
//            System.out.println(enchantment.value().description().getString());
//            return hasMending || hasUnbreaking;
//        }).forEach((enchantmentx) -> {
//            Enchantment enchantment = (Enchantment)enchantmentx.value();
//
//            for(int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); --j) {
//                if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
//                    list.add(new EnchantmentLevelEntry(enchantmentx, j));
//                    break;
//                }
//            }
//
//        });
//        return list;
//    }
}
