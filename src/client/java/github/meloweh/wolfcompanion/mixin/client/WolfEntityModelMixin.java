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
    @Accessor("head")
    public abstract ModelPart getHead();
/*
    @Inject(
            method = "getTexturedModelData(Lnet/minecraft/client/model/Dilation;)Lnet/minecraft/client/model/ModelData;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPartData;addChild(Ljava/lang/String;Lnet/minecraft/client/model/ModelPartBuilder;Lnet/minecraft/client/model/ModelTransform;)Lnet/minecraft/client/model/ModelPartData;",
                    ordinal = 2, // Adjust this if "body" is not the third addChild in the method
                    shift = At.Shift.AFTER
            )
    )
    private static void addBagPart(Dilation dilation, CallbackInfoReturnable<ModelData> cir) {
        ModelData modelData = cir.getReturnValue();
        if (modelData != null) {
            ModelPartData modelPartData = modelData.getRoot();

            // Adding your custom "bag" model part
            modelPartData.addChild("bag", ModelPartBuilder.create()
                            .uv(0, 0).cuboid(2.6667F, -1.5F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                            .uv(1, 6).cuboid(-2.3333F, -1.5F, -2.0F, 5.0F, 2.0F, 1.0F, new Dilation(0.0F))
                            .uv(1, 13).cuboid(-2.3333F, 0.5F, -2.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F))
                            .uv(3, 20).cuboid(-2.3333F, 1.5F, -1.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F))
                            .uv(13, 24).cuboid(-3.3333F, -1.5F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                            .uv(0, 27).cuboid(-2.3333F, -2.5F, -1.0F, 5.0F, 4.0F, 1.0F, new Dilation(0.0F)),
                    ModelTransform.of(0.0F, -14.0F, -2.0F, 1.5707964F, 1.5707964F, 1.5707964F)

            );
        }
    }*/
}