package github.meloweh.wolfcompanion.util;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.config.WolfCompanionConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.Wolf;
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

    public void inventoryInit() {
        refreshInventoryContents(armoredWolf.getInventory());
    }

    public static boolean isBreedingItem(ItemStack stack) {
        return canPlayerEat(stack) && stack.getItem().getFoodProperties().isMeat();
    }

    public static boolean canPlayerEat(final ItemStack itemStack) {
        return !itemStack.isEmpty() && itemStack.isEdible();
    }

    public static boolean canEat(final ItemStack itemStack) {
        return canPlayerEat(itemStack) && isBreedingItem(itemStack);
    }

    public boolean canEat_(final ItemStack itemStack) {
        return !itemStack.isEmpty() && this.entity.isFood(itemStack) && itemStack.isEdible();
    }

    @NotNull
    public ItemStack findFood() {
        final float healthDiff = this.entity.getMaxHealth() - this.entity.getHealth();
        return this.inventoryContents.stream()
                .filter(this::canEat_)
                .min(Comparator.comparing(itemStack
                        -> Math.abs(healthDiff - itemStack.getItem().getFoodProperties().getNutrition())))
                .orElse(ItemStack.EMPTY);
    }

    public long getFoodCount() {
        return this.inventoryContents.stream().filter(this::canEat_).mapToInt(ItemStack::getCount).sum();
    }

    public boolean hasSpace() {
        Container inventory = this.armoredWolf.getInventory();
        int stackLimit = WolfCompanionConfig.current().wolfBagInventoryStackLimit();

        for (int slot = 1; slot < inventory.getContainerSize(); slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.isEmpty()) {
                return true;
            }
            if (itemStack.getCount() < Math.min(itemStack.getMaxStackSize(), stackLimit)) {
                return true;
            }
        }

        return false;
    }

    public boolean onlyFood(final Item item) {
        return this.inventoryContents.stream()
                .filter(this::canEat_)
                .noneMatch(e -> !e.is(item) && !e.isEmpty());
    }
}
