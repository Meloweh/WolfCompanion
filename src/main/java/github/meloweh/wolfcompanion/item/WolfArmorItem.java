package github.meloweh.wolfcompanion.item;

import github.meloweh.wolfcompanion.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WolfArmorItem extends Item {
    public static final int DURABILITY = 64;

    public WolfArmorItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(ModItems.ARMADILLO_SCUTE) || super.isValidRepairItem(stack, ingredient);
    }
}
