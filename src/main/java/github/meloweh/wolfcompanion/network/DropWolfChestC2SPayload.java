package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record DropWolfChestC2SPayload(UUID wolfUUID) implements CustomPayload {

    public static final Id<DropWolfChestC2SPayload> ID = new Id<>(WolfCompanion.id("gui_interact_c2s_payload"));
    public static final PacketCodec<RegistryByteBuf, DropWolfChestC2SPayload> PACKET_CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, DropWolfChestC2SPayload::wolfUUID,
            DropWolfChestC2SPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}