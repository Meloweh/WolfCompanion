package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.effects.ModEffects;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.util.NBTHelper;
import github.meloweh.wolfcompanion.util.WolfNbtList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerAccessor {
    @Unique
    ServerPlayer self;

    @Unique
    WolfNbtList rescuedWolfNbtList = new WolfNbtList();
    @Unique
    WolfNbtList whistleWolfNbtList = new WolfNbtList();

    @Accessor("server")
    public abstract MinecraftServer getServer__();

    @Accessor("containerCounter")
    public abstract int getScreenHandlerSyncId();

    @Invoker("nextContainerCounter")
    public abstract void execIncrementScreenHandlerSyncId();

    @Invoker("initMenu")
    public abstract void execOnScreenHandlerOpened(AbstractContainerMenu screenHandler);

    @Override
    public WolfNbtList getRescuedWolfNbts__() {
        return this.rescuedWolfNbtList;
    }
    @Override
    public WolfNbtList getWhistleWolfNbts__() {
        return this.whistleWolfNbtList;
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void restorePlayerDataAfterRespawn(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        this.rescuedWolfNbtList = ((ServerPlayerAccessor) oldPlayer).getRescuedWolfNbts__();
        this.whistleWolfNbtList = ((ServerPlayerAccessor) oldPlayer).getWhistleWolfNbts__();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void writeWolfDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        this.rescuedWolfNbtList.writeDataToNbt(nbt, WolfEventHandler.RESCUED_WOLF_NBT_KEY);
        this.whistleWolfNbtList.writeDataToNbt(nbt, WolfEventHandler.WHISTLE_WOLF_NBT_KEY);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readWolfDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        this.rescuedWolfNbtList.readDataToNbt(nbt, WolfEventHandler.RESCUED_WOLF_NBT_KEY);
        this.whistleWolfNbtList.readDataToNbt(nbt, WolfEventHandler.WHISTLE_WOLF_NBT_KEY);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        this.self = (ServerPlayer) (Object) this;
    }

    @Unique
    private void spawnDoggos(final WolfNbtList wolfNbtList) {
        final WolfNbtList canDelete = new WolfNbtList();
        wolfNbtList.getWolfNbts().forEach(wolfNbt -> {
            final int rescueTimeout = WolfNbtList.getIntOrDefault(wolfNbt, "RescueTimeout", -1);
            if (!(rescueTimeout > 0)) {
                if (rescueTimeout == 0) NBTHelper.cleanRescueWolfNbt(wolfNbt, this.self.getMaxHealth());
                final boolean success = NBTHelper.spawnWolfFromNbt(this.self, wolfNbt, rescueTimeout == 0);
                if (success) canDelete.queueWolfNbt(wolfNbt);
            }
        });
        wolfNbtList.getWolfNbts().removeIf(wolfNbt -> canDelete.getWolfNbts().contains(wolfNbt));
    }

    @Unique
    private void clearTimeoutEffects() {
        for (int i = 1; i < ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT.length; i++) {
            final MobEffectInstance inst = this.self.getEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT[i]);
            if (inst != null) {
                this.self.removeEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT[i]);
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void updateRespawnCountdown(CallbackInfo ci) {
        this.rescuedWolfNbtList.rescueTimeoutTick();
        final int level = Math.min(this.rescuedWolfNbtList.nonElapsedSize(), 11);
        final Optional<Integer> optBriefestTimeout = this.rescuedWolfNbtList.getBriefestTimeout();

        if (optBriefestTimeout.isEmpty()) {
            clearTimeoutEffects();
        }

        if (optBriefestTimeout.isPresent()) {
            final MobEffectInstance inst = this.self.getEffect(ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT[level]);
            if (inst == null) {
                clearTimeoutEffects();
                this.self.addEffect(new MobEffectInstance(
                        ModEffects.DEFEATED_WOLVES_PARTICLE_EFFECT[level],
                        optBriefestTimeout.get(),     // duration ticks
                        0,           // amplifier
                        false,       // ambient
                        false,       // showParticles
                        true         // showIcon
                ));
            }
        }
    }

    @Override
    public void queueRescuedWolfNbt__(CompoundTag nbt) {
        this.rescuedWolfNbtList.queueWolfNbt(nbt);
    }

    @Override
    public void queueWhistleWolfNbt__(CompoundTag nbt) {
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
