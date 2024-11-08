package github.meloweh.wolfcompanion.util;

import github.meloweh.wolfcompanion.mixin.ServerPlayerEntityMixin;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class MixinHelpers {
    @Nullable
    public static int invokeSomePrivateMethod(ServerPlayerEntity player) {
        if (player instanceof ServerPlayerEntityMixin) {
            return ((ServerPlayerEntityMixin) player).invokeSomePrivateMethod();
        } else {
            // Optionally handle the case where the mixin wasn't applied
            System.out.println("Mixin not applied to ServerPlayerEntity!");
            return -1; // or throw an exception, or handle as appropriate
        }
    }
}

