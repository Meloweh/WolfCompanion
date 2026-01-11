package github.meloweh.wolfcompanion.mixin;

import com.mojang.serialization.DynamicOps;
import github.meloweh.wolfcompanion.accessor.*;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.goals.*;
import github.meloweh.wolfcompanion.init.InitItem;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.screenhandler.WolfInventoryScreenHandler;
import github.meloweh.wolfcompanion.util.ConfigManager;
import github.meloweh.wolfcompanion.util.LineScan;
import github.meloweh.wolfcompanion.util.NBTHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.particle.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRules;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin implements
        InventoryChangedListener,
        RideableInventory,
        Tameable,
        WolfEntityProvider,
        EntityAccessor,
        MobEntityAccessor,
        ExtendedScreenHandlerFactory<UuidPayload>,
        WolfXpProvider,
        WolfEntityMixinProvider {
    @Unique
    protected SimpleInventory items;
    @Unique
    private WolfEntity self;
    @Unique
    private Optional<ItemEntity> targetPickup = Optional.empty();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        this.self = (WolfEntity) (Object) this;
        this.onChestedStatusChanged();
    }

    @Override
    public void setTargetPickup__(final ItemEntity entity) {
        if (entity == null) {
            this.targetPickup = Optional.empty();
        } else {
            this.targetPickup = Optional.of(entity);
        }
    }

    @Override
    public Optional<ItemEntity> getTargetPickup__() {
        return this.targetPickup;
    }

    @ModifyArg(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 5), index = 1)
    private Goal f(Goal goal) {
        if (this.self == null) {
            self = (WolfEntity) (Object) this;
        }
        return new WolfMeleeAttackGoal(this.self, 1.5, true);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void onInitGoals(CallbackInfo info) {
        if (this.self == null) {
            self = (WolfEntity) (Object) this;
        }
        ((MobEntityAccessor) self).getGoalSelector().add(1, new RescueOwnerFromLavaGoal(self, 1.75f, 2.5f, 7f));
        ((MobEntityAccessor) self).getGoalSelector().add(1, new RescueSelfFromLavaGoal(self));
        ((MobEntityAccessor) self).getGoalSelector().add(2, new EatFoodGoal(self));
        ((MobEntityAccessor) self).getGoalSelector().add(9, new PickUpFoodGoal(self));
    }

    @Shadow
    private boolean furWet;
    @Shadow
    private boolean canShakeWaterOff;
    @Shadow
    private float shakeProgress;

    @Unique
    private void doWolfShake() {
        this.furWet = true;
        if (!self.getEntityWorld().isClient()) {
            //this.furWet = true;
            //this.self.getEntityWorld().sendEntityStatus(this.self, (byte)56);
        }
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
                for (; nbt.contains(WolfEventHandler.RESCUED_WOLF_NBT_KEY + i); i++);
                nbt.put(WolfEventHandler.RESCUED_WOLF_NBT_KEY + i, wolfNbt);

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

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private ParticleEffect changeType(ParticleEffect parameters) {
        final byte shakeReason = getShakeReason();

        //System.out.println(shakeReason + " " + self.getEntityWorld().isClient);

        return switch (shakeReason) {
            case 1 -> TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.529f, 0.639f, 0.388f);
            case 2 -> ParticleTypes.SMOKE;
            default -> ParticleTypes.SPLASH;
        };
    }

    @Unique
    public boolean isPoisoned(WolfEntity wolf) {
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> effects = wolf.getActiveStatusEffects();
        return effects.keySet().stream().anyMatch(effect ->
                effect == StatusEffects.POISON
        );  // No negative effects found
    }

    @Shadow
    private float lastShakeProgress; //this.lastShakeProgress >= 2.0F

    @Inject(method = "tick", at = @At("TAIL"))
    private void shakeConditions(CallbackInfo ci) {
        if (self.isAlive() && !self.getEntityWorld().isClient()) {
             byte shakeReason = 0;
             if (!furWet && getShakeReason() == 0) {
                 if (ConfigManager.config.canShakeOffPoison && isPoisoned(this.self))
                     shakeReason = 1;

                 if (ConfigManager.config.canShakeOffFire && self.isOnFire() && !self.isInLava() && self.isOnGround()) {
                     shakeReason = 2;
                 }

                 if (getShakeReason() > 0)
                     setShakeReason((byte)0);

                 if (shakeReason > 0) {
                     doWolfShake();
                     setShakeReason(shakeReason);
                 }
             }

             if (getShakeReason() > 0){
                 if (lastShakeProgress >= 1.8f) {
                     setShakeReason((byte) 0);
                     if (isPoisoned(self)) {
                         self.removeStatusEffect(StatusEffects.POISON);
                     }
                     else if (self.isOnFire()) {
                         self.setFireTicks(0);
                     }
                 }
             }
        }

        if (this.self.getTarget() != null &&
                this.self.getAttacker() == null &&
                !this.self.isOnGround() &&
                !this.self.isAttacking()) {
            if (LineScan.hasFloorLava(this.self)) {
                this.self.setTarget((LivingEntity) null);
                this.self.setVelocity(this.self.getVelocity().multiply(-1, this.self.getVelocity().getY(), -1));
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void cancelDeath(DamageSource damageSource, CallbackInfo ci) {
        if (this.self.isTamed() && !this.self.getEntityWorld().isClient() && ConfigManager.config.canRespawn) {
            NbtWriteView writeView = NbtWriteView.create(ErrorReporter.EMPTY);
            this.self.writeData(writeView);
            final NbtCompound wolfNbt = writeView.getNbt();
            ReadView nbtReadView = NbtReadView.create(ErrorReporter.EMPTY, this.self.getRegistryManager(), wolfNbt);

            wolfcompanion_template_1_21_1$dropInventoryByButton();

            if (this.self.getOwner() != null) {
                final ServerPlayerAccessor playerAccessor = (ServerPlayerAccessor) (this.self.getOwner());
                playerAccessor.queueRescuedWolfNbt__(wolfNbt);
            } else {
                if (wolfNbt.contains("Owner")) {
                    LazyEntityReference<LivingEntity> lazyEntityReference = LazyEntityReference.fromDataOrPlayerName(nbtReadView, "Owner", this.getEntityWorld());

                    if (lazyEntityReference == null) {
                        System.out.println("ERROR: Could not retrieve lazyEntityReference for wolf.");
                    } else {
                        final UUID ownerUUID = lazyEntityReference.getUuid();
                        //final UUID ownerUUID = UUID.fromString(wolfNbt.getString("Owner").get());

                        if (WolfEventHandler.getMinecraftServer() == null) {
                            System.out.println("ERROR: Server for cancelling wolf deletion not found");
                        } else {
                            final File worldDirectory = WolfEventHandler.getMinecraftServer().getSavePath(WorldSavePath.ROOT).toFile();
                            final File playerDatFolder = new File(worldDirectory, "playerdata");
                            writeToPlayerSaveFile(playerDatFolder, ownerUUID, wolfNbt);
                        }
                    }
                } else {
                    System.out.println("What happened? A wolves owner got obscured. Good bye, you have been a good companion :(");
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
    @Unique
    private static final TrackedData<Boolean> DROP_CHEST = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Boolean> RELEASE_WOLF = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Byte> SHAKE_REASON = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BYTE);
    @Unique
    private static final TrackedData<Integer> XP = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);

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
        self.getEntityWorld().sendEntityStatus(self, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
        if (status == EntityStatuses.CREATE_EATING_PARTICLES) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                for (int i = 0; i < 8; i++) {
                    Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
                            .rotateX(-this.getPitch() * (float) (Math.PI / 180.0))
                            .rotateY(-this.getYaw() * (float) (Math.PI / 180.0));
                    this.getEntityWorld()
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
        if (this.isDirty() && !self.getEntityWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) self.getEntityWorld();
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
        if (!self.getEntityWorld().isClient()) {
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
        builder.add(RELEASE_WOLF, false);
        builder.add(SHAKE_REASON, (byte)0);
        builder.add(XP, 0);
    }

    @Unique
    private float getKnockbackAgainst(Entity target, DamageSource damageSource) {
        float f = (float)this.self.getAttributeValue(EntityAttributes.ATTACK_KNOCKBACK);
        World var5 = this.self.getEntityWorld();
        if (var5 instanceof ServerWorld serverWorld) {
            return EnchantmentHelper.modifyKnockback(serverWorld, this.self.getWeaponStack(), target, damageSource, f);
        } else {
            return f;
        }
    }

    @Override
    public boolean tryAttack__(ServerWorld world, Entity target) {
        float f = (float)this.self.getAttributeValue(EntityAttributes.ATTACK_DAMAGE) + getLevel() * 0.5f;
        ItemStack itemStack = this.self.getWeaponStack();
        DamageSource damageSource = (DamageSource)Optional.ofNullable(itemStack.getItem().getDamageSource(this.self)).orElse(this.self.getDamageSources().mobAttack(this.self));
        f = EnchantmentHelper.getDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
        boolean bl = target.damage(world, damageSource, f);
        if (bl) {
            float g = getKnockbackAgainst(target, damageSource);
            LivingEntity livingEntity;
            if (g > 0.0F && target instanceof LivingEntity) {
                livingEntity = (LivingEntity)target;
                livingEntity.takeKnockback(g * 0.5F, MathHelper.sin(this.self.getYaw() * 0.017453292F), -MathHelper.cos(this.self.getYaw() * 0.017453292F));
                this.self.setVelocity(this.self.getVelocity().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity) {
                livingEntity = (LivingEntity)target;
                itemStack.postHit(livingEntity, this.self);
            }

            EnchantmentHelper.onTargetDamaged(world, target, damageSource);
            this.self.onAttacking(target);
            //this.self.playAttackSound();
        }

        return bl;
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
    public boolean shouldReleaseWolf() {
        return getDataTracker(self).get(RELEASE_WOLF);

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
    public void setShouldReleaseWolf(final boolean yes) {
        getDataTracker(self).set(RELEASE_WOLF, yes);
    }

    @Unique
    public void setShakeReason(byte value) {
        getDataTracker(self).set(SHAKE_REASON, value);
    }

    @Unique
    public byte getShakeReason() {
        return getDataTracker(self).get(SHAKE_REASON);
    }

    @Override
    public void setXp(int value) {
        getDataTracker(self).set(XP, value);
    }

    @Override
    public int getXp() {
        return getDataTracker(self).get(XP);
    }

    @Override
    public int getLevel() {
        final int xp = getXp();
        final float level = 0.5f * (Math.sqrt(2 * xp + 1) - 1);
        return (int) level;
    }

    @Override
    public int getNextLevelXpRequirement(final int level) {
        //final int prev = Math.max(0, level - 1);
        //final int prevLevel = 2 * prev * prev + 2 * prev;
        return 2 * level * level + 2 * level;// - prevLevel;
    }

    @Override
    public int getDeltaXp() {
        final int xp = getXp();
        final int level = getLevel();
        final int requiredXp = getNextLevelXpRequirement(level + 1);
        return requiredXp - xp;
    }

    @Unique
    public void addXp(int value) {
        getDataTracker(self).set(XP, getXp() + value);
    }

    @Override
    public int repairGear(final int amount) {
        Optional<EnchantmentEffectContext> optional = EnchantmentHelper.chooseEquipmentWith(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, this.self, ItemStack::isDamaged);
        if (optional.isPresent()) {
            ItemStack itemStack = optional.get().stack();
            int i = EnchantmentHelper.getRepairWithExperience((ServerWorld) this.self.getEntityWorld(), itemStack, amount);
            int j = java.lang.Math.min(i, itemStack.getDamage());
            itemStack.setDamage(itemStack.getDamage() - j);
            if (j > 0) {
                int k = amount - j * amount / i;
                if (k > 0) {
                    return this.repairGear(k);
                }
            }

            return 0;
        } else {
            return amount;
        }
    }

    @Override
    public void releaseWolfButton() {
        setShouldDropChest(true);
        wolfcompanion_template_1_21_1$dropInventoryByButton();
        if (this.self.getOwner() != null) {
            this.self.setTamed(false, true);
            this.self.setOwner((LivingEntity) null);
            //final NbtCompound wolfNbt = new NbtCompound();
            //this.self.writeCustomDataToNbt(wolfNbt);
            //this.self.getUuid()
            //System.out.println(wolfNbt);

            //final ServerPlayerAccessor playerAccessor = (ServerPlayerAccessor) (this.self.getOwner());
            //playerAccessor.queueWolfNbt(wolfNbt);
        }
    }

    @Override
    public void wolfcompanion_template_1_21_1$dropInventoryByButton() {

        if (shouldDropChest() || !ConfigManager.config.keepWolfInventory) {
            if (this.items != null) {
                for (int i = this.items.size(); i >= 0; i--) {
                    final ItemStack itemStack = this.items.getStack(i);
                    if (!itemStack.isEmpty()) {
                        if (self.getEquippedStack(EquipmentSlot.BODY) != itemStack) {
                            this.items.removeStack(i);
                            self.dropStack((ServerWorld) self.getEntityWorld(), itemStack);
                        }
                    }
                }
            }

            if (this.hasChest()) {

                if (shouldDropChest() || !ConfigManager.config.keepWolfBag) {
                    if (!self.getEntityWorld().isClient()) {
                        self.dropItem((ServerWorld) self.getEntityWorld(), InitItem.ITEM_WOLF_BAG);
                    }
                    this.setHasChest(false);
                }
            }
            setShouldDropChest(false);
        }

    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void cancelPlayerDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity && this.self.isTamed()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void injectWriteCustomDataToNbt(WriteView view, CallbackInfo ci) {
        view.putBoolean("ChestedWolf", this.hasChest());
        if (this.hasChest() && this.self.getEntityWorld() instanceof ServerWorld serverWorld) {
            DynamicOps<NbtElement> ops =
                    serverWorld.getRegistryManager().getOps(NbtOps.INSTANCE); // registry-aware ops

            WriteView.ListView list = view.getList("WolfBagItems");

            for (int slot = 0; slot < this.items.size(); slot++) {
                ItemStack stack = this.items.getStack(slot);
                if (stack.isEmpty()) continue;

                NbtElement encoded = ItemStack.CODEC.encodeStart(ops, stack)
                        .getOrThrow(msg -> new IllegalStateException("Failed to encode ItemStack: " + msg));

                WriteView entry = list.add();
                entry.putInt("Slot", slot);
                entry.putString("Stack", encoded.toString()); // SNBT
            }
        }
        view.putNullable("CustomName", TextCodecs.CODEC, this.self.getCustomName());
        if (this.self.isCustomNameVisible()) {
            view.putBoolean("CustomNameVisible", this.self.isCustomNameVisible());
        }
        view.putInt("XP", this.getXp());
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void readCustomDataFromNbt(ReadView view, CallbackInfo ci) {
        this.setHasChest(view.getBoolean("ChestedWolf", false));
        this.onChestedStatusChanged();
        if (this.hasChest() && this.getEntityWorld() instanceof ServerWorld serverWorld) {
            DynamicOps<NbtElement> ops =
                    serverWorld.getRegistryManager().getOps(NbtOps.INSTANCE);

            for (ReadView entry : view.getListReadView("WolfBagItems")) {
                int slot = entry.getInt("Slot", -1);
                if (slot < 0 || slot >= this.items.size()) continue;

                String snbt = entry.getString("Stack", "");
                if (snbt.isEmpty()) continue;

                try {
                    NbtElement parsed = StringNbtReader.readCompound(snbt);

                    ItemStack stack = ItemStack.CODEC.parse(ops, parsed)
                            .getOrThrow(msg -> new IllegalStateException("Failed to decode ItemStack: " + msg));

                    if (slot == 0) {
                        this.self.equipBodyArmor(stack);
                    } else {
                        this.items.setStack(slot, stack);
                    }
                } catch (Exception ignored) {
                    // optionally log
                }
            }
        }
        this.self.setCustomName(view.read("CustomName", TextCodecs.CODEC).orElse(null));
        this.self.setCustomNameVisible(view.getBoolean("CustomNameVisible", false));

        this.setXp(view.getInt("XP", 0));
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

    @Unique
    private SimpleInventory getReducedInventory() {
        final SimpleInventory inv = new SimpleInventory(15);
        for (int i = 1; i < this.items.size(); i++) {
            inv.setStack(i - 1, this.items.getStack(i));
        }
        return inv;
    }

    @Unique
    private void transferReducedInventory(final SimpleInventory inv) {
        for (int i = 1; i < this.items.size(); i++) {
            this.items.setStack(i, inv.getStack(i - 1));
        }
    }

    @Unique
    private void dropItem(ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(this.self.getEntityWorld(), this.self.getX(), this.self.getY(), this.self.getZ(), stack);
        this.self.getEntityWorld().spawnEntity(itemEntity);
    }

    @Override
    public void spit__(ItemStack stack) {
        if (!stack.isEmpty() && !this.self.getEntityWorld().isClient()) {
            ItemEntity itemEntity = new ItemEntity(
                    this.self.getEntityWorld(), this.self.getX() + this.self.getRotationVector().x, this.self.getY() + 1.0, this.self.getZ() + this.self.getRotationVector().z, stack
            );
            itemEntity.setPickupDelay(40);
            itemEntity.setThrower(this.self);
            this.self.playSound(SoundEvents.ENTITY_FOX_SPIT, 1.0F, 1.0F);
            this.self.getEntityWorld().spawnEntity(itemEntity);
        }
    }

    @Unique
    private void loot__(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (!itemStack.isEmpty()) {
            if (this.hasChestEquipped()) {
                if (this.items.canInsert(itemStack)) {
                    this.self.triggerItemPickedUpByEntityCriteria(item);

                    final SimpleInventory reducedInventory = getReducedInventory();
                    final ItemStack itemStack2 = reducedInventory.addStack(itemStack); //this.items.addStack(itemStack);
                    transferReducedInventory(reducedInventory);

                    final int transfered = itemStack.getCount() - itemStack2.getCount();

                    this.self.sendPickup(item, transfered);
                    itemStack.decrement(transfered);

                    if (itemStack.isEmpty()) {
                        item.discard();
                    }

                    this.items.removeListener(this);
                    this.items.addListener(this);
                    this.items.markDirty();
                }
            } else {
                int i = itemStack.getCount();
                if (i > 1) {
                    this.dropItem(itemStack.split(i - 1));
                }

                this.spit__(this.self.getEquippedStack(EquipmentSlot.MAINHAND));
                this.self.triggerItemPickedUpByEntityCriteria(item);
                this.self.equipStack(EquipmentSlot.MAINHAND, itemStack.split(1));
                this.self.sendPickup(item, itemStack.getCount());
                item.discard();
            }
        }
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void onTickMovement(CallbackInfo ci) {
        if(this.targetPickup.isPresent()) {
            if (!this.self.getEntityWorld().isClient()
                    && this.self.isAlive()
                    && !this.self.isDead()
                    && ((ServerWorld)this.self.getEntityWorld()).getGameRules().getValue(GameRules.DO_MOB_GRIEFING)) {
                final Profiler profiler = Profilers.get();
                profiler.push("looting");

                Vec3i vec3i = ((MobEntityAccessor) this.self).getItemPickUpRangeExpander__();
                List<ItemEntity> list = this.getEntityWorld().getNonSpectatingEntities(ItemEntity.class, this.self.getBoundingBox()
                        .expand(vec3i.getX(), vec3i.getY(), vec3i.getZ()))
                        .stream().filter(e -> e == this.targetPickup.get()).toList();

                for (ItemEntity itemEntity : list) {
                    if (!itemEntity.isRemoved() && !itemEntity.getStack().isEmpty() && !itemEntity.cannotPickup() && this.self.canGather(((ServerWorld)this.self.getEntityWorld()), itemEntity.getStack())) {
                        this.loot__(itemEntity);
                    }
                }

                profiler.pop();
            }
        }
    }

    @Unique
    private Vec2f vecFromYaw(final float yaw) {
        final float rad = Math.toRadians(yaw);
        return new Vec2f(-MathHelper.sin(rad), MathHelper.cos(rad));
    }

    @Inject(method = "handleStatus", at = @At("HEAD"), cancellable = true)
    private void onHandleStatus(byte status, CallbackInfo ci) {
        if (status == EntityStatuses.CREATE_EATING_PARTICLES) {
            ItemStack itemStack = this.self.getEquippedStack(EquipmentSlot.MAINHAND);
            final Vec2f vec = vecFromYaw(self.bodyYaw).normalize();
            if (!itemStack.isEmpty()) {
                for (int i = 0; i < 8; i++) {
                    Vec3d vec3d = new Vec3d(((double)this.self.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
                            .rotateX(-this.self.getPitch() * (float) (Math.PI / 180.0))
                            .rotateY(-this.self.getYaw() * (float) (Math.PI / 180.0));
                    this.self.getEntityWorld().addParticleClient(
                                    new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack),
                            this.self.getX() + vec.x * 0.6,
                            this.self.getY() + 0.6,
                            this.self.getZ() + vec.y * 0.6,
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
        if (!player.getEntityWorld().isClient() &&
                hand == Hand.MAIN_HAND &&
                self.isTamed() &&
                self.isOwner(player) &&
                !self.isBaby()
        ) {
            final ItemStack itemStack = player.getStackInHand(hand);
            //System.out.println(itemStack.isOf(InitItem.ITEM_WOLF_BAG));
            if (!this.hasChest() && itemStack.isOf(InitItem.ITEM_WOLF_BAG)) {
                this.addChest(player, itemStack);
                final ActionResult result = self.getEntityWorld().isClient() ? ActionResult.SUCCESS : ActionResult.SUCCESS_SERVER;
                cir.setReturnValue(result);
                cir.cancel();
            } else if (player.isSneaking()) {
                this.openInventory(player);
                final ActionResult result = self.getEntityWorld().isClient() ? ActionResult.SUCCESS : ActionResult.SUCCESS_SERVER;
                cir.setReturnValue(result);
                cir.cancel();
            }
        }
    }
}

