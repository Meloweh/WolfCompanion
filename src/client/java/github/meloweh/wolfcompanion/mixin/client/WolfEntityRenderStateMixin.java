package github.meloweh.wolfcompanion.mixin.client;

import accessor.WolfEntityRenderStateProvider;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ModelTransformationMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WolfEntityRenderState.class)
public class WolfEntityRenderStateMixin implements WolfEntityRenderStateProvider {

    private WolfEntity wolf;
    private boolean chestEquipped;

    @Override
    public WolfEntity getWolf__() {
        return null;
    }

    @Override
    public boolean hasWolf__() {
        return wolf != null;
    }

    @Override
    public void setWolf__(final WolfEntity wolf) {
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
    public final ItemRenderState itemRenderState = new ItemRenderState();

    @Override
    public ItemRenderState getItemRenderState__() {
        return itemRenderState;
    }

    @Override
    public void updateRenderState__(LivingEntity entity, WolfEntityRenderStateProvider state, ItemModelManager itemModelManager) {
        itemModelManager.updateForLivingEntity(state.getItemRenderState__(), entity.getMainHandStack(), ModelTransformationMode.GROUND, false, entity);
    }
}
