package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record IntegerPayload(UUID uuid) implements CustomPayload {
    public static final Id<IntegerPayload> ID = new Id<>(WolfCompanion.id("non_block_pos"));
    public static final PacketCodec<RegistryByteBuf, IntegerPayload> PACKET_CODEC =
            PacketCodec.tuple(Uuids.PACKET_CODEC, IntegerPayload::uuid, IntegerPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}