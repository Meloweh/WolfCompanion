package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.accessor.*;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.goals.*;
import github.meloweh.wolfcompanion.init.InitItem;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.screenhandler.WolfInventoryScreenHandler;
import github.meloweh.wolfcompanion.util.ConfigManager;
import github.meloweh.wolfcompanion.util.NBTHelper;
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
import net.minecraft.nbt.*;
import net.minecraft.particle.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
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
        Saddleable,
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
        this.self = (WolfEntity) (Object) this;
        this.onChestedStatusChanged();
        //this.self.setCanPickUpLoot(true);
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

//    @Unique
//    public SoundEvent getEatSound(ItemStack stack) {
//        return SoundEvents.ENTITY_FOX_EAT;
//    }

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
        if (!self.getWorld().isClient) {
            //this.furWet = true;
            //this.self.getWorld().sendEntityStatus(this.self, (byte)56);
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

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private ParticleEffect changeType(ParticleEffect parameters) {
        final byte shakeReason = getShakeReason();

        //System.out.println(shakeReason + " " + self.getWorld().isClient);

        return switch (shakeReason) {
            case 1 -> EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.529f, 0.639f, 0.388f);
            case 2 -> ParticleTypes.SMOKE;
            default -> ParticleTypes.SPLASH;
        };
    }

//    @ModifyArgs(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
//    private void changeShakingParticles(Args args) {
//
//        args.set(1, ParticleTypes.SMOKE);
//    }


