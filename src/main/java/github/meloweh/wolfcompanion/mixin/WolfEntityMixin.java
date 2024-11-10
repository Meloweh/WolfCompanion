package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.*;
import github.meloweh.wolfcompanion.entity.WolfInventoryEntity;
import github.meloweh.wolfcompanion.network.BlockPosPayload;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.screenhandler.ExampleInventoryScreenHandler;
import github.meloweh.wolfcompanion.screenhandler.ExampleInventoryScreenHandler2;
import github.meloweh.wolfcompanion.screenhandler.WolfScreenHandler;
import github.meloweh.wolfcompanion.util.NBTHelper;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Blocks;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin implements
        InventoryChangedListener,
        RideableInventory,
        Tameable,
        Saddleable,
        WolfEntityProvider,
        EntityAccessor,
        MobEntityAccessor,
        ExtendedScreenHandlerFactory<UuidPayload>,
        WolfEntityMixinProvider {
    @Unique
    private static final TrackedData<Byte> HORSE_FLAGS = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BYTE);
    @Unique
    private static final int SADDLED_FLAG = 4;
    @Unique
    protected SimpleInventory items;
    @Unique
    private Inventory inventory;
    @Unique
    private WolfEntity self;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {

        //WolfEntity wolf;
        //wolf.hasPassenger()
        self = (WolfEntity) (Object) this;
        inventory = new SingleStackInventory() {


            @Override
            public ItemStack getStack() {
                //return ((MobEntityAccessor) WolfEntityMixin.this).getGetBodyArmor();
                return WolfEntityMixin.this.getBodyArmor();
            }

            @Override
            public void setStack(ItemStack stack) {
                //((MobEntityAccessor) WolfEntityMixin.this).invokeEquipBodyArmor(stack);
                items.setStack(10, stack);
            }

            @Override
            public void markDirty() {
                System.out.println("wolf mark dirty");
            }

            @Override
            public void onOpen(PlayerEntity player) {
                System.out.println("wolf on open");
                SingleStackInventory.super.onOpen(player);
            }

            @Override
            public void onClose(PlayerEntity player) {
                System.out.println("wolf on close");
                SingleStackInventory.super.onClose(player);
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return player.canInteractWithEntity(self, 4.0);
            }
            /*@Override
            public boolean canPlayerUse(PlayerEntity player) {
                return player.getVehicle() == WolfEntityMixin.this || player.canInteractWithEntity(WolfEntityMixin.this, 4.0);
            }*/
        };
        this.onChestedStatusChanged();
    }

    @Override
    public SimpleInventory wolfcompanion_template_1_21_1$getItemsInventory() {
        return this.items;
    }

    @Unique
    private static final TrackedData<Boolean> CHEST = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    //private static final TrackedData<Boolean> INVENTORY = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private boolean isDirty = false;

    private void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    /*
    private void updateDataToClients() {
        if (this.isDirty() && !self.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) self.getWorld();
            EntityS2CPacket packet = new NbtQueryResponseS2CPacket(self.getUuid(), this.customData);
            serverWorld.getPlayers().stream().forEach(player ->
                    serverWorld.getServer().getPlayerManager().sendToAll(packet));
            setDirty(false);  // Reset dirty after sending update
        }
    }*/


    @Unique
    protected boolean getHorseFlag(int bitmask) {
        return (getDataTracker(self).get(HORSE_FLAGS) & bitmask) != 0;
    }

    @Unique
    protected void setHorseFlag(int bitmask, boolean flag) {
        byte b = getDataTracker(self).get(HORSE_FLAGS);
        if (flag) {
            getDataTracker(self).set(HORSE_FLAGS, (byte)(b | bitmask));
        } else {
            getDataTracker(self).set(HORSE_FLAGS, (byte)(b & ~bitmask));
        }
    }

    @Unique
    public final int getInventorySize() {
        return getInventorySize(this.getInventoryColumns());
    }

    @Unique
    private static int getInventorySize(int columns) {
        return columns * 3 + 1;
    }

    @Unique
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

    @Unique
    protected void updateSaddledFlag() {
        /*if (!self.getWorld().isClient) {
            this.setHorseFlag(SADDLED_FLAG, !this.items.getStack(0).isEmpty());
        }*/
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        this.updateSaddledFlag();
    }

    @Unique
    public WolfEntityMixin getThis() {
        return this;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ExampleInventoryScreenHandler2(syncId, playerInventory, self, NBTHelper.getWolfNBT(self));
    }

    @Override
    public UuidPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new UuidPayload(self.getUuid(), NBTHelper.getWolfNBT(self));
    }



    @Unique
    public void openWolfInventory(final ServerPlayerEntity player, WolfEntityMixin wolfEntityMixin, Inventory inventory) {
        /*System.out.println(player!= null);
        assert player != null;
        if (player.currentScreenHandler != player.playerScreenHandler) {
            player.closeHandledScreen();
        }
        ((ServerPlayerAccessor) player).execIncrementScreenHandlerSyncId();

        int i = wolfEntityMixin.getInventoryColumns();
        player.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(((ServerPlayerAccessor) player).getScreenHandlerSyncId(), i, wolfEntityMixin.getId()));
        player.currentScreenHandler = new WolfScreenHandler(((ServerPlayerAccessor) player).getScreenHandlerSyncId(),
                player.getInventory(),
                inventory, self, this.inventory, i, this.items);
        ((ServerPlayerAccessor) player).execOnScreenHandlerOpened(player.currentScreenHandler);*/
        //final int syncId = ((ServerPlayerAccessor) player).getScreenHandlerSyncId();
        //System.out.println("SyncId: " + syncId);
        //new ExampleInventoryScreenHandler2(syncId, player.getInventory(), self)
        player.openHandledScreen(this);
        System.out.println("POST player.openHandledScreen(this);");
    }

    @Unique
    public boolean areInventoriesDifferent(Inventory inventory) {
        return this.items != inventory;
    }

    @Unique
    private int getId() {
        System.out.println("getId");
        System.out.println("getId: " + ((self == null) ? "self is null" : "self is not null"));
        return self.getId();
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!self.getWorld().isClient) {
            System.out.println("TESTING openInventory openWolfInventory");
            openWolfInventory((ServerPlayerEntity) player, this, player.getInventory());
            //player.openHorseInventory();
        }
    }

    @Unique
    public ActionResult interactCompanion(PlayerEntity player, Hand hand) {
        System.out.println("TESTING interactCompanion");

        if (player.shouldCancelInteraction()) {
            System.out.println("TESTING interactCompanionV2 openInventory");
            this.openInventory(player);
            return ActionResult.success(self.getWorld().isClient);
        } /*else {
            ItemStack itemStack = player.getStackInHand(hand);
            if (!itemStack.isEmpty()) {
                ActionResult actionResult = itemStack.useOnEntity(player, self, hand);
                if (actionResult.isAccepted()) {
                    return actionResult;
                }

                //if (self.canUseSlot(EquipmentSlot.BODY) && self.isHorseArmor(itemStack) && !this.isWearingBodyArmor()) {
                //    this.equipBodyArmor(itemStack.copyWithCount(1));
                //    itemStack.decrementUnlessCreative(1, player);
                //    return ActionResult.success(this.getWorld().isClient);
                //}
            }

            return ActionResult.success(self.getWorld().isClient);
        }*/
        System.out.println("TESTING interactCompanionV2 skipping");
        return ActionResult.success(self.getWorld().isClient);
    }

    @Unique
    private StackReference staticGetStackReference(LivingEntity entity, EquipmentSlot slot) {
        return slot != EquipmentSlot.HEAD && slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND
                ? StackReference.of(entity, slot, stack -> stack.isEmpty() || self.getPreferredEquipmentSlot(stack) == slot)
                : StackReference.of(entity, slot);
    }

    @Unique
    @Nullable
    private EquipmentSlot getEquipmentSlot(int slotId) {
        if (slotId == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
            return EquipmentSlot.HEAD;
        } else if (slotId == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
            return EquipmentSlot.CHEST;
        } else if (slotId == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
            return EquipmentSlot.LEGS;
        } else if (slotId == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
            return EquipmentSlot.FEET;
        } else if (slotId == 98) {
            return EquipmentSlot.MAINHAND;
        } else if (slotId == 99) {
            return EquipmentSlot.OFFHAND;
        } else {
            return slotId == 105 ? EquipmentSlot.BODY : null;
        }
    }

    @Unique
    public StackReference entityLivingGetStackReference(int mappedIndex) {
        EquipmentSlot equipmentSlot = getEquipmentSlot(mappedIndex);
        return equipmentSlot != null ? staticGetStackReference(self, equipmentSlot) : StackReference.EMPTY;
    }


    @Unique
    private StackReference getOtherStackReference(int mappedIndex) {
        int i = mappedIndex - 400;
        if (i == 0) {
            return new StackReference() {
                @Override
                public ItemStack get() {
                    return WolfEntityMixin.this.items.getStack(0);
                }

                @Override
                public boolean set(ItemStack stack) {
                    if (!stack.isEmpty() && !stack.isOf(Items.SADDLE)) {
                        return false;
                    } else {
                        WolfEntityMixin.this.items.setStack(0, stack);
                        WolfEntityMixin.this.updateSaddledFlag();
                        return true;
                    }
                }
            };
        } else {
            int j = mappedIndex - 500 + 1;
            return j >= 1 && j < this.items.size() ? StackReference.of(this.items, j) : entityLivingGetStackReference(mappedIndex);
        }
    }

    @Override
    public StackReference wolfcompanion_template_1_21_1$getGetStackReference(int mappedIndex) {
        return mappedIndex == 499 ? new StackReference() {
            @Override
            public ItemStack get() {
                System.out.println("wolfcompanion_template_1_21_1$getGetStackReference");
                return WolfEntityMixin.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
            }

            @Override
            public boolean set(ItemStack stack) {
                System.out.println("wolfcompanion_template_1_21_1$getGetStackReference");
                if (stack.isEmpty()) {
                    if (WolfEntityMixin.this.hasChest()) {
                        WolfEntityMixin.this.setHasChest(false);
                        WolfEntityMixin.this.onChestedStatusChanged();
                    }

                    return true;
                } else if (stack.isOf(Items.CHEST)) {
                    if (!WolfEntityMixin.this.hasChest()) {
                        WolfEntityMixin.this.setHasChest(true);
                        WolfEntityMixin.this.onChestedStatusChanged();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } : getOtherStackReference(mappedIndex);
    }

    @Unique
    public final Inventory getInventory() {
        return this.inventory;
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void injectInitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(HORSE_FLAGS, (byte)0);
        builder.add(CHEST, false);
        //builder.add(INVENTORY, inventory);
    }

    /*@Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HORSE_FLAGS, (byte)0);
        builder.add(CHEST, false);
    }*/

    @Unique
    private DataTracker getDataTracker(WolfEntity wolf) {
        //System.out.println("///////EEEEEEEEE////////////EEEEEEEEEEEEE//////////EEEEEEEEEE");
        return self.getDataTracker();
    }

    @Unique
    public boolean hasChest() {
        return getDataTracker(self).get(CHEST);
    }

    @Unique
    public void setHasChest(boolean hasChest) {
        //((ServerPlayerEntity)self).openHorseInventory();
        getDataTracker(self).set(CHEST, hasChest);
    }
    /*
    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.items != null) {
            for (int i = 0; i < this.items.size(); i++) {
                ItemStack itemStack = this.items.getStack(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)) {
                    self.dropStack(itemStack);
                }
            }
        }

        if (this.hasChest()) {
            if (!self.getWorld().isClient) {
                self.dropItem(Blocks.CHEST);
            }

            this.setHasChest(false);
        }
    }*/

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void injectWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }

        if (!this.items.getStack(0).isEmpty()) {
            nbt.put("SaddleItem", this.items.getStack(0).encode(self.getRegistryManager()));
        }

        nbt.putBoolean("ChestedHorse", this.hasChest());
        if (this.hasChest()) {
            NbtList nbtList = new NbtList();

            for (int i = 1; i < this.items.size(); i++) {
                ItemStack itemStack = this.items.getStack(i);
                System.out.println("writing ItemStack: " + itemStack.toHoverableText().getString());
                if (!itemStack.isEmpty()) {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte)(i - 1));
                    nbtList.add(itemStack.encode(self.getRegistryManager(), nbtCompound));
                }
            }

            nbt.put("Items", nbtList);
            System.out.println("write nbt HAS CHEST");
        } else {
            System.out.println("write nbt HAS NO CHEST");
        }
    }

    /*
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }

        if (!this.items.getStack(0).isEmpty()) {
            nbt.put("SaddleItem", this.items.getStack(0).encode(self.getRegistryManager()));
        }

        nbt.putBoolean("ChestedHorse", this.hasChest());
        if (this.hasChest()) {
            NbtList nbtList = new NbtList();

            for (int i = 1; i < this.items.size(); i++) {
                ItemStack itemStack = this.items.getStack(i);
                if (!itemStack.isEmpty()) {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte)(i - 1));
                    nbtList.add(itemStack.encode(self.getRegistryManager(), nbtCompound));
                }
            }

            nbt.put("Items", nbtList);
        }
    }*/

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        UUID uUID;
        if (nbt.containsUuid("Owner")) {
            uUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            uUID = ServerConfigHandler.getPlayerUuidByName(self.getServer(), string);
        }

        if (uUID != null) {
            self.setOwnerUuid(uUID);
        }

        if (nbt.contains("SaddleItem", NbtElement.COMPOUND_TYPE)) {
            ItemStack itemStack = (ItemStack)ItemStack.fromNbt(self.getRegistryManager(), nbt.getCompound("SaddleItem")).orElse(ItemStack.EMPTY);
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
                    final ItemStack itemStack = ItemStack.fromNbt(self.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY);
                    System.out.println("reading ItemStack: " + itemStack.toHoverableText().getString());
                    this.items.setStack(j + 1, itemStack);
                }
            }
            System.out.println("read nbt HAS CHEST");
        } else {
            System.out.println("read nbt HAS NO CHEST");
        }

        this.updateSaddledFlag();
    }

    /*
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID uUID;
        if (nbt.containsUuid("Owner")) {
            uUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            uUID = ServerConfigHandler.getPlayerUuidByName(self.getServer(), string);
        }

        if (uUID != null) {
            self.setOwnerUuid(uUID);
        }

        if (nbt.contains("SaddleItem", NbtElement.COMPOUND_TYPE)) {
            ItemStack itemStack = (ItemStack)ItemStack.fromNbt(self.getRegistryManager(), nbt.getCompound("SaddleItem")).orElse(ItemStack.EMPTY);
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
                    this.items.setStack(j + 1, (ItemStack)ItemStack.fromNbt(self.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY));
                }
            }
        }

        this.updateSaddledFlag();
    }*/

    @Unique
    public ActionResult interactCompanionV2(PlayerEntity player, Hand hand) {
        boolean bl = !self.isBaby() && player.shouldCancelInteraction();
        System.out.println("TESTING interactCompanionV2");
        if (!self.hasPassengers() && !bl) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (!itemStack.isEmpty()) {
                if (!this.hasChest() && itemStack.isOf(Items.CHEST)) {
                    this.addChest(player, itemStack);
                    return ActionResult.success(self.getWorld().isClient);
                }
            }
            System.out.println("TESTING interactCompanionV2 chest + next interact");
            return interactCompanion(player, hand);
        } else {
            System.out.println("TESTING interactCompanionV2 interact");
            return interactCompanion(player, hand);
        }
    }

    @Unique
    private void addChest(PlayerEntity player, ItemStack chest) {
        this.setHasChest(true);
        this.playAddChestSound();
        chest.decrementUnlessCreative(1, player);
        this.onChestedStatusChanged();
    }

    @Unique
    protected void playAddChestSound() {
        self.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F + 1.0F);
    }

    @Unique
    public int getInventoryColumns() {
        return 5;
    }

    /*@Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(CallbackInfo ci) {
    }*/

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onRightClick(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!player.getWorld().isClient() && hand == Hand.MAIN_HAND) {
            System.out.println("TESTING interactMob on right click");
            final ActionResult result = interactCompanionV2(player, hand);
            cir.setReturnValue(result);
            /*player.openHandledScreen(new WolfInventoryEntity(player, this));
            cir.setReturnValue(ActionResult.SUCCESS);*/
        }
    }

    /*@Override
    public SimpleInventory getInventory() {
        return inventory;
    }*/
}

