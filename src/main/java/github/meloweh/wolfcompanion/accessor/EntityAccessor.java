package github.meloweh.wolfcompanion.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;

public interface EntityAccessor {
    boolean invokeHasPassenger(Entity passenger);
    //DataTracker getDataTracker();

    SlotAccess getStackReference(int mappedIndex);
}
