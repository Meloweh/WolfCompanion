package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
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
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerAccessor {
    //@Unique
    //NbtCompound wolfNbt;

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

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeWolfDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!wolfNbts.isEmpty()) {
            for (int i = 0; i < wolfNbts.size(); i++) {
                final NbtCompound wolfNbt = wolfNbts.get(i);
                nbt.put(WolfEventHandler.Wolf_NBT_KEY + i, wolfNbt);
            }
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readWolfDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        for (int i = 0; nbt.contains(WolfEventHandler.Wolf_NBT_KEY + i); i++) {
            final NbtElement wolfElement = nbt.get(WolfEventHandler.Wolf_NBT_KEY + i);
            if (!(wolfElement instanceof NbtCompound)) {
                throw new IllegalStateException("nbt should be compound");
            }
            wolfNbts.add((NbtCompound) wolfElement);
        }
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        this.self = (ServerPlayerEntity) (Object) this;
//        System.out.println("player init");
//        if (!self.getWorld().isClient) {
//            System.out.println("player print");
//            final File worldDirectory = self.getServer().getSavePath(WorldSavePath.ROOT).toFile();
//            //modifyPlayerData(new File(worldDirectory, "playerdata"),self.getUuid());
//        }
    }

    @Inject(method = "sleep", at = @At("HEAD"))
    private void respawnDoggo(BlockPos pos, CallbackInfo ci) {
        wolfNbts.forEach(wolfNbt -> {
            wolfNbt.putFloat("Health", this.self.getMaxHealth());
            wolfNbt.remove("HurtTime");
            wolfNbt.remove("HurtByTimestamp");
            wolfNbt.remove("DeathTime");
            wolfNbt.remove("ArmorItems");
            wolfNbt.remove("body_armor_item");
            wolfNbt.remove("body_armor_drop_chance");
            wolfNbt.remove("ArmorDropChances");

            ServerWorld world = (ServerWorld) this.self.getWorld();
            final WolfEntity newWolf = EntityType.WOLF.create(world);
            newWolf.readNbt(wolfNbt);
            newWolf.refreshPositionAndAngles(self.getX(), self.getY(), self.getZ(), self.getYaw(), self.getPitch());
            newWolf.playSpawnEffects();
            final boolean result = world.spawnEntity(newWolf);
        });
        wolfNbts.clear();
    }

    private void loadWolfNbt() {

    }

    @Override
    public void queueWolfNbt(NbtCompound nbt) {
        wolfNbts.add(nbt);
        //this.wolfNbt = nbt;
    }

//    @Override
//    public NbtCompound readWolfNbt() {
//        return this.wolfNbt;
//    }
//
//    @Override
//    public boolean hasWolfNbt() {
//        return this.wolfNbt != null;
//    }

    //void execOnScreenHandlerOpened(ScreenHandler screenHandler);

    //this.wolfTorso = ((WolfEntityModelAccessor) model).getTorso();
}
