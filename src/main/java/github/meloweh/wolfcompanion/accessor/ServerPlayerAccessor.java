package github.meloweh.wolfcompanion.accessor;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;

import java.util.List;

public interface ServerPlayerAccessor {
    int getScreenHandlerSyncId();
    void execIncrementScreenHandlerSyncId();
    void execOnScreenHandlerOpened(ScreenHandler screenHandler);
    //void writeWolfDataToNbt(NbtCompound nbt);
    //void readWolfDataToNbt(NbtCompound nbt);
    void queueWolfNbt(NbtCompound nbt);
    NbtCompound readWolfNbt();
    boolean hasWolfNbt();
    boolean isWolfNbtSaved();
    List<NbtCompound> getWolfNbts__();
}
