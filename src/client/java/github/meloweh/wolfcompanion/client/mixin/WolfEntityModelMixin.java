package github.meloweh.wolfcompanion.client.mixin;

import github.meloweh.wolfcompanion.client.accessor.WolfEntityModelAccessor;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(WolfModel.class)
public abstract class WolfEntityModelMixin implements WolfEntityModelAccessor {
    @Accessor("body")
    public abstract ModelPart getTorso();
    @Accessor("head")
    public abstract ModelPart getHead();
}
