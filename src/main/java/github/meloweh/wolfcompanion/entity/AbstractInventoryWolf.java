package github.meloweh.wolfcompanion.entity;

import java.util.UUID;

import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.screenhandler.WolfScreenHandler;
import net.minecraft.block.Blocks;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractInventoryWolf extends AnimalEntity implements InventoryChangedListener, RideableInventory, JumpingMount, Saddleable {
    private static final TrackedData<Byte> HORSE_FLAGS = DataTracker.registerData(AbstractInventoryWolf.class, TrackedDataHandlerRegistry.BYTE);
    private static final int SADDLED_FLAG = 4;
    protected SimpleInventory items;
    @Nullable
    private UUID ownerUuid;
    private final Inventory inventory = new SingleStackInventory() {
        @Override
        public ItemStack getStack() {
            return AbstractInventoryWolf.this.getBodyArmor();
        }

        @Override
        public void setStack(ItemStack stack) {
            AbstractInventoryWolf.this.equipBodyArmor(stack);
        }

        @Override
        public void markDirty() {
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return player.getVehicle() == AbstractInventoryWolf.this || player.canInteractWithEntity(AbstractInventoryWolf.this, 4.0);
        }
    };

    private static final TrackedData<Boolean> CHEST = DataTracker.registerData(AbstractInventoryWolf.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final EntityDimensions babyBaseDimensions;

    protected AbstractInventoryWolf(EntityType<? extends AbstractInventoryWolf> entityType, World world) {
        super(entityType, world);
        this.onChestedStatusChanged();
        this.babyBaseDimensions = entityType.getDimensions()
                .withAttachments(EntityAttachments.builder().add(EntityAttachmentType.PASSENGER, 0.0F, entityType.getHeight() - 0.15625F, 0.0F))
                .scaled(0.5F);
    }

    protected boolean getHorseFlag(int bitmask) {
        return (this.dataTracker.get(HORSE_FLAGS) & bitmask) != 0;
    }

    protected void setHorseFlag(int bitmask, boolean flag) {
        byte b = this.dataTracker.get(HORSE_FLAGS);
        if (flag) {
            this.dataTracker.set(HORSE_FLAGS, (byte)(b | bitmask));
        } else {
            this.dataTracker.set(HORSE_FLAGS, (byte)(b & ~bitmask));
        }
    }

    public void setOwnerUuid(@Nullable UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    @Override
    public void saddle(ItemStack stack, @Nullable SoundCategory soundCategory) {
        this.items.setStack(0, stack);
    }

    public void equipHorseArmor(PlayerEntity player, ItemStack stack) {
        if (this.isHorseArmor(stack)) {
            this.equipBodyArmor(stack.copyWithCount(1));
            stack.decrementUnlessCreative(1, player);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.getHorseFlag(SADDLED_FLAG);
    }

    public final int getInventorySize() {
        return getInventorySize(this.getInventoryColumns());
    }

    public static int getInventorySize(int columns) {
        return columns * 3 + 1;
    }

    protected void onChestedStatusChanged() {
        SimpleInventory simpleInventory = this.items;
        this.items = new SimpleInventory(this.getInventorySize());
        if (simpleInventory != null) {
            simpleInventory.removeListener(this);
            int i = Math.min(simpleInventory.size(), this.items.size());

            for (int j = 0; j < i; j++) {
                ItemStack itemStack = simpleInventory.getStack(j);
                if (!itemStack.isEmpty()) {
                    this.items.setStack(j, itemStack.copy());
                }
            }
        }

        this.items.addListener(this);
        this.updateSaddledFlag();
    }

    protected void updateSaddledFlag() {
        if (!this.getWorld().isClient) {
            this.setHorseFlag(SADDLED_FLAG, !this.items.getStack(0).isEmpty());
        }
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        boolean bl = this.isSaddled();
        this.updateSaddledFlag();
        if (this.age > 20 && !bl && this.isSaddled()) {
            this.playSound(this.getSaddleSound(), 0.5F, 1.0F);
        }
    }

    public void openWolfInventory(final ServerPlayerEntity player, AbstractInventoryWolf horse, Inventory inventory) {
        if (player.currentScreenHandler != player.playerScreenHandler) {
            player.closeHandledScreen();
        }

        ((ServerPlayerAccessor) player).execIncrementScreenHandlerSyncId();
        int i = horse.getInventoryColumns();
        player.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(((ServerPlayerAccessor) player).getScreenHandlerSyncId(), i, horse.getId()));
        player.currentScreenHandler = new WolfScreenHandler(((ServerPlayerAccessor) player).getScreenHandlerSyncId(), player.getInventory(), inventory, horse, i);
        ((ServerPlayerAccessor) player).execOnScreenHandlerOpened(player.currentScreenHandler);
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!this.getWorld().isClient) {
            openWolfInventory((ServerPlayerEntity) player, this, this.items);
        }
    }

    public ActionResult interactCompanion(PlayerEntity player, Hand hand) {
        if (this.hasPassengers() || this.isBaby()) {
            return super.interactMob(player, hand);
        } else if (player.shouldCancelInteraction()) {
            this.openInventory(player);
            return ActionResult.success(this.getWorld().isClient);
        } else {
            ItemStack itemStack = player.getStackInHand(hand);
            if (!itemStack.isEmpty()) {
                ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
                if (actionResult.isAccepted()) {
                    return actionResult;
                }

                if (this.canUseSlot(EquipmentSlot.BODY) && this.isHorseArmor(itemStack) && !this.isWearingBodyArmor()) {
                    this.equipHorseArmor(player, itemStack);
                    return ActionResult.success(this.getWorld().isClient);
                }
            }

            return ActionResult.success(this.getWorld().isClient);
        }
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        return false;
    }

    private StackReference getOtherStackReference(int mappedIndex) {
        int i = mappedIndex - 400;
        if (i == 0) {
            return new StackReference() {
                @Override
                public ItemStack get() {
                    return AbstractInventoryWolf.this.items.getStack(0);
                }

                @Override
                public boolean set(ItemStack stack) {
                    if (!stack.isEmpty() && !stack.isOf(Items.SADDLE)) {
                        return false;
                    } else {
                        AbstractInventoryWolf.this.items.setStack(0, stack);
                        AbstractInventoryWolf.this.updateSaddledFlag();
                        return true;
                    }
                }
            };
        } else {
            int j = mappedIndex - 500 + 1;
            return j >= 1 && j < this.items.size() ? StackReference.of(this.items, j) : super.getStackReference(mappedIndex);
        }
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        return mappedIndex == 499 ? new StackReference() {
            @Override
            public ItemStack get() {
                return AbstractInventoryWolf.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
            }

            @Override
            public boolean set(ItemStack stack) {
                if (stack.isEmpty()) {
                    if (AbstractInventoryWolf.this.hasChest()) {
                        AbstractInventoryWolf.this.setHasChest(false);
                        AbstractInventoryWolf.this.onChestedStatusChanged();
                    }

                    return true;
                } else if (stack.isOf(Items.CHEST)) {
                    if (!AbstractInventoryWolf.this.hasChest()) {
                        AbstractInventoryWolf.this.setHasChest(true);
                        AbstractInventoryWolf.this.onChestedStatusChanged();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } : getOtherStackReference(mappedIndex);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.initAttributes(world.getRandom());
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    public boolean areInventoriesDifferent(Inventory inventory) {
        return this.items != inventory;
    }

    public final Inventory getInventory() {
        return this.inventory;
    }

    protected void initAttributes(Random random) {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HORSE_FLAGS, (byte)0);
        builder.add(CHEST, false);
    }

    public boolean hasChest() {
        return this.dataTracker.get(CHEST);
    }

    public void setHasChest(boolean hasChest) {
        this.dataTracker.set(CHEST, hasChest);
    }

    @Override
    public EntityDimensions getBaseDimensions(EntityPose pose) {
        return this.isBaby() ? this.babyBaseDimensions : super.getBaseDimensions(pose);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.items != null) {
            for (int i = 0; i < this.items.size(); i++) {
                ItemStack itemStack = this.items.getStack(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                    this.dropStack(itemStack);
                }
            }
        }

        if (this.hasChest()) {
            if (!this.getWorld().isClient) {
                this.dropItem(Blocks.CHEST);
            }

            this.setHasChest(false);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        if (!this.items.getStack(0).isEmpty()) {
            nbt.put("SaddleItem", this.items.getStack(0).encode(this.getRegistryManager()));
        }

        nbt.putBoolean("ChestedHorse", this.hasChest());
        if (this.hasChest()) {
            NbtList nbtList = new NbtList();

            for (int i = 1; i < this.items.size(); i++) {
                ItemStack itemStack = this.items.getStack(i);
                if (!itemStack.isEmpty()) {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte)(i - 1));
                    nbtList.add(itemStack.encode(this.getRegistryManager(), nbtCompound));
                }
            }

            nbt.put("Items", nbtList);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID uUID;
        if (nbt.containsUuid("Owner")) {
            uUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }

        if (uUID != null) {
            this.setOwnerUuid(uUID);
        }

        if (nbt.contains("SaddleItem", NbtElement.COMPOUND_TYPE)) {
            ItemStack itemStack = (ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("SaddleItem")).orElse(ItemStack.EMPTY);
            if (itemStack.isOf(Items.SADDLE)) {
                this.items.setStack(0, itemStack);
            }
        }

        this.updateSaddledFlag();
        ////////
        this.setHasChest(nbt.getBoolean("ChestedHorse"));
        this.onChestedStatusChanged();
        if (this.hasChest()) {
            NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);

            for (int i = 0; i < nbtList.size(); i++) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                int j = nbtCompound.getByte("Slot") & 255;
                if (j < this.items.size() - 1) {
                    this.items.setStack(j + 1, (ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY));
                }
            }
        }

        this.updateSaddledFlag();
    }

    public ActionResult interactCompanionV2(PlayerEntity player, Hand hand) {
        boolean bl = !this.isBaby() && player.shouldCancelInteraction();
        if (!this.hasPassengers() && !bl) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (!itemStack.isEmpty()) {
                if (!this.hasChest() && itemStack.isOf(Items.CHEST)) {
                    this.addChest(player, itemStack);
                    return ActionResult.success(this.getWorld().isClient);
                }
            }

            return interactCompanion(player, hand);
        } else {
            return interactCompanion(player, hand);
        }
    }

    private void addChest(PlayerEntity player, ItemStack chest) {
        this.setHasChest(true);
        this.playAddChestSound();
        chest.decrementUnlessCreative(1, player);
        this.onChestedStatusChanged();
    }

    protected void playAddChestSound() {
        this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    public int getInventoryColumns() {
        return this.hasChest() ? 5 : 0;
    }
}
