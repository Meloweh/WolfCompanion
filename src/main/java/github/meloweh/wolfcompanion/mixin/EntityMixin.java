package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.EntityAccessor;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.inventory.StackReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor, WolfEntityProvider {
    @Invoker("hasPassenger")
    public abstract boolean invokeHasPassenger(Entity passenger);
}
