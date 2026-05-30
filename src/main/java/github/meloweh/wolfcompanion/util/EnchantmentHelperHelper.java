package github.meloweh.wolfcompanion.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;

public class EnchantmentHelperHelper {
    public static List<Enchantment> getWolfArmorEnchantments() {
        final List<Enchantment> list = new ArrayList<>();
        list.add(Enchantments.MENDING);
        list.add(Enchantments.UNBREAKING);
        return list;
    }

    public static List<EnchantmentInstance> getPossibleWolfArmorEntries(int level, boolean allowTreasure) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        for (Enchantment enchantment : getWolfArmorEnchantments()) {
            if (enchantment.isTreasureOnly() && !allowTreasure) {
                continue;
            }

            for(int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); --j) {
                if (level >= enchantment.getMinCost(j) && level <= enchantment.getMaxCost(j)) {
                    list.add(new EnchantmentInstance(enchantment, j));
                    break;
                }
            }
        }
        return list;
    }
}
