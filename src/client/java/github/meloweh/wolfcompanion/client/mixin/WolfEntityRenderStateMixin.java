package github.meloweh.wolfcompanion.client.mixin;

import github.meloweh.wolfcompanion.client.accessor.WolfEntityRenderStateProvider;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WolfRenderState.class)
public class WolfEntityRenderStateMixin implements WolfEntityRenderStateProvider {

    private Wolf wolf;
    private boolean chestEquipped;
    private int entityId;

    @Override
    public boolean hasWolf__() {
        return wolf != null;
    }

    @Override
    public void setWolf__(final Wolf wolf) {
        this.wolf = wolf;
    }

    @Override
    public boolean hasChestEquipped__() {
        return chestEquipped;
    }

    @Override
    public void setChestEquipped__(boolean bl) {
        chestEquipped = bl;
    }

    @Unique
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();

    @Override
    public ItemStackRenderState getItemRenderState__() {
        return itemRenderState;
    }

    @Override
    public void updateRenderState__(LivingEntity entity, WolfEntityRenderStateProvider state, ItemModelResolver itemModelManager) {
        itemModelManager.updateForLiving(state.getItemRenderState__(), entity.getMainHandItem(), ItemDisplayContext.GROUND, entity);
    }

    @Override
    public void setEntityId__(int id) {
        entityId = id;
    }

    @Override
    public int getEntityId__() {
        return entityId;
    }
}
