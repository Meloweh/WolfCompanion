package github.meloweh.wolfcompanion.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.inventory.StackReference;

public interface EntityAccessor {
    boolean invokeHasPassenger(Entity passenger);
    DataTracker getDataTracker();

    StackReference getStackReference(int mappedIndex);
}
