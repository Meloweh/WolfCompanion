package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.effects.ModEffects;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.util.NBTHelper;
import github.meloweh.wolfcompanion.util.WolfNbtList;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerAccessor {
    @Unique
    ServerPlayerEntity self;

    @Unique
    WolfNbtList rescuedWolfNbtList = new WolfNbtList();
    @Unique
    WolfNbtList whistleWolfNbtList = new WolfNbtList();

    @Accessor("server")
    public abstract MinecraftServer getServer__();

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

    /*@Inject(method = "onSpawn", at = @At("TAIL"))
    private void spawnDoggosOnSpawn(CallbackInfo ci) {
        respawnRescuedDoggo(null, null);
    }*/

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    public void writeWolfDataToNbt(WriteView view, CallbackInfo ci) {
        this.rescuedWolfNbtList.writeDataToNbt(view, WolfEventHandler.RESCUED_WOLF_NBT_KEY);
        this.whistleWolfNbtList.writeDataToNbt(view, WolfEventHandler.WHISTLE_WOLF_NBT_KEY);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    public void readWolfDataToNbt(ReadView view, CallbackInfo ci) {
        this.rescuedWolfNbtList.readDataToNbt(view, WolfEventHandler.RESCUED_WOLF_NBT_KEY);
        this.whistleWolfNbtList.readDataToNbt(view, WolfEventHandler.WHISTLE_WOLF_NBT_KEY);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        this.self = (ServerPlayerEntity) (Object) this;
    }

    @Unique
    private void spawnDoggos(final WolfNbtList wolfNbtList) {
        final WolfNbtList canDelete = new WolfNbtList();
        wolfNbtList.getWolfNbts().forEach(wolfNbt -> {
            final boolean rescue = wolfNbt.getInt("RescueTimeout", -1) == 0;
            if (rescue) NBTHelper.cleanRescueWolfNbt(wolfNbt, this.self.getMaxHealth());
            final boolean success = NBTHelper.spawnWolfFromNbt(this.self, wolfNbt, rescue);
            if (success) canDelete.queueWolfNbt(wolfNbt);
        });
        wolfNbtList.getWolfNbts().removeIf(wolfNbt -> canDelete.getWolfNbts().contains(wolfNbt));
    }

    /*@Inject(method = "sleep", at = @At("TAIL"))
    private void respawnRescuedDoggo(BlockPos pos, CallbackInfo ci) {
        if (self.isSleeping())
            spawnDoggos(this.rescuedWolfNbtList, true);

    }*/

    @Unique
    private void clearTimeoutEffects() {
        for (int i = 1; i < ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY.length; i++) {
            final StatusEffectInstance inst = this.self.getStatusEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i]);
            if (inst != null) {
                this.self.removeStatusEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i]);
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void updateRespawnCountdown(CallbackInfo ci) {
        //spawnDoggos(this.rescuedWolfNbtList, true);
        this.rescuedWolfNbtList.rescueTimeoutTick();
        //final List<NbtCompound> elapsed = this.rescuedWolfNbtList.dequeueElapsedTimeout();
        //elapsed.forEach(nbt -> this.whistleWolfNbtList.queueWolfNbt(nbt));
        final int level = Math.min(this.rescuedWolfNbtList.nonElapsedSize(), 11);
        final Optional<Integer> optBriefestTimeout = this.rescuedWolfNbtList.getBriefestTimeout();

        if (optBriefestTimeout.isEmpty()) {
            clearTimeoutEffects();
        }

        if (optBriefestTimeout.isPresent()) {
            final StatusEffectInstance inst = this.self.getStatusEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[level]);
            if (inst == null) {
                clearTimeoutEffects();
                this.self.addStatusEffect(new StatusEffectInstance(
                        ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[level],
                        optBriefestTimeout.get(),     // duration ticks
                        0,           // amplifier
                        false,       // ambient
                        false,       // showParticles
                        true,        // showIcon
                        null         // hiddenEffect
                ));
            }
        }
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
        spawnDoggos(this.whistleWolfNbtList);
    }

    @Override
    public void spawnElapsedRescueWolfNbts__() {
        spawnDoggos(this.rescuedWolfNbtList);
    }

    @Override
    public boolean hasElapsed__() {
        return this.rescuedWolfNbtList.elapsedSize() > 0;
    }
}