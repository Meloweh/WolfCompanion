package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public record WolfInventoryPayload(OpenWolfScreenS2CPacket packet) implements CustomPayload {
    public static final CustomPayload.Id<WolfInventoryPayload> ID = new CustomPayload.Id<>(WolfCompanion.id("wolf_inventory_payload"));
    //public static final PacketCodec<PacketByteBuf, WolfInventoryPayload> PACKET_CODEC
    //        = PacketCodec.tuple(OpenWolfScreenS2CPacket.CODEC, WolfInventoryPayload::packet, WolfInventoryPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}

/*
public record WolfInventoryPayload(UUID myUuid) implements CustomPayload {
    public static final CustomPayload.Id<WolfInventoryPayload> ID = new CustomPayload.Id<>(WolfCompanion.id("wolf_inventory_payload"));
    public static final PacketCodec<ByteBuf, WolfInventoryPayload> PACKET_CODEC
            = PacketCodec.tuple(Uuids.PACKET_CODEC, WolfInventoryPayload::myUuid, WolfInventoryPayload::new);


    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}*/