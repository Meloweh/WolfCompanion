package github.meloweh.wolfcompanion.accessor;

import github.meloweh.wolfcompanion.util.WolfNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;

public interface ServerPlayerAccessor {
    void queueRescuedWolfNbt__(NbtCompound nbt);
    void queueWhistleWolfNbt__(NbtCompound nbt);

    WolfNbtList getRescuedWolfNbts__();
    WolfNbtList getWhistleWolfNbts__();
    void spawnWhistleWolfNbts__();
}
