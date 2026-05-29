package github.meloweh.wolfcompanion.menu;

import github.meloweh.wolfcompanion.config.WolfCompanionConfig;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

final class WolfBagInventorySlot extends Slot {
    WolfBagInventorySlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return Math.min(super.getMaxStackSize(), WolfCompanionConfig.current().wolfBagInventoryStackLimit());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(super.getMaxStackSize(stack), WolfCompanionConfig.current().wolfBagInventoryStackLimit());
    }
}
