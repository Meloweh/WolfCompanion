package github.meloweh.wolfcompanion.util;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class WolfInventoryProvider {
    private final List<ItemStack> inventoryContents;
    private final WolfEntity entity;
    private final WolfEntityProvider armoredWolf;

    public WolfInventoryProvider(@NotNull WolfEntity wolf) {
        this.entity = wolf;
        this.armoredWolf = (WolfEntityProvider) wolf;
        this.inventoryContents = new ArrayList<>();
    }

    public void refreshInventoryContents(Inventory invBasic) {
        this.inventoryContents.clear();
        for(int slotIndex = 1;
            slotIndex < invBasic.size();
            ++slotIndex) {
            this.inventoryContents.add(invBasic.getStack(slotIndex));
        }
    }

    public void inventoryInit(final InventoryChangedListener listener) {
        armoredWolf.getInventory().removeListener(listener);
        armoredWolf.getInventory().addListener(listener);
        refreshInventoryContents(armoredWolf.getInventory());
    }

    public static boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ItemTags.WOLF_FOOD);
    }

    public static boolean canPlayerEat(final ItemStack itemStack) {
        return !itemStack.isEmpty() && itemStack.contains(DataComponentTypes.FOOD);
    }

    public static boolean canEat(final ItemStack itemStack) {
        return canPlayerEat(itemStack) && isBreedingItem(itemStack);
    }

    public boolean canEat_(final ItemStack itemStack) {
        return !itemStack.isEmpty() && this.entity.isBreedingItem(itemStack) && itemStack.contains(DataComponentTypes.FOOD);
    }

    @NotNull
    public ItemStack findFood() {
        final float healthDiff = this.entity.getMaxHealth() - this.entity.getHealth();
        return this.inventoryContents.stream()
                .filter(this::canEat_)
                .min(Comparator.comparing(itemStack
                        -> Math.abs(healthDiff - itemStack.get(DataComponentTypes.FOOD).nutrition())))
                .orElse(ItemStack.EMPTY);
    }

    public long getFoodCount() {
        return this.inventoryContents.stream().filter(this::canEat_).mapToInt(ItemStack::getCount).sum();
    }

//    public boolean hasFood() {
//        return this.inventoryContents.stream().anyMatch(this::canEat_);
//    }

    public boolean hasSpace() {
        Iterator<ItemStack> it = this.armoredWolf.getInventory().heldStacks.iterator();

        for (ItemStack itemStack = Items.POTATO.getDefaultStack(); it.hasNext(); itemStack = it.next()) {
            if (itemStack.isEmpty()) return true;
        }
        return false;
    }

//    public boolean hasOnly(final Item item, final int max) {
//        return this.inventoryContents.stream()
//                .filter(i -> i.isOf(item)).mapToInt(ItemStack::getCount).sum() <= max;
//    }

    public boolean onlyFood(final Item item) {
        return this.inventoryContents.stream()
                .filter(this::canEat_)
                .anyMatch(i -> (!i.isOf(item)) && !i.isEmpty());
    }

//    public boolean isOnlyItem(final Item item, final int max) {
//        return isOnlyItem(item) && hasOnly(item, max);
//    }
}