//    @Unique
//    public void spawnPoisonDustParticle(World world, double x, double y, double z) {
//        // Create a new DustParticleEffect with RGB values suitable for a "poison" color
//        Vector3f color = new Vector3f(0.2f, 0.8f, 0.2f); // A green color
//        float particleScale = 1.0f; // Size of the dust particle
//
//        //DustParticleEffect dustParticle = new DustParticleEffect(color, particleScale);
//        final EntityEffectParticleEffect dustParticle = EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.2f, 0.8f, 0.2f);
//
//        // Example of how to spawn this particle
//        for (int i = 0; i < 10; i++) {  // Generate 20 particles for visibility
//            double offsetX = world.random.nextGaussian() * 0.02;
//            double offsetY = 0.5f + world.random.nextGaussian() * 0.02;
//            double offsetZ = world.random.nextGaussian() * 0.02;
//            world.addParticle(dustParticle, x, y, z, offsetX, offsetY, offsetZ);
//        }
//    }


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
        if (self.isAlive() && !self.getWorld().isClient) {
             byte shakeReason = 0;
             if (!self.isWet() && getShakeReason() == 0) {
                 if (ConfigManager.config.canShakeOffPoison && isPoisoned(this.self))
                     shakeReason = 1;

                 if (ConfigManager.config.canShakeOffFire && self.isOnFire() && !self.isInLava() && self.isOnGround() && self.prevY <= self.getY()) {
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

    }

//    @Inject(method = "tick", at = @At("TAIL"))
//    private void shakeConditions(CallbackInfo ci) {
//        //if (self.hasStatusEffect())
//        if (/*hasNegativeStatusEffect(this.self) || */self.isOnFire() && !self.isInLava()) {
//            //doWolfShake();
//            //this.self.removeStatusEffect(StatusEffects.POISON);
//            //self.setOnFire(false);
//
//            spawnPoisonDustParticle(self.getWorld(), self.getX(), self.getY(), self.getZ());
//        }
//    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void cancelDeath(DamageSource damageSource, CallbackInfo ci) {
        if (this.self.isTamed() && !this.self.getWorld().isClient && ConfigManager.config.canRespawn) {
            final NbtCompound wolfNbt = new NbtCompound();
            this.self.writeCustomDataToNbt(wolfNbt);

            wolfcompanion_template_1_21_1$dropInventoryByButton();

            if (this.self.getOwner() != null) {
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
    @Unique
    private static final TrackedData<Boolean> DROP_CHEST = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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
        builder.add(SHAKE_REASON, (byte)0);
        builder.add(XP, 0);
    }

    @Unique
    private float getKnockbackAgainst(Entity target, DamageSource damageSource) {
        float f = (float)this.self.getAttributeValue(EntityAttributes.ATTACK_KNOCKBACK);
        World var5 = this.self.getWorld();
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
            int i = EnchantmentHelper.getRepairWithExperience((ServerWorld) this.self.getWorld(), itemStack, amount);
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
    public void wolfcompanion_template_1_21_1$dropInventoryByButton() {

        if (shouldDropChest() || !ConfigManager.config.keepWolfInventory) {
            if (this.items != null) {
                for (int i = this.items.size(); i >= 0; i--) {
                    final ItemStack itemStack = this.items.getStack(i);
                    if (!itemStack.isEmpty()) {
                        if (self.getEquippedStack(EquipmentSlot.BODY) != itemStack) {
                            this.items.removeStack(i);
                            self.dropStack((ServerWorld) self.getWorld(), itemStack);
                        }
                    }
                }
            }

            if (this.hasChest()) {

                if (shouldDropChest() || !ConfigManager.config.keepWolfBag) {
                    if (!self.getWorld().isClient) {
                        self.dropItem((ServerWorld) self.getWorld(), InitItem.ITEM_WOLF_BAG);
                    }
                    this.setHasChest(false);
                }
            }
            setShouldDropChest(false);
        }

    }

    public void dropInventory() {
//        if (this.items != null) {
//            for (int i = this.items.size(); i >= 0; i--) {
//                final ItemStack itemStack = this.items.getStack(i);
//                if (!itemStack.isEmpty()) {
//                    if (self.getEquippedStack(EquipmentSlot.BODY) != itemStack) {
//                        this.items.removeStack(i);
//                        self.dropStack(itemStack);
//                    }
//                }
//            }
//        }
//
//        if (this.hasChest()) {
//            if (!self.getWorld().isClient) {
//                self.dropItem(InitItem.ITEM_WOLF_BAG);
//            }
//
//            this.setHasChest(false);
//        }
//        setShouldDropChest(false);
    }

//    @Unique
//    private void dropEverything() {
//        if (!ConfigManager.config.keepWolfInventory) {
//            if (this.items != null) {
//                this.items.clearToList().forEach(itemStack -> {
//                    if (!itemStack.isEmpty()) {
//                        if (self.getEquippedStack(EquipmentSlot.BODY) != itemStack || !ConfigManager.config.keepWolfArmor) {
//                            self.dropStack(itemStack);
//                        }
//                    }
//                });
//            }
//
//            if (this.hasChest() && !ConfigManager.config.keepWolfBag) {
//                if (!self.getWorld().isClient) {
//                    self.dropItem(InitItem.ITEM_WOLF_BAG);
//                }
//
//                this.setHasChest(false);
//            }
//        }
//        setShouldDropChest(false);
//    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void cancelPlayerDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity && this.self.isTamed()) {
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
                if (!itemStack.isEmpty()) {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte)(i - 1));
                    nbtList.add(itemStack.toNbt(self.getRegistryManager(), nbtCompound));
                }
            }

            nbt.put("Items", nbtList);
        }
        Text text = this.self.getCustomName();
        if (text != null) {
            nbt.putString("CustomName", Text.Serialization.toJsonString(text, this.self.getRegistryManager()));
        }
        nbt.putInt("XP", this.getXp());
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
        this.setXp(nbt.getInt("XP"));
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
        ItemEntity itemEntity = new ItemEntity(this.self.getWorld(), this.self.getX(), this.self.getY(), this.self.getZ(), stack);
        this.self.getWorld().spawnEntity(itemEntity);
    }

    @Override
    public void spit__(ItemStack stack) {
        if (!stack.isEmpty() && !this.self.getWorld().isClient) {
            ItemEntity itemEntity = new ItemEntity(
                    this.self.getWorld(), this.self.getX() + this.self.getRotationVector().x, this.self.getY() + 1.0, this.self.getZ() + this.self.getRotationVector().z, stack
            );
            itemEntity.setPickupDelay(40);
            itemEntity.setThrower(this.self);
            this.self.playSound(SoundEvents.ENTITY_FOX_SPIT, 1.0F, 1.0F);
            this.self.getWorld().spawnEntity(itemEntity);
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
            if (!this.self.getWorld().isClient
                    && this.self.isAlive()
                    && !this.self.isDead()
                    && ((ServerWorld)this.self.getWorld()).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                final Profiler profiler = Profilers.get();
                profiler.push("looting");

                Vec3i vec3i = ((MobEntityAccessor) this.self).getItemPickUpRangeExpander__();
                List<ItemEntity> list = this.getWorld().getNonSpectatingEntities(ItemEntity.class, this.self.getBoundingBox()
                        .expand(vec3i.getX(), vec3i.getY(), vec3i.getZ()))
                        .stream().filter(e -> e == this.targetPickup.get()).toList();

                for (ItemEntity itemEntity : list) {
                    if (!itemEntity.isRemoved() && !itemEntity.getStack().isEmpty() && !itemEntity.cannotPickup() && this.self.canGather(((ServerWorld)this.self.getWorld()), itemEntity.getStack())) {
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
                    this.self.getWorld().addParticle(
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
        if (!player.getWorld().isClient() &&
                hand == Hand.MAIN_HAND &&
                self.isTamed() &&
                self.isOwner(player) &&
                !self.isBaby()
        ) {
            final ItemStack itemStack = player.getStackInHand(hand);
            System.out.println(itemStack.isOf(InitItem.ITEM_WOLF_BAG));
            if (!this.hasChest() && itemStack.isOf(InitItem.ITEM_WOLF_BAG)) {
                this.addChest(player, itemStack);
                final ActionResult result = self.getWorld().isClient ? ActionResult.SUCCESS : ActionResult.SUCCESS_SERVER;
                cir.setReturnValue(result);
                cir.cancel();
            } else if (player.isSneaking()) {
                this.openInventory(player);
                final ActionResult result = self.getWorld().isClient ? ActionResult.SUCCESS : ActionResult.SUCCESS_SERVER;
                cir.setReturnValue(result);
                cir.cancel();
            }
        }
    }
}

