package github.meloweh.wolfcompanion.mixin.client;

import accessor.WolfEntityModelAccessor;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(WolfEntityModel.class)
public abstract class WolfEntityModelMixin implements WolfEntityModelAccessor {
    @Accessor("torso")
    public abstract ModelPart getTorso();
    @Accessor("realHead")
    public abstract ModelPart getRealHead();
    @Accessor("head")
    public abstract ModelPart getHead();
}