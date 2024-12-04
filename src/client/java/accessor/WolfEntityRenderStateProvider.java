package accessor;

import net.minecraft.entity.passive.WolfEntity;

public interface WolfEntityRenderStateProvider {
    WolfEntity getWolf__();
    boolean hasWolf__();
    void setWolf__(final WolfEntity wolf);
    boolean hasChestEquipped__();
    void setChestEquipped__(final boolean bl);
}
