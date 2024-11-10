package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record UuidPayload(UUID uuid, NbtCompound nbt) implements CustomPayload {

    public static final Id<UuidPayload> ID = new Id<>(WolfCompanion.id("non_block_pos"));
    public static final PacketCodec<RegistryByteBuf, UuidPayload> PACKET_CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, UuidPayload::uuid,
            PacketCodecs.NBT_COMPOUND, UuidPayload::nbt,
            UuidPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}