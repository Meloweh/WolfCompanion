package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record LockWolfC2SPayload(UUID wolfUUID) implements CustomPayload {

    public static final Id<LockWolfC2SPayload> ID = new Id<>(WolfCompanion.id("c2s.wolf.lock"));
    public static final PacketCodec<RegistryByteBuf, LockWolfC2SPayload> PACKET_CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, LockWolfC2SPayload::wolfUUID,
            LockWolfC2SPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}