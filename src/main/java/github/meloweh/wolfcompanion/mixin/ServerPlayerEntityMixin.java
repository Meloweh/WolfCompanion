package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerAccessor {
    @Accessor("screenHandlerSyncId")
    public abstract int getScreenHandlerSyncId();

    @Invoker("incrementScreenHandlerSyncId")
    public abstract void execIncrementScreenHandlerSyncId();

    @Invoker("onScreenHandlerOpened")
    public abstract void execOnScreenHandlerOpened(ScreenHandler screenHandler);

    //this.wolfTorso = ((WolfEntityModelAccessor) model).getTorso();
}
