package accessor;

import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;

public interface WolfEntityRenderStateProvider {
    boolean hasWolf__();
    void setWolf__(final Wolf wolf);
    boolean hasChestEquipped__();
    void setChestEquipped__(final boolean bl);
    void setEntityId__(final int id);
    int getEntityId__();
    ItemStackRenderState getItemRenderState__();
    void updateRenderState__(LivingEntity entity, WolfEntityRenderStateProvider state, ItemModelResolver itemModelManager);
}
