package github.meloweh.wolfcompanion.handler;

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

