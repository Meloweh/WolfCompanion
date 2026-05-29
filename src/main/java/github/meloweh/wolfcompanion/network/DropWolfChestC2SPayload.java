package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DropWolfChestC2SPayload(UUID wolfUUID) implements CustomPacketPayload {

    public static final Type<DropWolfChestC2SPayload> ID = new Type<>(WolfCompanion.id("gui_interact_c2s_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DropWolfChestC2SPayload> PACKET_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, DropWolfChestC2SPayload::wolfUUID,
            DropWolfChestC2SPayload::new);


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}