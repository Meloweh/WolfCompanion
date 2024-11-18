package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
    NbtCompound wolfNbt;

    @Unique
    ServerPlayerEntity self;

    @Accessor("screenHandlerSyncId")
    public abstract int getScreenHandlerSyncId();

    @Invoker("incrementScreenHandlerSyncId")
    public abstract void execIncrementScreenHandlerSyncId();

    @Invoker("onScreenHandlerOpened")
    public abstract void execOnScreenHandlerOpened(ScreenHandler screenHandler);

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeWolfDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (wolfNbt != null) {
            nbt.put(WolfEventHandler.Wolf_NBT_KEY, wolfNbt);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readWolfDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(WolfEventHandler.Wolf_NBT_KEY)) {
            final NbtElement wolfElement = nbt.get(WolfEventHandler.Wolf_NBT_KEY);
            if (!(wolfElement instanceof NbtCompound)) {
                throw new IllegalStateException("nbt should be compound");
            }
            wolfNbt = (NbtCompound) nbt.get(WolfEventHandler.Wolf_NBT_KEY);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        this.self = (ServerPlayerEntity) (Object) this;
    }

    @Inject(method = "sleep", at = @At("HEAD"))
    private void respawnDoggo(BlockPos pos, CallbackInfo ci) {
        if (hasWolfNbt()) {
            wolfNbt.putFloat("Health", this.self.getMaxHealth());
            wolfNbt.remove("HurtTime");
            wolfNbt.remove("HurtByTimestamp");
            wolfNbt.remove("DeathTime");

            ServerWorld world = (ServerWorld) this.self.getWorld();
            final WolfEntity newWolf = EntityType.WOLF.create(world);
            newWolf.readNbt(wolfNbt);
            newWolf.refreshPositionAndAngles(self.getX() + 3, self.getY(), self.getZ(), self.getYaw(), self.getPitch());
            final boolean result = world.spawnEntity(newWolf);
            System.out.println("can respawn wolf: " + result + " " + newWolf.getHealth() + " " + newWolf.isAlive());
        } else {
            System.out.println("had no wolf respawn nbt");
        }
    }

    private void loadWolfNbt() {

    }

    @Override
    public void queueWolfNbt(NbtCompound nbt) {
        this.wolfNbt = nbt;
    }

    @Override
    public NbtCompound readWolfNbt() {
        return this.wolfNbt;
    }

    @Override
    public boolean hasWolfNbt() {
        return this.wolfNbt != null;
    }

    //void execOnScreenHandlerOpened(ScreenHandler screenHandler);

    //this.wolfTorso = ((WolfEntityModelAccessor) model).getTorso();
}
