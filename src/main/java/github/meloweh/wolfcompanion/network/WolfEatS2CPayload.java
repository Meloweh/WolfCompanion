package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record WolfEatS2CPayload(int id, ItemStack stack) implements CustomPayload {

    public static final Id<WolfEatS2CPayload> ID = new Id<>(WolfCompanion.id("s2c.wolf.eat"));
    public static final PacketCodec<RegistryByteBuf, WolfEatS2CPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, WolfEatS2CPayload::id,
            ItemStack.PACKET_CODEC, WolfEatS2CPayload::stack,
            WolfEatS2CPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}