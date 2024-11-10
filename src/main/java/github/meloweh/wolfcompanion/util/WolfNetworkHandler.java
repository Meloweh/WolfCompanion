package github.meloweh.wolfcompanion.util;

import github.meloweh.wolfcompanion.mixin.WolfEntityMixin;
import github.meloweh.wolfcompanion.network.UuidPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class WolfNetworkHandler {
    public static final String WOLF_INVENTORY_UPDATE = "wolf_inventory_update";

    public static void register() {
        //PayloadTypeRegistry.playC2S().register(UuidPayload.ID, UuidPayload.PACKET_CODEC);

        //ServerPlayNetworking.registerGlobalReceiver(UuidPayload.ID, (payload, context) -> {

        //});
    }
    /*
    public static void sendInventoryUpdate(WolfEntity wolf, ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(wolf.getUuid());
        InventoryUtil.writeInventoryToBuffer(buf, wolf.getInventory());
        ServerPlayNetworking.send(player, ModPackets.WOLF_INVENTORY_UPDATE, buf);
    }

    public void onInventoryChanged(int slotIndex, ItemStack newStack) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(wolfUuid);
        buf.writeInt(1);  // Sending one item change for simplicity
        buf.writeInt(slotIndex);
        buf.writeItemStack(newStack);

        ClientPlayNetworking.send(CUSTOM_INVENTORY_UPDATE_ID, buf);
    }


    public static void setupPacketListeners() {
        ServerPlayNetworking.registerGlobalReceiver(WOLF_INVENTORY_UPDATE, (server, player, handler, buf, responseSender) -> {
            UUID wolfUUID = buf.;
            SimpleInventory inventory = InventoryUtil.readInventoryFromBuffer(buf);
            server.execute(() -> {
                Entity entity = server.getWorld().getEntity(wolfUUID);
                if (entity instanceof WolfEntityMixin) {
                    ((WolfEntityMixin) entity).setInventory(inventory);
                    // Optionally update all clients that can see this wolf
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CUSTOM_INVENTORY_UPDATE_ID, (server, player, handler, buf, responseSender) -> {
            UUID wolfUUID = buf.readUuid();
            int size = buf.readInt();
            List<ItemStack> inventory = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                inventory.add(buf.readItemStack());
            }

            server.execute(() -> {
                Entity entity = player.world.getEntity(wolfUUID);
                if (entity instanceof WolfEntity) {
                    ((WolfEntity) entity).setInventory(inventory);
                    entity.setDirty(true);
                    // Optionally update all clients viewing this inventory
                    for (ServerPlayerEntity viewer : PlayerLookup.tracking(entity)) {
                        sendInventoryUpdate((WolfEntity) entity, viewer);
                    }
                }
            });
        });

    }*/
}

