package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.util.NBTHelper;
import github.meloweh.wolfcompanion.util.WolfNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerAccessor {
    @Unique
    ServerPlayerEntity self;

    @Unique
    WolfNbtList rescuedWolfNbtList = new WolfNbtList();
    @Unique
    WolfNbtList whistleWolfNbtList = new WolfNbtList();

    @Accessor("screenHandlerSyncId")
    public abstract int getScreenHandlerSyncId();

    @Invoker("incrementScreenHandlerSyncId")
    public abstract void execIncrementScreenHandlerSyncId();

    @Invoker("onScreenHandlerOpened")
    public abstract void execOnScreenHandlerOpened(ScreenHandler screenHandler);

    @Override
    public WolfNbtList getRescuedWolfNbts__() {
        return this.rescuedWolfNbtList;
    }
    @Override
    public WolfNbtList getWhistleWolfNbts__() {
        return this.whistleWolfNbtList;
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void restorePlayerDataAfterRespawn(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.rescuedWolfNbtList = ((ServerPlayerAccessor) oldPlayer).getRescuedWolfNbts__();
        this.whistleWolfNbtList = ((ServerPlayerAccessor) oldPlayer).getWhistleWolfNbts__();
    }

    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void spawnDoggosOnSpawn(CallbackInfo ci) {
        respawnRescuedDoggo(null, null);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeWolfDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.rescuedWolfNbtList.writeDataToNbt(nbt, WolfEventHandler.RESCUED_WOLF_NBT_KEY);
        this.whistleWolfNbtList.writeDataToNbt(nbt, WolfEventHandler.WHISTLE_WOLF_NBT_KEY);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readWolfDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.rescuedWolfNbtList.readDataToNbt(nbt, WolfEventHandler.RESCUED_WOLF_NBT_KEY);
        this.whistleWolfNbtList.readDataToNbt(nbt, WolfEventHandler.WHISTLE_WOLF_NBT_KEY);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        this.self = (ServerPlayerEntity) (Object) this;
    }

    @Unique
    private void spawnDoggos(final WolfNbtList wolfNbtList, final boolean rescue) {
        wolfNbtList.getWolfNbts().forEach(wolfNbt -> {
            if (rescue) NBTHelper.cleanRescueWolfNbt(wolfNbt, this.self.getMaxHealth());
            NBTHelper.spawnWolfFromNbt(this.self, wolfNbt, rescue);
        });
        wolfNbtList.clear();
    }

    @Inject(method = "sleep", at = @At("HEAD"))
    private void respawnRescuedDoggo(BlockPos pos, CallbackInfo ci) {
        spawnDoggos(this.rescuedWolfNbtList, true);
    }

    @Override
    public void queueRescuedWolfNbt__(NbtCompound nbt) {
        this.rescuedWolfNbtList.queueWolfNbt(nbt);
    }

    @Override
    public void queueWhistleWolfNbt__(NbtCompound nbt) {
        this.whistleWolfNbtList.queueWolfNbt(nbt);
    }

    @Override
    public void spawnWhistleWolfNbts__() {
        spawnDoggos(this.whistleWolfNbtList, false);
    }
}
