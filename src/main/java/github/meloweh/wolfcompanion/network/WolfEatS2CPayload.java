package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record WolfEatS2CPayload(int id, ItemStack stack) implements CustomPacketPayload {

    public static final Type<WolfEatS2CPayload> ID = new Type<>(WolfCompanion.id("s2c.wolf.eat"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WolfEatS2CPayload> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, WolfEatS2CPayload::id,
            ItemStack.STREAM_CODEC, WolfEatS2CPayload::stack,
            WolfEatS2CPayload::new);


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}