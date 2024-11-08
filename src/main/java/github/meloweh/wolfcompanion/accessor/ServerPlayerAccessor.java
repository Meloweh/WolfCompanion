package github.meloweh.wolfcompanion.accessor;

import net.minecraft.screen.ScreenHandler;

public interface ServerPlayerAccessor {
    int getScreenHandlerSyncId();
    void execIncrementScreenHandlerSyncId();
    void execOnScreenHandlerOpened(ScreenHandler screenHandler);
}
