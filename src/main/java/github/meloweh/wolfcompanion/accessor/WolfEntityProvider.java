package github.meloweh.wolfcompanion.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.SimpleInventory;

public interface WolfEntityProvider {
    boolean shouldDropChest();

    //StackReference wolfcompanion_template_1_21_1$getGetStackReference(int mappedIndex);
    boolean hasChestEquipped();
    SimpleInventory getInventory();

    void setShouldDropChest(boolean yes);

    void wolfcompanion_template_1_21_1$dropInventoryByButton();

    boolean wolfcompanion_template_1_21_1$tryAttack(Entity target);
}
