package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record IntegerPayload(UUID uuid) implements CustomPacketPayload {
    public static final Type<IntegerPayload> ID = new Type<>(WolfCompanion.id("non_block_pos"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IntegerPayload> PACKET_CODEC =
            StreamCodec.composite(UUIDUtil.STREAM_CODEC, IntegerPayload::uuid, IntegerPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}