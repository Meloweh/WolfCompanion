package github.meloweh.wolfcompanion.accessor;

import github.meloweh.wolfcompanion.util.WolfNbtList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public interface ServerPlayerAccessor {
    List<CompoundTag> getWolfNbts__();
    void queueRescuedWolfNbt__(CompoundTag nbt);
    void queueWhistleWolfNbt__(CompoundTag nbt);

    WolfNbtList getRescuedWolfNbts__();
    WolfNbtList getWhistleWolfNbts__();
    void spawnWhistleWolfNbts__();
    void spawnElapsedRescueWolfNbts__();
    boolean hasElapsed__();
    MinecraftServer getServer__();
}
