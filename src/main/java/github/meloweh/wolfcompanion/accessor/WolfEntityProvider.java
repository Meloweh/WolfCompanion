package github.meloweh.wolfcompanion.accessor;

import net.minecraft.inventory.StackReference;

public interface WolfEntityProvider {
    StackReference wolfcompanion_template_1_21_1$getGetStackReference(int mappedIndex);
    boolean hasChestEquipped();
}
