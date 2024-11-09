package github.meloweh.wolfcompanion.handler;

import github.meloweh.wolfcompanion.screenhandler.WolfInventoryScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class WolfInventoryHandler { /*implements ExtendedScreenHandlerFactory {
    private final SimpleInventory inventory = new SimpleInventory(36);
    private WolfEntity wolfEntity;
    public static final Text TITLE = Text.translatable("container.wolf_inventory");

    public WolfInventoryHandler(WolfEntity wolfEntity) {
        this.wolfEntity = wolfEntity;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        // Encode the wolf's UUID instead of a BlockPos
        UUID wolfUuid = wolfEntity.getUuid();
        buf.writeUuid(wolfUuid);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        // Pass the SimpleInventory and the position of the wolf for distance checks
        return new WolfInventoryScreenHandler(syncId, playerInventory, inventory, wolfEntity.blockPosition());
    }

    public void openInventory(PlayerEntity player) {
        player.openHorseInventory(this);
        inventory.onOpen(player);
    }

    public void closeInventory(PlayerEntity player) {
        inventory.onClose(player);
    }

    public SimpleInventory getInventory() {
        return inventory;
    }

    // Mimic persistence behavior using NBT
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, this.inventory.getHeldStacks());
    }

    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.inventory.getHeldStacks());
    }*/
}

