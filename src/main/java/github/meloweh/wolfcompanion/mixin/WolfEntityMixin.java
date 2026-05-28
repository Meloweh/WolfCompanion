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
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
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

@Mixin(Wolf.class)
public abstract class WolfEntityMixin implements
        HasCustomInventoryScreen,
        OwnableEntity,
        WolfEntityProvider,
        EntityAccessor,
        MobEntityAccessor,
        ExtendedMenuProvider<UuidPayload>,
        WolfXpProvider,
        WolfEntityMixinProvider {
    @Unique
    protected SimpleContainer items;
    @Unique
    private Wolf self;
    @Unique
    private Optional<ItemEntity> targetPickup = Optional.empty();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo info) {
        this.self = (Wolf) (Object) this;
        this.targetPickup = Optional.empty();
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
        if (this.targetPickup == null) {
            this.targetPickup = Optional.empty();
        }
        return this.targetPickup;
    }

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 5), index = 1)
    private Goal f(Goal goal) {
        if (this.self == null) {
            self = (Wolf) (Object) this;
        }
        return new WolfMeleeAttackGoal(this.self, 1.5, true);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void onInitGoals(CallbackInfo info) {
        if (this.self == null) {
            self = (Wolf) (Object) this;
        }
        ((MobEntityAccessor) self).getGoalSelector().addGoal(1, new RescueOwnerFromLavaGoal(self, 1.75f, 2.5f, 7f));
        ((MobEntityAccessor) self).getGoalSelector().addGoal(1, new RescueSelfFromLavaGoal(self));
        ((MobEntityAccessor) self).getGoalSelector().addGoal(2, new EatFoodGoal(self));
        ((MobEntityAccessor) self).getGoalSelector().addGoal(9, new PickUpFoodGoal(self));
    }

    @Shadow
    private boolean isWet;
    @Shadow
    private boolean isShaking;
    @Shadow
    private float shakeAnim;

    @Unique
    private void doWolfShake() {
        this.isWet = true;
        if (!self.level().isClientSide()) {
            //this.furWet = true;
            //this.self.getEntityWorld().sendEntityStatus(this.self, (byte)56);
        }
    }

    @Unique
    public void writeToPlayerSaveFile(File playerDataFolder, UUID playerUUID, final CompoundTag wolfNbt) {
        File playerFile = new File(playerDataFolder, playerUUID.toString() + ".dat");
        if (playerFile.exists()) {
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                // Open file input stream
                fileInputStream = new FileInputStream(playerFile);

                // Read the existing NBT data
                final CompoundTag nbt = NbtIo.readCompressed(fileInputStream, NbtAccounter.unlimitedHeap());

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

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private ParticleOptions changeType(ParticleOptions parameters) {
        final byte shakeReason = getShakeReason();

        return switch (shakeReason) {
            case 1 -> ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.529f, 0.639f, 0.388f);
            case 2 -> ParticleTypes.SMOKE;
            default -> ParticleTypes.SPLASH;
        };
    }

    @Unique
    public boolean isPoisoned(Wolf wolf) {
        Map<Holder<MobEffect>, MobEffectInstance> effects = wolf.getActiveEffectsMap();
        return effects.keySet().stream().anyMatch(effect ->
                effect == MobEffects.POISON
        );  // No negative effects found
    }

    @Shadow
    private float shakeAnimO; //this.lastShakeProgress >= 2.0F
    @Unique
    private int restingTicks = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void shakeConditions(CallbackInfo ci) {
        if (self.isAlive() && !self.level().isClientSide()) {

            if (ConfigManager.config.allowPassiveRegeneration
                    && this.self.isOrderedToSit()
                    && this.self.getHealth() < this.self.getMaxHealth()) {
                restingTicks++;
                if (restingTicks > 20 * ConfigManager.config.passiveRegenerationRate) {
                    restingTicks = 0;
                    this.self.heal(1);
                }
            }

             byte shakeReason = 0;
             if (!isWet && getShakeReason() == 0) {
                 if (ConfigManager.config.canShakeOffPoison && isPoisoned(this.self))
                     shakeReason = 1;

                 if (ConfigManager.config.canShakeOffFire && self.isOnFire() && !self.isInLava() && self.onGround()) {
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
                 if (shakeAnimO >= 1.8f) {
                     setShakeReason((byte) 0);
                     if (isPoisoned(self)) {
                         self.removeEffect(MobEffects.POISON);
                     }
                     else if (self.isOnFire()) {
                         self.setRemainingFireTicks(0);
                     }
                 }
             }
        }

        if (this.self.getTarget() != null &&
                this.self.getLastHurtByMob() == null &&
                !this.self.onGround() &&
                !this.self.isAggressive()) {
            if (LineScan.hasFloorLava(this.self)) {
                this.self.setTarget((LivingEntity) null);
                this.self.setDeltaMovement(this.self.getDeltaMovement().multiply(-1, this.self.getDeltaMovement().y(), -1));
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void cancelDeath(DamageSource damageSource, CallbackInfo ci) {
        if (this.self.isTame() && !this.self.level().isClientSide() && ConfigManager.config.canRespawn) {
            TagValueOutput writeView = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            this.self.saveWithoutId(writeView);
            final CompoundTag wolfNbt = writeView.buildResult();

            wolfNbt.putInt("RescueTimeout", 20 * 60 * 10);

            ValueInput nbtReadView = TagValueInput.create(ProblemReporter.DISCARDING, this.self.registryAccess(), wolfNbt);

            wolfcompanion_template_1_21_1$dropInventoryByButton();

            if (this.self.getOwner() != null) {
                final ServerPlayerAccessor playerAccessor = (ServerPlayerAccessor) (this.self.getOwner());
                playerAccessor.queueRescuedWolfNbt__(wolfNbt);
            } else {
                if (wolfNbt.contains("Owner")) {
                    EntityReference<LivingEntity> lazyEntityReference = EntityReference.readWithOldOwnerConversion(nbtReadView, "Owner", this.level());

                    if (lazyEntityReference == null) {
                        System.out.println("ERROR: Could not retrieve lazyEntityReference for wolf.");
                    } else {
                        final UUID ownerUUID = lazyEntityReference.getUUID();
                        //final UUID ownerUUID = UUID.fromString(wolfNbt.getString("Owner").get());

                        if (WolfEventHandler.getMinecraftServer() == null) {
                            System.out.println("ERROR: Server for cancelling wolf deletion not found");
                        } else {
                            final File worldDirectory = WolfEventHandler.getMinecraftServer().getWorldPath(LevelResource.ROOT).toFile();
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
    public SimpleContainer wolfcompanion_template_1_21_1$getItemsInventory() {
        return this.items;
    }

    @Unique
    private static final EntityDataAccessor<Boolean> CHEST = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Boolean> DROP_CHEST = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Boolean> RELEASE_WOLF = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Byte> SHAKE_REASON = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BYTE);
    @Unique
    private static final EntityDataAccessor<Integer> XP = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Boolean> AGGRESSIVE = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Boolean> LOCK = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);

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
        SimpleContainer simpleInventory = this.items;
        this.items = new SimpleContainer(this.getInventorySize());
        if (simpleInventory != null) {
            int i = Math.min(simpleInventory.getContainerSize(), this.items.getContainerSize());

            for (int j = 0; j < i; j++) {
                ItemStack itemStack = simpleInventory.getItem(j);
                if (!itemStack.isEmpty()) {
                    this.items.setItem(j, itemStack.copy());
                }
            }
        }

        this.items.setChanged();
    }

    @Override
    public SimpleContainer getInventory() {
        return this.items;
    }

    @Unique
    public WolfEntityMixin getThis() {
        return this;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new WolfInventoryScreenHandler(syncId, playerInventory, self, NBTHelper.getWolfNBT(self));
    }

    @Override
    public UuidPayload getScreenOpeningData(ServerPlayer player) {
        return new UuidPayload(self.getUUID(), NBTHelper.getWolfNBT(self));
    }



    @Unique
    public void openWolfInventory(final ServerPlayer player, WolfEntityMixin wolfEntityMixin, Container inventory) {
        player.openMenu(this);
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
    public void openCustomInventoryScreen(Player player) {
        if (!self.level().isClientSide()) {
            openWolfInventory((ServerPlayer) player, this, player.getInventory());
        }
    }

//    @Unique
//    private StackReference staticGetStackReference(LivingEntity entity, EquipmentSlot slot) {
//        return slot != EquipmentSlot.HEAD && slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND
//                ? StackReference.of(entity, slot, stack -> stack.isEmpty() || self.getPreferredEquipmentSlot(stack) == slot)
//                : StackReference.of(entity, slot);
//    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void injectInitDataTracker(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(CHEST, false);
        builder.define(DROP_CHEST, false);
        builder.define(RELEASE_WOLF, false);
        builder.define(SHAKE_REASON, (byte)0);
        builder.define(XP, 0);
        builder.define(AGGRESSIVE, false);
        builder.define(LOCK, false);
    }

    @Unique
    private float getKnockbackAgainst(Entity target, DamageSource damageSource) {
        float f = (float)this.self.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        Level var5 = this.self.level();
        if (var5 instanceof ServerLevel serverWorld) {
            return EnchantmentHelper.modifyKnockback(serverWorld, this.self.getWeaponItem(), target, damageSource, f);
        } else {
            return f;
        }
    }

    @Override
    public boolean tryAttack__(ServerLevel world, Entity target) {
        float f = (float)this.self.getAttributeValue(Attributes.ATTACK_DAMAGE) + getLevel() * 0.5f;
        ItemStack itemStack = this.self.getWeaponItem();
        DamageSource damageSource = (DamageSource)Optional.ofNullable(itemStack.getItem().getItemDamageSource(this.self)).orElse(this.self.damageSources().mobAttack(this.self));
        f = EnchantmentHelper.modifyDamage(world, itemStack, target, damageSource, f);
        f += itemStack.getItem().getAttackDamageBonus(target, f, damageSource);
        boolean bl = target.hurtServer(world, damageSource, f);
        if (bl) {
            float g = getKnockbackAgainst(target, damageSource);
            LivingEntity livingEntity;
            if (g > 0.0F && target instanceof LivingEntity) {
                livingEntity = (LivingEntity)target;
                livingEntity.knockback(g * 0.5F, Mth.sin(this.self.getYRot() * 0.017453292F), -Mth.cos(this.self.getYRot() * 0.017453292F));
                this.self.setDeltaMovement(this.self.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof LivingEntity) {
                livingEntity = (LivingEntity)target;
                itemStack.hurtEnemy(livingEntity, this.self);
            }

            EnchantmentHelper.doPostAttackEffects(world, target, damageSource);
            this.self.setLastHurtMob(target);
            //this.self.playAttackSound();
        }

        return bl;
    }

    @Unique
    private SynchedEntityData getDataTracker(Wolf wolf) {
        return self.getEntityData();
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
    public void setAggressive__(boolean aggressive) {
        getDataTracker(self).set(AGGRESSIVE, aggressive);
    }

    @Override
    public boolean isAggressive__() {
        return getDataTracker(self).get(AGGRESSIVE);
    }

    @Override
    public void setLock__(boolean lock) {
        getDataTracker(self).set(LOCK, lock);
    }

    @Override
    public boolean isLock__() {
        return getDataTracker(self).get(LOCK);
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
        Optional<EnchantedItemInUse> optional = EnchantmentHelper.getRandomItemWith(EnchantmentEffectComponents.REPAIR_WITH_XP, this.self, ItemStack::isDamaged);
        if (optional.isPresent()) {
            ItemStack itemStack = optional.get().itemStack();
            int i = EnchantmentHelper.modifyDurabilityToRepairFromXp((ServerLevel) this.self.level(), itemStack, amount);
            int j = java.lang.Math.min(i, itemStack.getDamageValue());
            itemStack.setDamageValue(itemStack.getDamageValue() - j);
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
            this.self.setTame(false, true);
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
                for (int i = this.items.getContainerSize(); i >= 0; i--) {
                    final ItemStack itemStack = this.items.getItem(i);
                    if (!itemStack.isEmpty()) {
                        if (self.getItemBySlot(EquipmentSlot.BODY) != itemStack) {
                            this.items.removeItemNoUpdate(i);
                            self.spawnAtLocation((ServerLevel) self.level(), itemStack);
                        }
                    }
                }
            }

            if (this.hasChest()) {

                if (shouldDropChest() || !ConfigManager.config.keepWolfBag) {
                    if (!self.level().isClientSide()) {
                        self.spawnAtLocation((ServerLevel) self.level(), InitItem.ITEM_WOLF_BAG);
                    }
                    this.setHasChest(false);
                }
            }
            setShouldDropChest(false);
        }

    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void cancelPlayerDamage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof Player && this.self.isTame()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void injectWriteCustomDataToNbt(ValueOutput view, CallbackInfo ci) {
        view.putBoolean("ChestedWolf", this.hasChest());
        view.putBoolean("wcm_IsAggressive", this.isAggressive__());
        view.putBoolean("wcm_IsLock", this.isLock__());
        if (this.hasChest() && this.self.level() instanceof ServerLevel serverWorld) {
            DynamicOps<Tag> ops =
                    serverWorld.registryAccess().createSerializationContext(NbtOps.INSTANCE); // registry-aware ops

            ValueOutput.ValueOutputList list = view.childrenList("WolfBagItems");

            for (int slot = 0; slot < this.items.getContainerSize(); slot++) {
                ItemStack stack = this.items.getItem(slot);
                if (stack.isEmpty()) continue;

                Tag encoded = ItemStack.CODEC.encodeStart(ops, stack)
                        .getOrThrow(msg -> new IllegalStateException("Failed to encode ItemStack: " + msg));

                ValueOutput entry = list.addChild();
                entry.putInt("Slot", slot);
                entry.putString("Stack", encoded.toString()); // SNBT
            }
        }
        view.storeNullable("CustomName", ComponentSerialization.CODEC, this.self.getCustomName());
        if (this.self.isCustomNameVisible()) {
            view.putBoolean("CustomNameVisible", this.self.isCustomNameVisible());
        }
        view.putInt("XP", this.getXp());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomDataFromNbt(ValueInput view, CallbackInfo ci) {
        this.setHasChest(view.getBooleanOr("ChestedWolf", false));
        this.setAggressive__(view.getBooleanOr("wcm_IsAggressive", false));
        this.setLock__(view.getBooleanOr("wcm_IsLock", false));
        this.onChestedStatusChanged();
        if (this.hasChest() && this.level() instanceof ServerLevel serverWorld) {
            DynamicOps<Tag> ops =
                    serverWorld.registryAccess().createSerializationContext(NbtOps.INSTANCE);

            for (ValueInput entry : view.childrenListOrEmpty("WolfBagItems")) {
                int slot = entry.getIntOr("Slot", -1);
                if (slot < 0 || slot >= this.items.getContainerSize()) continue;

                String snbt = entry.getStringOr("Stack", "");
                if (snbt.isEmpty()) continue;

                try {
                    Tag parsed = TagParser.parseCompoundFully(snbt);

                    ItemStack stack = ItemStack.CODEC.parse(ops, parsed)
                            .getOrThrow(msg -> new IllegalStateException("Failed to decode ItemStack: " + msg));

                    if (slot == 0) {
                        this.self.setBodyArmorItem(stack);
                    } else {
                        this.items.setItem(slot, stack);
                    }
                } catch (Exception ignored) {
                    // optionally log
                }
            }
        }
        this.self.setCustomName(view.read("CustomName", ComponentSerialization.CODEC).orElse(null));
        this.self.setCustomNameVisible(view.getBooleanOr("CustomNameVisible", false));

        this.setXp(view.getIntOr("XP", 0));
    }

    @Unique
    private void addChest(Player player, ItemStack chest) {
        this.setHasChest(true);
        this.playAddChestSound();
        chest.consume(1, player);
        this.onChestedStatusChanged();
    }

    @Unique
    protected void playAddChestSound() {
        self.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F + 1.0F);
    }

    @Unique
    public int getInventoryColumns() {
        return 5;
    }

    @Unique
    private SimpleContainer getReducedInventory() {
        final SimpleContainer inv = new SimpleContainer(15);
        for (int i = 1; i < this.items.getContainerSize(); i++) {
            inv.setItem(i - 1, this.items.getItem(i));
        }
        return inv;
    }

    @Unique
    private void transferReducedInventory(final SimpleContainer inv) {
        for (int i = 1; i < this.items.getContainerSize(); i++) {
            this.items.setItem(i, inv.getItem(i - 1));
        }
    }

    @Unique
    private void dropItem(ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(this.self.level(), this.self.getX(), this.self.getY(), this.self.getZ(), stack);
        this.self.level().addFreshEntity(itemEntity);
    }

    @Override
    public void spit__(ItemStack stack) {
        if (!stack.isEmpty() && !this.self.level().isClientSide()) {
            ItemEntity itemEntity = new ItemEntity(
                    this.self.level(), this.self.getX() + this.self.getLookAngle().x, this.self.getY() + 1.0, this.self.getZ() + this.self.getLookAngle().z, stack
            );
            itemEntity.setPickUpDelay(40);
            itemEntity.setThrower(this.self);
            this.self.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
            this.self.level().addFreshEntity(itemEntity);
        }
    }

    @Unique
    private void loot__(ItemEntity item) {
        ItemStack itemStack = item.getItem();
        if (!itemStack.isEmpty()) {
            if (this.hasChestEquipped()) {
                if (this.items.canAddItem(itemStack)) {
                    this.self.onItemPickup(item);

                    final SimpleContainer reducedInventory = getReducedInventory();
                    final ItemStack itemStack2 = reducedInventory.addItem(itemStack); //this.items.addStack(itemStack);
                    transferReducedInventory(reducedInventory);

                    final int transfered = itemStack.getCount() - itemStack2.getCount();

                    this.self.take(item, transfered);
                    itemStack.shrink(transfered);

                    if (itemStack.isEmpty()) {
                        item.discard();
                    }

                    this.items.setChanged();
                }
            } else {
                int i = itemStack.getCount();
                if (i > 1) {
                    this.dropItem(itemStack.split(i - 1));
                }

                this.spit__(this.self.getItemBySlot(EquipmentSlot.MAINHAND));
                this.self.onItemPickup(item);
                this.self.setItemSlot(EquipmentSlot.MAINHAND, itemStack.split(1));
                this.self.take(item, itemStack.getCount());
                item.discard();
            }
        }
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void onTickMovement(CallbackInfo ci) {
        Optional<ItemEntity> targetPickup = this.getTargetPickup__();
        if(targetPickup.isPresent()) {
            if (!this.self.level().isClientSide()
                    && this.self.isAlive()
                    && !this.self.isDeadOrDying()
                    && ((ServerLevel)this.self.level()).getGameRules().get(GameRules.MOB_GRIEFING)) {
                final ProfilerFiller profiler = Profiler.get();
                profiler.push("looting");

                Vec3i vec3i = ((MobEntityAccessor) this.self).getItemPickUpRangeExpander__();
                List<ItemEntity> list = this.level().getEntitiesOfClass(ItemEntity.class, this.self.getBoundingBox()
                        .inflate(vec3i.getX(), vec3i.getY(), vec3i.getZ()))
                        .stream().filter(e -> e == targetPickup.get()).toList();

                for (ItemEntity itemEntity : list) {
                    if (!itemEntity.isRemoved() && !itemEntity.getItem().isEmpty() && !itemEntity.hasPickUpDelay() && this.self.wantsToPickUp(((ServerLevel)this.self.level()), itemEntity.getItem())) {
                        this.loot__(itemEntity);
                    }
                }

                profiler.pop();
            }
        }
    }

    @Unique
    private Vec2 vecFromYaw(final float yaw) {
        final float rad = Math.toRadians(yaw);
        return new Vec2(-Mth.sin(rad), Mth.cos(rad));
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"), cancellable = true)
    private void onHandleStatus(byte status, CallbackInfo ci) {
        if (status == EntityEvent.FOX_EAT) {
            ItemStack itemStack = this.self.getItemBySlot(EquipmentSlot.MAINHAND);
            final Vec2 vec = vecFromYaw(self.yBodyRot).normalized();
            if (!itemStack.isEmpty()) {
                for (int i = 0; i < 8; i++) {
                    Vec3 vec3d = new Vec3(((double)this.self.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
                            .xRot(-this.self.getXRot() * (float) (Math.PI / 180.0))
                            .yRot(-this.self.getYRot() * (float) (Math.PI / 180.0));
                    this.self.level().addParticle(
                                    new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(itemStack)),
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

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onRightClick(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.level().isClientSide() &&
                hand == InteractionHand.MAIN_HAND &&
                self.isTame() &&
                self.isOwnedBy(player) &&
                !self.isBaby()
        ) {
            final ItemStack itemStack = player.getItemInHand(hand);
            //System.out.println(itemStack.isOf(InitItem.ITEM_WOLF_BAG));
            if (!this.hasChest() && itemStack.is(InitItem.ITEM_WOLF_BAG)) {
                this.addChest(player, itemStack);
                final InteractionResult result = self.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
                cir.setReturnValue(result);
                cir.cancel();
            } else if (player.isShiftKeyDown()) {
                this.openCustomInventoryScreen(player);
                final InteractionResult result = self.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
                cir.setReturnValue(result);
                cir.cancel();
            }
        }
    }
}
