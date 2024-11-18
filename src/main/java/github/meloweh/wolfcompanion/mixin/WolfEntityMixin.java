package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.accessor.*;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.goals.EatFoodGoal;
import github.meloweh.wolfcompanion.init.InitItem;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.screenhandler.WolfInventoryScreenHandler;
import github.meloweh.wolfcompanion.util.NBTHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    protected SimpleInventory items;
    @Unique
    private WolfEntity self;

//    @Unique
//    private static final int EATING_DURATION = 600;
//    @Unique
//    private float headRollProgress;
//    @Unique
//    private float lastHeadRollProgress;
//    @Unique
//    float extraRollingHeight;
//    @Unique
//    float lastExtraRollingHeight;
//    @Unique
//    private int eatingTime;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        //items.addListener(this);
        //items.markDirty();
        self = (WolfEntity) (Object) this;
        this.onChestedStatusChanged();
    }

//    @Unique
//    public SoundEvent getEatSound(ItemStack stack) {
//        return SoundEvents.ENTITY_FOX_EAT;
//    }


    @Inject(method = "initGoals", at = @At("TAIL"))
    private void onInitGoals(CallbackInfo info) {
        System.out.println("Is self present?: " + this.self);
        if (this.self == null) {
            self = (WolfEntity) (Object) this;
        }
        ((MobEntityAccessor) self).getGoalSelector().add(1, new EatFoodGoal(self));
    }

    @Unique
    public void writeToPlayerSaveFile(File playerDataFolder, UUID playerUUID, final NbtCompound wolfNbt) {
        File playerFile = new File(playerDataFolder, playerUUID.toString() + ".dat");
        if (playerFile.exists()) {
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                // Open file input stream
                fileInputStream = new FileInputStream(playerFile);

                // Read the existing NBT data
                final NbtCompound nbt = NbtIo.readCompressed(fileInputStream, NbtSizeTracker.ofUnlimitedBytes());

                int i = 0;
                for (; nbt.contains(WolfEventHandler.Wolf_NBT_KEY + i); i++);
                nbt.put(WolfEventHandler.Wolf_NBT_KEY + i, wolfNbt);

                // Open file output stream and write the modified data back
                fileOutputStream = new FileOutputStream(playerFile);
                NbtIo.writeCompressed(nbt, fileOutputStream);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Close streams to prevent memory leaks
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("File does not exist");
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void cancelDeath(DamageSource damageSource, CallbackInfo ci) {
        if (this.self.isTamed() && !this.self.getWorld().isClient) {
            final NbtCompound wolfNbt = new NbtCompound();
            this.self.writeCustomDataToNbt(wolfNbt);

            if (this.self.getOwner() != null) {
                dropEverything();

                final ServerPlayerAccessor playerAccessor = (ServerPlayerAccessor) (this.self.getOwner());

                playerAccessor.queueWolfNbt(wolfNbt);
            } else {
                if (wolfNbt.contains("Owner")) {
                    final UUID ownerUUID = wolfNbt.getUuid("Owner");
                    final File worldDirectory = self.getServer().getSavePath(WorldSavePath.ROOT).toFile();
                    final File playerDatFolder = new File(worldDirectory, "playerdata");

                    writeToPlayerSaveFile(playerDatFolder, ownerUUID, wolfNbt);
                }
            }

        }
    }

    @Override
    public SimpleInventory wolfcompanion_template_1_21_1$getItemsInventory() {
        return this.items;
    }

    @Unique
    private static final TrackedData<Boolean> CHEST = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> DROP_CHEST = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

//    private boolean isDirty = false;
//
//    private void setDirty(boolean dirty) {
//        this.isDirty = dirty;
//    }
//
//    public boolean isDirty() {
//        return this.isDirty;
//    }

    /*
    @Override
    public void handleStatus(byte status) {
        self.getWorld().sendEntityStatus(self, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
        if (status == EntityStatuses.CREATE_EATING_PARTICLES) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                for (int i = 0; i < 8; i++) {
                    Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
                            .rotateX(-this.getPitch() * (float) (Math.PI / 180.0))
                            .rotateY(-this.getYaw() * (float) (Math.PI / 180.0));
                    this.getWorld()
                            .addParticle(
                                    new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack),
                                    this.getX() + this.getRotationVector().x / 2.0,
                                    this.getY(),
                                    this.getZ() + this.getRotationVector().z / 2.0,
                                    vec3d.x,
                                    vec3d.y + 0.05,
                                    vec3d.z
                            );
                }
            }
        } else {
            super.handleStatus(status);
        }
    }*/

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
        this.items.markDirty();
    }

    @Override
    public SimpleInventory getInventory() {
        return this.items;
    }

    @Override
    public void onInventoryChanged(Inventory sender) {

    }

    @Unique
    public WolfEntityMixin getThis() {
        return this;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new WolfInventoryScreenHandler(syncId, playerInventory, self, NBTHelper.getWolfNBT(self));
    }

    @Override
    public UuidPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new UuidPayload(self.getUuid(), NBTHelper.getWolfNBT(self));
    }



    @Unique
    public void openWolfInventory(final ServerPlayerEntity player, WolfEntityMixin wolfEntityMixin, Inventory inventory) {
        player.openHandledScreen(this);
    }

