package github.meloweh.wolfcompanion.util;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.registry.ModItems;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;

public final class WolfArmorHelper {
    public static final int ARMOR_SLOT = 0;
    private static final int SCUTE_REPAIR_AMOUNT = 8;

    private WolfArmorHelper() {
    }

    public static boolean isWolfArmor(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.WOLF_ARMOR);
    }

    public static ItemStack getArmorStack(Wolf wolf) {
        SimpleContainer inventory = ((WolfEntityProvider) wolf).getInventory();
        if (inventory == null || inventory.getContainerSize() <= ARMOR_SLOT) {
            return ItemStack.EMPTY;
        }
        return inventory.getItem(ARMOR_SLOT);
    }

    public static boolean hasArmor(Wolf wolf) {
        return isWolfArmor(getArmorStack(wolf));
    }

    public static boolean canAbsorbDamage(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_ARMOR);
    }

    public static void damageArmor(Wolf wolf, float damage) {
        ItemStack armor = getArmorStack(wolf);
        if (!isWolfArmor(armor)) {
            return;
        }

        int durabilityDamage = Math.max(1, Mth.ceil(damage));
        armor.hurtAndBreak(durabilityDamage, wolf, entity -> entity.broadcastBreakEvent(EquipmentSlot.CHEST));
        if (armor.isEmpty()) {
            ((WolfEntityProvider) wolf).getInventory().setItem(ARMOR_SLOT, ItemStack.EMPTY);
        }
    }

    public static boolean repairArmor(Wolf wolf, ItemStack ingredient) {
        ItemStack armor = getArmorStack(wolf);
        if (!isWolfArmor(armor) || !armor.isDamaged() || !ingredient.is(ModItems.ARMADILLO_SCUTE)) {
            return false;
        }

        armor.setDamageValue(Math.max(0, armor.getDamageValue() - SCUTE_REPAIR_AMOUNT));
        return true;
    }
}
