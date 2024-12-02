package github.meloweh.wolfcompanion.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public interface WolfEntityProvider {
    boolean shouldDropChest();

    //StackReference wolfcompanion_template_1_21_1$getGetStackReference(int mappedIndex);
    boolean hasChestEquipped();
    SimpleInventory getInventory();

    void setShouldDropChest(boolean yes);

    void wolfcompanion_template_1_21_1$dropInventoryByButton();

    boolean wolfcompanion_template_1_21_1$tryAttack(Entity target);

    Optional<ItemEntity> getTargetPickup__();

    void setTargetPickup__(final ItemEntity entity);

    void spit__(ItemStack stack);
}
