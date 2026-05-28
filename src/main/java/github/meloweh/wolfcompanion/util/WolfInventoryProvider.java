package github.meloweh.wolfcompanion.util;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WolfInventoryProvider {
    private final List<ItemStack> inventoryContents;
    private final Wolf entity;
    private final WolfEntityProvider armoredWolf;

    public WolfInventoryProvider(@NotNull Wolf wolf) {
        this.entity = wolf;
        this.armoredWolf = (WolfEntityProvider) wolf;
        this.inventoryContents = new ArrayList<>();
    }

    public void refreshInventoryContents(Container invBasic) {
        this.inventoryContents.clear();
        for(int slotIndex = 1;
            slotIndex < invBasic.getContainerSize();
            ++slotIndex) {
            this.inventoryContents.add(invBasic.getItem(slotIndex));
        }
    }

    public void inventoryInit(final ContainerListener listener) {
        armoredWolf.getInventory().removeListener(listener);
        armoredWolf.getInventory().addListener(listener);
        refreshInventoryContents(armoredWolf.getInventory());
    }

    public static boolean isBreedingItem(ItemStack stack) {
        return stack.is(ItemTags.WOLF_FOOD);
    }

    public static boolean canPlayerEat(final ItemStack itemStack) {
        return !itemStack.isEmpty() && itemStack.has(DataComponents.FOOD);
    }

    public static boolean canEat(final ItemStack itemStack) {
        return canPlayerEat(itemStack) && isBreedingItem(itemStack);
    }

    public boolean canEat_(final ItemStack itemStack) {
        return !itemStack.isEmpty() && this.entity.isFood(itemStack) && itemStack.has(DataComponents.FOOD);
    }

    @NotNull
    public ItemStack findFood() {
        final float healthDiff = this.entity.getMaxHealth() - this.entity.getHealth();
        return this.inventoryContents.stream()
                .filter(this::canEat_)
                .min(Comparator.comparing(itemStack
                        -> Math.abs(healthDiff - itemStack.get(DataComponents.FOOD).nutrition())))
                .orElse(ItemStack.EMPTY);
    }

    public long getFoodCount() {
        return this.inventoryContents.stream().filter(this::canEat_).mapToInt(ItemStack::getCount).sum();
    }

//    public boolean hasFood() {
//        return this.inventoryContents.stream().anyMatch(this::canEat_);
//    }

    public boolean hasSpace() {
        Iterator<ItemStack> it = this.armoredWolf.getInventory().items.iterator();

        for (ItemStack itemStack = Items.POTATO.getDefaultInstance(); it.hasNext(); itemStack = it.next()) {
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
                .noneMatch(e -> !e.is(item) && !e.isEmpty());
    }

//    public boolean isOnlyItem(final Item item, final int max) {
//        return isOnlyItem(item) && hasOnly(item, max);
//    }
}
