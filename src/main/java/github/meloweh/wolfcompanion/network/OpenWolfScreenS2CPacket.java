package github.meloweh.wolfcompanion.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class OpenWolfScreenS2CPacket {/*implements Packet<ClientPlayPacketListener> {
    public static final PacketCodec<PacketByteBuf, OpenWolfScreenS2CPacket> CODEC = Packet.createCodec(
            OpenWolfScreenS2CPacket::write, OpenWolfScreenS2CPacket::new
    );
    private final int syncId;
    private final int slotColumnCount;
    private final int wolfId;

    public OpenWolfScreenS2CPacket(int syncId, int slotColumnCount, int wolfId) {
        this.syncId = syncId;
        this.slotColumnCount = slotColumnCount;
        this.wolfId = wolfId;
    }

    private OpenWolfScreenS2CPacket(PacketByteBuf buf) {
        this.syncId = buf.readUnsignedByte();
        this.slotColumnCount = buf.readVarInt();
        this.wolfId = buf.readInt();
    }

    private void write(PacketByteBuf buf) {
        buf.writeByte(this.syncId);
        buf.writeVarInt(this.slotColumnCount);
        buf.writeInt(this.wolfId);
    }



    @Override
    public PacketType<OpenWolfScreenS2CPacket> getPacketId() {
        return PlayPackets.WOLF_SCREEN_OPEN;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onOpenWolfScreen(this);
    }

    public int getSyncId() {
        return this.syncId;
    }

    public int getSlotColumnCount() {
        return this.slotColumnCount;
    }

    public int getWolfId() {
        return this.wolfId;
    }*/
}
