package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UuidPayload(UUID uuid, CompoundTag nbt) implements CustomPacketPayload {

    public static final Type<UuidPayload> ID = new Type<>(WolfCompanion.id("non_block_pos"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UuidPayload> PACKET_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, UuidPayload::uuid,
            ByteBufCodecs.COMPOUND_TAG, UuidPayload::nbt,
            UuidPayload::new);


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}