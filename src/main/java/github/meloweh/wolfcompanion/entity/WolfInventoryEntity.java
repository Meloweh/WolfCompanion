package github.meloweh.wolfcompanion.entity;

public class WolfInventoryEntity /*extends BlockEntity implements ExtendedScreenHandlerFactory<WolfInventoryPayload>*/ {
    /*public static final Text TITLE = Text.translatable("container." + WolfCompanion.MOD_ID + ".example_inventory");

    private final SimpleInventory inventory = new SimpleInventory(36) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }

        @Override
        public void onOpen(PlayerEntity player) {
            super.onOpen(player);
            WolfInventoryEntity.this.numPlayersOpen++;
            update();
        }

        @Override
        public void onClose(PlayerEntity player) {
            super.onClose(player);
            WolfInventoryEntity.this.numPlayersOpen--;
            update();
        }
    };

    private final InventoryStorage inventoryStorage = InventoryStorage.of(inventory, null);

    private int numPlayersOpen;
    public float lidAngle;

    public WolfInventoryEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeInit.EXAMPLE_INVENTORY_BLOCK_ENTITY, pos, state);
    }

    @Override
    public WolfInventoryPayload getScreenOpeningData(ServerPlayerEntity player) {
        player.getWorld(
        return new WolfInventoryPayload();
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new WolfInventoryScreenHandler(syncId, playerInventory, this);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.inventory.getHeldStacks(), registryLookup);

        if(nbt.contains("NumPlayersOpen", NbtElement.INT_TYPE)) {
            this.numPlayersOpen = nbt.getInt("NumPlayersOpen");
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
        super.writeNbt(nbt, registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = super.toInitialChunkDataNbt(registryLookup);
        writeNbt(nbt, registryLookup);
        nbt.putInt("NumPlayersOpen", this.numPlayersOpen);
        return nbt;
    }

    private void update() {
        markDirty();
        if(world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return inventoryStorage;
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    public int getNumPlayersOpen() {
        return this.numPlayersOpen;
    }*/
}
