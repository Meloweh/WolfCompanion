package github.meloweh.wolfcompanion.mixin.client;

import accessor.WolfEntityRenderStateProvider;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;

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

}
