package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AggressionWolfC2SPayload(UUID wolfUUID) implements CustomPacketPayload {

    public static final Type<AggressionWolfC2SPayload> ID = new Type<>(WolfCompanion.id("c2s.wolf.aggression"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AggressionWolfC2SPayload> PACKET_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, AggressionWolfC2SPayload::wolfUUID,
            AggressionWolfC2SPayload::new);


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}