package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record ReleaseWolfC2SPayload(UUID wolfUUID) implements CustomPayload {

    public static final Id<ReleaseWolfC2SPayload> ID = new Id<>(WolfCompanion.id("gui_interact_c2s_payload2"));
    public static final PacketCodec<RegistryByteBuf, ReleaseWolfC2SPayload> PACKET_CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, ReleaseWolfC2SPayload::wolfUUID,
            ReleaseWolfC2SPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}