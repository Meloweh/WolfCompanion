package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record WolfInventoryPayload(UUID myUuid) implements CustomPayload {
    public static final CustomPayload.Id<WolfInventoryPayload> ID = new CustomPayload.Id<>(WolfCompanion.id("wolf_inventory_payload"));
    public static final PacketCodec<ByteBuf, WolfInventoryPayload> CODEC
            = PacketCodec.tuple(Uuids.PACKET_CODEC, WolfInventoryPayload::myUuid, WolfInventoryPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}