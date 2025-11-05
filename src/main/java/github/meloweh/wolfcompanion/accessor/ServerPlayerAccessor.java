package github.meloweh.wolfcompanion.accessor;

import github.meloweh.wolfcompanion.util.WolfNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public interface ServerPlayerAccessor {
    List<NbtCompound> getWolfNbts__();
    void queueRescuedWolfNbt__(NbtCompound nbt);
    void queueWhistleWolfNbt__(NbtCompound nbt);

    WolfNbtList getRescuedWolfNbts__();
    WolfNbtList getWhistleWolfNbts__();
    void spawnWhistleWolfNbts__();
    MinecraftServer getServer__();
}
