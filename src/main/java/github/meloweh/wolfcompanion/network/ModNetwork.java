package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;

public final class ModNetwork {
    private ModNetwork() {
    }

    public static void registerServerPayloads() {
        PayloadTypeRegistry.playC2S().register(DropWolfChestC2SPayload.ID, DropWolfChestC2SPayload.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(ReleaseWolfC2SPayload.ID, ReleaseWolfC2SPayload.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(AggressionWolfC2SPayload.ID, AggressionWolfC2SPayload.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(LockWolfC2SPayload.ID, LockWolfC2SPayload.PACKET_CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(DropWolfChestC2SPayload.ID, (payload, context) ->
                context.server().execute(() -> forWolf(context, payload.wolfUUID(), ModNetwork::dropChest)));

        ServerPlayNetworking.registerGlobalReceiver(ReleaseWolfC2SPayload.ID, (payload, context) ->
                context.server().execute(() -> forWolf(context, payload.wolfUUID(), ModNetwork::releaseWolf)));

        ServerPlayNetworking.registerGlobalReceiver(AggressionWolfC2SPayload.ID, (payload, context) ->
                context.server().execute(() -> forWolf(context, payload.wolfUUID(), wolf -> {
                    WolfEntityProvider provider = (WolfEntityProvider) wolf;
                    provider.setAggressive__(!provider.isAggressive__());
                })));

        ServerPlayNetworking.registerGlobalReceiver(LockWolfC2SPayload.ID, (payload, context) ->
                context.server().execute(() -> forWolf(context, payload.wolfUUID(), wolf -> {
                    WolfEntityProvider provider = (WolfEntityProvider) wolf;
                    provider.setLock__(!provider.isLock__());
                })));
    }

    private static void forWolf(ServerPlayNetworking.Context context, UUID wolfUuid, Consumer<Wolf> action) {
        context.server().getAllLevels().forEach(serverWorld -> {
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
