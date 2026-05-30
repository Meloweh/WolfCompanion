package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record UuidPayload(UUID uuid, CompoundTag nbt) {

    public static final ResourceLocation ID = WolfCompanion.id("non_block_pos");

    public UuidPayload(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readNbt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.uuid);
        buf.writeNbt(this.nbt);
    }
}
