package github.meloweh.wolfcompanion.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public interface WolfEntityProvider {
    boolean shouldDropChest();
    boolean shouldReleaseWolf();
    boolean isAggressive__();
    boolean isLock__();

    //StackReference wolfcompanion_template_1_21_1$getGetStackReference(int mappedIndex);
    boolean hasChestEquipped();
    SimpleInventory getInventory();

    void setShouldDropChest(boolean yes);
    void setShouldReleaseWolf(boolean yes);
    void setAggressive__(boolean yes);
    void setLock__(boolean lock);

    void wolfcompanion_template_1_21_1$dropInventoryByButton();
    void releaseWolfButton();

    boolean tryAttack__(Entity target);

    Optional<ItemEntity> getTargetPickup__();

    void setTargetPickup__(final ItemEntity entity);

    void spit__(ItemStack stack);
}
