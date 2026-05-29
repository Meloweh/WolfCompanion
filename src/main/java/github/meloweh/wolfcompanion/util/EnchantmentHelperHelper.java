package github.meloweh.wolfcompanion.util;

import com.google.common.collect.Lists;
import github.meloweh.wolfcompanion.WolfCompanion;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;

public class EnchantmentHelperHelper {
    public static List<ResourceKey<Enchantment>> getWolfArmorEnchantments() {
        final List<ResourceKey<Enchantment>> list = new ArrayList<>();
        list.add(Enchantments.MENDING);
        list.add(Enchantments.UNBREAKING);
        return list;
    }

    public static List<EnchantmentInstance> getPossibleWolfArmorEntries(int level, Stream<Holder<Enchantment>> possibleEnchantments) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        final List<ResourceKey<Enchantment>> wolfArmorEnchantments = getWolfArmorEnchantments();
        possibleEnchantments.filter((enchantment) -> wolfArmorEnchantments.stream().anyMatch(enchantment::is))
                .forEach((enchantmentx) -> {
                    Enchantment enchantment = (Enchantment)enchantmentx.value();

                    for(int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); --j) {
                        if (level >= enchantment.getMinCost(j) && level <= enchantment.getMaxCost(j)) {
                            list.add(new EnchantmentInstance(enchantmentx, j));
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
