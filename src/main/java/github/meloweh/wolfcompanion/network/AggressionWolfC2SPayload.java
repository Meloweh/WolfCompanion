package github.meloweh.wolfcompanion.network;

import github.meloweh.wolfcompanion.WolfCompanion;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

public record AggressionWolfC2SPayload(UUID wolfUUID) implements FabricPacket {

    public static final PacketType<AggressionWolfC2SPayload> ID =
            PacketType.create(WolfCompanion.id("c2s.wolf.aggression"), AggressionWolfC2SPayload::new);

    public AggressionWolfC2SPayload(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.wolfUUID);
    }

    @Override
    public PacketType<?> getType() {
        return ID;
    }
}
