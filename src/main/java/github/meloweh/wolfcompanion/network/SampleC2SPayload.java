package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SampleC2SPayload(String mystring, int myint) implements CustomPayload {

    public static final Id<SampleC2SPayload> ID = new Id<>(WolfCompanion.id("sample_c2s_payload"));
    public static final PacketCodec<RegistryByteBuf, SampleC2SPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SampleC2SPayload::mystring,
            PacketCodecs.INTEGER, SampleC2SPayload::myint,
            SampleC2SPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}