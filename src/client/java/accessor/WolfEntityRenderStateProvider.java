package accessor;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import org.apache.http.annotation.Obsolete;

public interface WolfEntityRenderStateProvider {
    @Obsolete
    WolfEntity getWolf__();
    boolean hasWolf__();
    void setWolf__(final WolfEntity wolf);
    boolean hasChestEquipped__();
    void setChestEquipped__(final boolean bl);
    void setEntityId__(final int id);
    int getEntityId__();
    ItemRenderState getItemRenderState__();
    void updateRenderState__(LivingEntity entity, WolfEntityRenderStateProvider state, ItemModelManager itemModelManager);
}
