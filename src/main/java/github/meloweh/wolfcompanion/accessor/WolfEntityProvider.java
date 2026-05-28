package github.meloweh.wolfcompanion.accessor;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface WolfEntityProvider {
    boolean shouldDropChest();
    boolean shouldReleaseWolf();
    boolean isAggressive__();
    boolean isLock__();

    //StackReference wolfcompanion_template_1_21_1$getGetStackReference(int mappedIndex);
    boolean hasChestEquipped();
    SimpleContainer getInventory();

    void setShouldDropChest(boolean yes);
    void setShouldReleaseWolf(boolean yes);
    void setAggressive__(boolean yes);
    void setLock__(boolean lock);

    void wolfcompanion_template_1_21_1$dropInventoryByButton();
    void releaseWolfButton();

    boolean tryAttack__(ServerLevel serverWorld, Entity target);

    Optional<ItemEntity> getTargetPickup__();

    void setTargetPickup__(final ItemEntity entity);

    void spit__(ItemStack stack);
}
