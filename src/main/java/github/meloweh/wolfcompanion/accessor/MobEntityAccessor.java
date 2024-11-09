package github.meloweh.wolfcompanion.accessor;

import net.minecraft.item.ItemStack;

public interface MobEntityAccessor {
    ItemStack getBodyArmor();
    void equipBodyArmor(ItemStack stack);
}