//    @Unique
//    public boolean areInventoriesDifferent(Inventory inventory) {
//        return this.items != inventory;
//    }

//    @Unique
//    private int getId() {
//        return self.getId();
//    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!self.getWorld().isClient) {
            openWolfInventory((ServerPlayerEntity) player, this, player.getInventory());
        }
    }

//    @Unique
//    private StackReference staticGetStackReference(LivingEntity entity, EquipmentSlot slot) {
//        return slot != EquipmentSlot.HEAD && slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND
//                ? StackReference.of(entity, slot, stack -> stack.isEmpty() || self.getPreferredEquipmentSlot(stack) == slot)
//                : StackReference.of(entity, slot);
//    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void injectInitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(CHEST, false);
        builder.add(DROP_CHEST, false);
    }

    @Unique
    private DataTracker getDataTracker(WolfEntity wolf) {
        return self.getDataTracker();
    }

    @Unique
    public boolean hasChest() {
        return getDataTracker(self).get(CHEST);
    }

    @Override
    public boolean shouldDropChest() {
        return getDataTracker(self).get(DROP_CHEST);
    }

    @Override
    public boolean hasChestEquipped() {
        return hasChest();
    }

    @Unique
    public void setHasChest(boolean hasChest) {
        getDataTracker(self).set(CHEST, hasChest);
    }

    @Override
    public void setShouldDropChest(final boolean yes) {
        getDataTracker(self).set(DROP_CHEST, yes);
    }

    @Override
    public void dropInventory() {
        if (this.items != null) {
            for (int i = this.items.size(); i >= 0; i--) {
                final ItemStack itemStack = this.items.getStack(i);
                if (!itemStack.isEmpty()) {
                    if (self.getEquippedStack(EquipmentSlot.BODY) != itemStack) {
                        this.items.removeStack(i);
                        self.dropStack(itemStack);
                    }
                }
            }
        }

        if (this.hasChest()) {
            if (!self.getWorld().isClient) {
                self.dropItem(InitItem.ITEM_WOLF_BAG);
            }

            this.setHasChest(false);
        }
        setShouldDropChest(false);
    }

    @Unique
    private void dropEverything() {
        if (this.items != null) {
            this.items.clearToList().forEach(itemStack -> {
                if (!itemStack.isEmpty()) {
                    if (self.getEquippedStack(EquipmentSlot.BODY) != itemStack) {
                        self.dropStack(itemStack);
                    }
                }
            });
        }

        if (this.hasChest()) {
            if (!self.getWorld().isClient) {
                self.dropItem(InitItem.ITEM_WOLF_BAG);
            }

            this.setHasChest(false);
        }
        setShouldDropChest(false);
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void cancelPlayerDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void injectWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("ChestedWolf", this.hasChest());
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
        }
        Text text = this.self.getCustomName();
        if (text != null) {
            nbt.putString("CustomName", Text.Serialization.toJsonString(text, this.self.getRegistryManager()));
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        /*if (nbt.contains("SaddleItem", NbtElement.COMPOUND_TYPE)) {
            ItemStack itemStack = (ItemStack)ItemStack.fromNbt(self.getRegistryManager(), nbt.getCompound("SaddleItem")).orElse(ItemStack.EMPTY);
            if (itemStack.isOf(Items.SADDLE)) {
                this.items.setStack(0, itemStack);
            }
        }*/

        ////////
        this.setHasChest(nbt.getBoolean("ChestedWolf"));
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
        }
        if (nbt.contains("CustomName", 8)) {
            String string = nbt.getString("CustomName");

            try {
                this.self.setCustomName(Text.Serialization.fromJson(string, this.self.getRegistryManager()));
            } catch (Exception var16) {
                WolfCompanion.LOGGER.warn("Failed to parse entity custom name {}", string, var16);
            }
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
        this.self.playSound(this.self.getEatSound(Items.PORKCHOP.getDefaultStack()), 1.0F, 1.0F);
        this.self.getWorld().sendEntityStatus(this.self, EntityStatuses.CREATE_EATING_PARTICLES);
    }*/

