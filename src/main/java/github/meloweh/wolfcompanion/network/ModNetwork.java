package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;

public final class ModNetwork {
    private ModNetwork() {
    }

    public static void registerServerPayloads() {
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(DropWolfChestC2SPayload.ID,
                (payload, player, sender) -> forWolf(player, payload.wolfUUID(), ModNetwork::dropChest));

        ServerPlayNetworking.registerGlobalReceiver(ReleaseWolfC2SPayload.ID,
                (payload, player, sender) -> forWolf(player, payload.wolfUUID(), ModNetwork::releaseWolf));

        ServerPlayNetworking.registerGlobalReceiver(AggressionWolfC2SPayload.ID,
                (payload, player, sender) -> forWolf(player, payload.wolfUUID(), wolf -> {
                    WolfEntityProvider provider = (WolfEntityProvider) wolf;
                    provider.setAggressive__(!provider.isAggressive__());
                }));

        ServerPlayNetworking.registerGlobalReceiver(LockWolfC2SPayload.ID,
                (payload, player, sender) -> forWolf(player, payload.wolfUUID(), wolf -> {
                    WolfEntityProvider provider = (WolfEntityProvider) wolf;
                    provider.setLock__(!provider.isLock__());
                }));
    }

    private static void forWolf(ServerPlayer player, UUID wolfUuid, Consumer<Wolf> action) {
        player.server.getAllLevels().forEach(serverWorld -> {
            Entity entity = serverWorld.getEntity(wolfUuid);
            if (entity instanceof Wolf wolf) {
                action.accept(wolf);
            }
        });
    }

    private static void dropChest(Wolf wolf) {
        WolfEntityProvider provider = (WolfEntityProvider) wolf;
        provider.setShouldDropChest(true);
        closeOwnerScreen(wolf);
        provider.wolfcompanion_template_1_21_1$dropInventoryByButton();
    }

    private static void releaseWolf(Wolf wolf) {
        WolfEntityProvider provider = (WolfEntityProvider) wolf;
        provider.setShouldReleaseWolf(true);
        closeOwnerScreen(wolf);
        provider.releaseWolfButton();
    }

    private static void closeOwnerScreen(Wolf wolf) {
        LivingEntity owner = wolf.getOwner();
        if (owner instanceof ServerPlayer serverPlayer) {
            serverPlayer.closeContainer();
        }
    }
}
