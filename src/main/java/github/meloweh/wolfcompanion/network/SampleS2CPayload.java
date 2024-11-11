package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record SampleS2CPayload(String mystring, int myint) implements CustomPayload {

    public static final Id<SampleS2CPayload> ID = new Id<>(WolfCompanion.id("sample_s2c_payload"));
    public static final PacketCodec<RegistryByteBuf, SampleS2CPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SampleS2CPayload::mystring,
            PacketCodecs.INTEGER, SampleS2CPayload::myint,
            SampleS2CPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}