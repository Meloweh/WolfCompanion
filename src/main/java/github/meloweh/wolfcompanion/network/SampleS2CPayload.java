package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SampleS2CPayload(String mystring, int myint) implements CustomPacketPayload {

    public static final Type<SampleS2CPayload> ID = new Type<>(WolfCompanion.id("sample_s2c_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SampleS2CPayload> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SampleS2CPayload::mystring,
            ByteBufCodecs.INT, SampleS2CPayload::myint,
            SampleS2CPayload::new);


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}