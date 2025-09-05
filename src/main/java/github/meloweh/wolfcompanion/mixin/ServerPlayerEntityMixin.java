package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.util.ConfigManager;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerAccessor {
    @Shadow
    public abstract ServerWorld getWorld();

    @Unique
    ServerPlayerEntity self;

    @Unique
    List<NbtCompound> wolfNbts = new ArrayList<>();

    @Accessor("screenHandlerSyncId")
    public abstract int getScreenHandlerSyncId();

    @Invoker("incrementScreenHandlerSyncId")
    public abstract void execIncrementScreenHandlerSyncId();

    @Invoker("onScreenHandlerOpened")
    public abstract void execOnScreenHandlerOpened(ScreenHandler screenHandler);

    @Override
    public List<NbtCompound> getWolfNbts__() {
        return wolfNbts;
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void restorePlayerDataAfterRespawn(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.wolfNbts = ((ServerPlayerAccessor) oldPlayer).getWolfNbts__();
    }

    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void spawnDoggosOnSpawn(CallbackInfo ci) {
        respawnDoggo(null, null);
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    public void writeWolfDataToNbt(WriteView view, CallbackInfo ci) {
        if (!wolfNbts.isEmpty()) {
            for (int i = 0; i < wolfNbts.size(); i++) {
                final NbtCompound wolfNbt = wolfNbts.get(i);
                view.put(WolfEventHandler.Wolf_NBT_KEY + i, NbtCompound.CODEC, wolfNbt);
            }
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    public void readWolfDataToNbt(ReadView view, CallbackInfo ci) {
        for (int i = 0; view.contains(WolfEventHandler.Wolf_NBT_KEY + i); i++) {
            final Optional<NbtCompound> wolfElement = view.read(WolfEventHandler.Wolf_NBT_KEY + i, NbtCompound.CODEC);
            wolfNbts.add(wolfElement.get());
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        this.self = (ServerPlayerEntity) (Object) this;
    }

    @Inject(method = "sleep", at = @At("HEAD"))
    private void respawnDoggo(BlockPos pos, CallbackInfo ci) {
        if (pos == null) return;
        List<NbtCompound> canDelete = new ArrayList<>();
        wolfNbts.forEach(wolfNbt -> {
            // TODO: reset instead of deleting
            //wolfNbt.remove("HurtTime");
            //wolfNbt.remove("HurtByTimestamp");
            //wolfNbt.remove("DeathTime");
            wolfNbt.remove("body_armor_item");
            wolfNbt.remove("body_armor_drop_chance");
            wolfNbt.remove("ArmorDropChances");
            //wolfNbt.remove("fall_distance");
            //wolfNbt.remove("OnGround");
            //wolfNbt.remove("Motion");
            //wolfNbt.remove("Fire");
            //wolfNbt.remove("Air");
            //wolfNbt.putFloat("Health", this.self.getMaxHealth());

            wolfNbt.putFloat("Health", 40f);
            wolfNbt.putShort("HurtTime", (short)0);
            wolfNbt.putInt("HurtByTimestamp", 0);
            wolfNbt.putShort("DeathTime", (short)0);
            wolfNbt.putBoolean("OnGround", true);
            wolfNbt.putDouble("fall_distance", 0d);

            if (!ConfigManager.config.keepWolfInventory) {
                if (!ConfigManager.config.keepWolfArmor)
                    wolfNbt.remove("ArmorItems");
                if (!ConfigManager.config.keepWolfBag)
                    wolfNbt.remove("ChestedWolf");
                wolfNbt.remove("Items");
                wolfNbt.putInt("XP", 0);
            }


            ServerWorld world = this.self.getWorld();
            //final WolfEntity newWolf = EntityType.WOLF.create(world, SpawnReason.MOB_SUMMONED);

            WolfEntity newWolf = EntityType.WOLF.create(
                    world,
                    e -> {
                        final NbtComponent nbtComponent = NbtComponent.of(wolfNbt);
                        nbtComponent.applyToEntity(e);
                        //NbtCompound.CODEC.parse(wolfNbt);
                        //e.writeData(wolfNbt);
                        //EntityType.loadFromEntityNbt(this.getWorld(), this.self, EntityType.WOLF., NbtComponent.of(wolfNbt)); // see note below
                    },
                    pos,
                    SpawnReason.MOB_SUMMONED,
                    true,  // align position to center
                    false  // spawn in water allowed?
            );

            if (newWolf != null) {
                //final NbtComponent nbtComponent = NbtComponent.of(wolfNbt);
                //nbtComponent.applyToEntity(newWolf);

                //System.out.println(wolfNbt.toString());
                //if (1==1)return;
                //final ReadView readView = NbtReadView.create(ErrorReporter.EMPTY, RegistryWrapper.WrapperLookup.of(null), wolfNbt);
            //    EntityType.loadFromEntityNbt(this.getWorld(), this.self, newWolf, NbtComponent.of(wolfNbt));

            //    newWolf.setHealth(newWolf.getMaxHealth());
            //    newWolf.clearStatusEffects();

            //    newWolf.refreshPositionAndAngles(self.getX(), self.getY(), self.getZ(), self.getYaw(), self.getPitch());
            //    newWolf.playSpawnEffects();
                final boolean success = world.spawnNewEntityAndPassengers(newWolf);
                if (success) canDelete.add(wolfNbt);
            }
        });
        canDelete.forEach(e -> wolfNbts.remove(e));
    }

    @Override
    public void queueWolfNbt(NbtCompound nbt) {
        wolfNbts.add(nbt);
    }

    public boolean removeWolfNbt(final NbtCompound nbt) {
        //System.out.println(wolfNbts.get(0).toString());
        return false;
        //return wolfNbts.removeIf(wolf -> wolf.getString("Uuid").get().equals(nbt.getString("Uuid").get()));
    }
}