//    @Unique
//    private boolean canEat(ItemStack stack) {
//        return stack.contains(DataComponentTypes.FOOD) && this.self.getTarget() == null;
//    }

//    @Unique
//    private final List<ItemStack> inventoryContents = new ArrayList<>();
//
//    private void refreshInventoryContents(Inventory invBasic) {
//        this.inventoryContents.clear();
//        for(int slotIndex = 1;
//            slotIndex < 16;
//            ++slotIndex) {
//            this.inventoryContents.add(invBasic.getStack(slotIndex));
//        }
//    }

//    @Inject(method = "tickMovement", at = @At("TAIL"))
//    private void onTickMovement(CallbackInfo ci) {
//        if (!this.self.getWorld().isClient && this.self.isAlive() && this.self.canMoveVoluntarily()) {
//            this.eatingTime++;
//            ItemStack itemStack = this.self.getEquippedStack(EquipmentSlot.MAINHAND);
//            if (canEat(itemStack)) { // TODO: no cookies for doggo
//                if (this.eatingTime > 600) {
//                    ItemStack itemStack2 = itemStack.finishUsing(this.self.getWorld(), this.self);
//                    if (!itemStack2.isEmpty()) {
//                        this.self.equipStack(EquipmentSlot.MAINHAND, itemStack2);
//                    }
//
//                    this.eatingTime = 0;
//                } else if (this.eatingTime > 560 && this.self.getRandom().nextFloat() < 0.1F) {
//                    this.self.playSound(this.getEatSound(itemStack), 1.0F, 1.0F);
//                    this.self.getWorld().sendEntityStatus(this.self, EntityStatuses.CREATE_EATING_PARTICLES);
//                }
//            }
//        }
//    }

    @Inject(method = "handleStatus", at = @At("HEAD"), cancellable = true)
    private void onHandleStatus(byte status, CallbackInfo ci) {
        if (status == EntityStatuses.CREATE_EATING_PARTICLES) {
            ItemStack itemStack = this.self.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                for (int i = 0; i < 8; i++) {
                    Vec3d vec3d = new Vec3d(((double)this.self.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
                            .rotateX(-this.self.getPitch() * (float) (Math.PI / 180.0))
                            .rotateY(-this.self.getYaw() * (float) (Math.PI / 180.0));
                    this.self.getWorld().addParticle(
                                    new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack),
                            this.self.getX() + this.self.getRotationVector().x / 2.0,
                            this.self.getY(),
                            this.self.getZ() + this.self.getRotationVector().z / 2.0,
                                    vec3d.x,
                                    vec3d.y + 0.05,
                                    vec3d.z
                            );
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onRightClick(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!player.getWorld().isClient() &&
                hand == Hand.MAIN_HAND &&
                self.isTamed() &&
                self.isOwner(player) &&
                !self.isBaby()
        ) {
            final ItemStack itemStack = player.getStackInHand(hand);
            if (!this.hasChest() && itemStack.isOf(InitItem.ITEM_WOLF_BAG)) {
                this.addChest(player, itemStack);
                final ActionResult result = ActionResult.success(self.getWorld().isClient);
                cir.setReturnValue(result);
                cir.cancel();
            } else if (player.isSneaking()) {
                this.openInventory(player);
                final ActionResult result =  ActionResult.success(self.getWorld().isClient);
                cir.setReturnValue(result);
                cir.cancel();
            }
        }
    }

    /*@Override
    public SimpleInventory getInventory() {
        return inventory;
    }*/
}

