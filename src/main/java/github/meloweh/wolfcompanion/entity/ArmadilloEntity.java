package github.meloweh.wolfcompanion.entity;

import github.meloweh.wolfcompanion.registry.ModEntities;
import github.meloweh.wolfcompanion.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class ArmadilloEntity extends Animal {
    private static final String STATE_KEY = "state";
    private static final String SCUTE_TIME_KEY = "scute_time";
    private static final String LEGACY_SCUTE_TIME_KEY = "ScuteTime";
    private static final EntityDataAccessor<Byte> ARMADILLO_STATE = SynchedEntityData.defineId(
            ArmadilloEntity.class,
            EntityDataSerializers.BYTE
    );
    private static final int SCARED_TICKS_AFTER_DANGER = 80;
    private static final int SCARE_CHECK_INTERVAL = 20;
    private static final int PEEK_EVENT = 64;
    private static final double SCARE_DISTANCE_HORIZONTAL = 7.0D;
    private static final double SCARE_DISTANCE_VERTICAL = 2.0D;

    private long inStateTicks;
    private int scuteTime;
    private int dangerTicks;
    private int nextPeekTicks;

    public ArmadilloEntity(EntityType<? extends ArmadilloEntity> entityType, Level level) {
        super(entityType, level);
        this.getNavigation().setCanFloat(true);
        this.scuteTime = nextScuteTime();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.14D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARMADILLO_STATE, (byte) ArmadilloState.IDLE.ordinal());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ArmadilloPanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new ArmadilloRollUpGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(Items.SPIDER_EYE), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.SPIDER_EYE);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.ARMADILLO.create(level);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide() && this.isAlive() && !this.isBaby() && --this.scuteTime <= 0) {
            shedScute();
            this.scuteTime = nextScuteTime();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isScared()) {
            this.setYHeadRot(this.getYRot());
        }
        this.inStateTicks++;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ARMADILLO_STATE.equals(key)) {
            this.inStateTicks = 0L;
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.BRUSH) && brushOffScute()) {
            if (!this.level().isClientSide()) {
                stack.hurtAndBreak(16, player, p -> p.broadcastBreakEvent(hand));
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        if (this.isScared()) {
            return InteractionResult.FAIL;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString(STATE_KEY, this.getState().getSerializedName());
        nbt.putInt(SCUTE_TIME_KEY, this.scuteTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.switchToState(ArmadilloState.fromName(nbt.getString(STATE_KEY)));
        if (nbt.contains(SCUTE_TIME_KEY)) {
            this.scuteTime = nbt.getInt(SCUTE_TIME_KEY);
        } else if (nbt.contains(LEGACY_SCUTE_TIME_KEY)) {
            this.scuteTime = nbt.getInt(LEGACY_SCUTE_TIME_KEY);
        } else {
            this.scuteTime = nextScuteTime();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isScared()) {
            amount = (amount - 1.0F) / 2.0F;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        super.actuallyHurt(source, amount);
        if (this.isNoAi() || this.isDeadOrDying()) {
            return;
        }

        if (source.getEntity() instanceof LivingEntity) {
            this.dangerTicks = SCARED_TICKS_AFTER_DANGER;
            if (this.canStayRolledUp()) {
                this.rollUp();
            }
        } else if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_DROWNING)) {
            this.rollOut();
        }
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.isScared();
    }

    @Override
    public void setInLove(Player player) {
        super.setInLove(player);
        this.playSound(SoundEvents.SNIFFER_EAT, 1.0F, 1.0F);
    }

    @Override
    public void ageUp(int amount, boolean forced) {
        if (this.isBaby() && forced) {
            this.playSound(SoundEvents.SNIFFER_EAT, 1.0F, 1.0F);
        }
        super.ageUp(amount, forced);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == PEEK_EVENT && this.level().isClientSide()) {
            this.playSound(SoundEvents.SNIFFER_SNIFFING, 1.0F, 1.0F);
            return;
        }
        super.handleEntityEvent(status);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isScared()) {
            return null;
        }
        return SoundEvents.TURTLE_AMBIENT_LAND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isScared() ? SoundEvents.TURTLE_HURT_BABY : SoundEvents.TURTLE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.TURTLE_DEATH;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.TURTLE_SHAMBLE, 0.15F, 1.0F);
    }

    @Override
    public int getMaxHeadYRot() {
        return this.isScared() ? 0 : 32;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (!this.isBaby()) {
            this.spawnAtLocation(new ItemStack(ModItems.ARMADILLO_SCUTE, 1 + this.random.nextInt(1 + Math.max(0, looting))));
        }
    }

    public static boolean checkArmadilloSpawnRules(EntityType<ArmadilloEntity> type, LevelAccessor level, MobSpawnType spawnType, net.minecraft.core.BlockPos pos, net.minecraft.util.RandomSource random) {
        return Animal.checkAnimalSpawnRules(type, level, spawnType, pos, random);
    }

    public ArmadilloState getState() {
        return ArmadilloState.byId(this.entityData.get(ARMADILLO_STATE));
    }

    public boolean isScared() {
        return this.getState() != ArmadilloState.IDLE;
    }

    public boolean shouldHideInShell() {
        return this.getState().shouldHideInShell(this.inStateTicks);
    }

    public boolean shouldSwitchToScaredState() {
        return this.getState() == ArmadilloState.ROLLING && this.inStateTicks > ArmadilloState.ROLLING.animationDuration();
    }

    public void switchToState(ArmadilloState state) {
        if (this.getState() == state) {
            return;
        }
        this.entityData.set(ARMADILLO_STATE, (byte) state.ordinal());
        this.inStateTicks = 0L;
    }

    public void rollUp() {
        if (this.isScared()) {
            return;
        }
        this.stopInPlace();
        this.resetLove();
        this.gameEvent(GameEvent.ENTITY_INTERACT);
        this.playSound(SoundEvents.TURTLE_EGG_CRACK, 1.0F, 1.0F);
        this.switchToState(ArmadilloState.ROLLING);
    }

    public void rollOut() {
        if (!this.isScared()) {
            return;
        }
        this.gameEvent(GameEvent.ENTITY_INTERACT);
        this.playSound(SoundEvents.TURTLE_EGG_HATCH, 1.0F, 1.0F);
        this.switchToState(ArmadilloState.IDLE);
        this.dangerTicks = 0;
        this.nextPeekTicks = 0;
    }

    public boolean canStayRolledUp() {
        return !this.isInWaterOrBubble()
                && !this.isInLava()
                && !this.isLeashed()
                && !this.isPassenger()
                && !this.isVehicle();
    }

    public boolean brushOffScute() {
        if (this.isBaby()) {
            return false;
        }
        if (!this.level().isClientSide()) {
            this.spawnAtLocation(new ItemStack(ModItems.ARMADILLO_SCUTE));
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.BRUSH_GENERIC, 1.0F, 1.0F);
        }
        return true;
    }

    public boolean isScaredBy(LivingEntity entity) {
        if (entity == this || !entity.isAlive()) {
            return false;
        }
        AABB scareBox = this.getBoundingBox().inflate(SCARE_DISTANCE_HORIZONTAL, SCARE_DISTANCE_VERTICAL, SCARE_DISTANCE_HORIZONTAL);
        if (!scareBox.intersects(entity.getBoundingBox())) {
            return false;
        }
        if (entity.getMobType() == MobType.UNDEAD) {
            return true;
        }
        if (this.getLastHurtByMob() == entity) {
            return true;
        }
        if (entity instanceof Player player) {
            return !player.isSpectator() && (player.isSprinting() || player.isPassenger());
        }
        return false;
    }

    public float getShellProgress(float tickDelta) {
        ArmadilloState state = this.getState();
        float progress = Math.min(1.0F, (this.inStateTicks + tickDelta) / Math.max(1.0F, state.animationDuration()));
        return switch (state) {
            case IDLE -> 0.0F;
            case ROLLING -> progress;
            case SCARED -> 1.0F;
            case UNROLLING -> 1.0F - progress;
        };
    }

    private void shedScute() {
        this.spawnAtLocation(ModItems.ARMADILLO_SCUTE);
        this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);
        this.gameEvent(GameEvent.ENTITY_PLACE);
    }

    private int nextScuteTime() {
        return this.random.nextInt(20 * 60 * 5) + 20 * 60 * 5;
    }

    private void stopInPlace() {
        this.getNavigation().stop();
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(0.0D, movement.y, 0.0D);
    }

    private LivingEntity findNearestThreat() {
        if (this.tickCount % SCARE_CHECK_INTERVAL != 0 && this.dangerTicks > 0) {
            return null;
        }
        AABB scareBox = this.getBoundingBox().inflate(SCARE_DISTANCE_HORIZONTAL, SCARE_DISTANCE_VERTICAL, SCARE_DISTANCE_HORIZONTAL);
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, scareBox, this::isScaredBy);
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (LivingEntity entity : entities) {
            double distance = this.distanceToSqr(entity);
            if (distance < nearestDistance) {
                nearest = entity;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    public enum ArmadilloState implements StringRepresentable {
        IDLE("idle", false, 0) {
            @Override
            boolean shouldHideInShell(long ticks) {
                return false;
            }
        },
        ROLLING("rolling", true, 10) {
            @Override
            boolean shouldHideInShell(long ticks) {
                return ticks > 5L;
            }
        },
        SCARED("scared", true, 50) {
            @Override
            boolean shouldHideInShell(long ticks) {
                return true;
            }
        },
        UNROLLING("unrolling", true, 30) {
            @Override
            boolean shouldHideInShell(long ticks) {
                return ticks < 26L;
            }
        };

        private final String name;
        private final boolean threatened;
        private final int animationDuration;

        ArmadilloState(String name, boolean threatened, int animationDuration) {
            this.name = name;
            this.threatened = threatened;
            this.animationDuration = animationDuration;
        }

        static ArmadilloState fromName(String name) {
            for (ArmadilloState state : values()) {
                if (state.name.equals(name)) {
                    return state;
                }
            }
            return IDLE;
        }

        static ArmadilloState byId(byte id) {
            ArmadilloState[] values = values();
            return values[Math.floorMod(id, values.length)];
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        boolean isThreatened() {
            return this.threatened;
        }

        int animationDuration() {
            return this.animationDuration;
        }

        abstract boolean shouldHideInShell(long ticks);
    }

    private static final class ArmadilloPanicGoal extends PanicGoal {
        private final ArmadilloEntity armadillo;

        private ArmadilloPanicGoal(ArmadilloEntity armadillo, double speedModifier) {
            super(armadillo, speedModifier);
            this.armadillo = armadillo;
        }

        @Override
        public void start() {
            this.armadillo.rollOut();
            super.start();
        }
    }

    private static final class ArmadilloRollUpGoal extends Goal {
        private final ArmadilloEntity armadillo;

        private ArmadilloRollUpGoal(ArmadilloEntity armadillo) {
            this.armadillo = armadillo;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return this.armadillo.isScared()
                    || (this.armadillo.canStayRolledUp() && this.armadillo.findNearestThreat() != null);
        }

        @Override
        public boolean canContinueToUse() {
            return this.armadillo.isScared();
        }

        @Override
        public void start() {
            this.armadillo.dangerTicks = SCARED_TICKS_AFTER_DANGER;
            this.armadillo.nextPeekTicks = this.pickNextPeekTimer();
            this.armadillo.rollUp();
        }

        @Override
        public void stop() {
            if (this.armadillo.isScared() && !this.armadillo.canStayRolledUp()) {
                this.armadillo.rollOut();
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (!this.armadillo.canStayRolledUp()) {
                this.armadillo.rollOut();
                return;
            }

            this.armadillo.stopInPlace();
            if (this.armadillo.findNearestThreat() != null) {
                this.armadillo.dangerTicks = SCARED_TICKS_AFTER_DANGER;
            } else if (this.armadillo.dangerTicks > 0) {
                this.armadillo.dangerTicks--;
            }

            ArmadilloState state = this.armadillo.getState();
            if (this.armadillo.shouldSwitchToScaredState()) {
                this.armadillo.switchToState(ArmadilloState.SCARED);
                if (this.armadillo.onGround()) {
                    this.armadillo.playSound(SoundEvents.TURTLE_EGG_BREAK, 1.0F, 1.0F);
                }
                return;
            }

            if (state == ArmadilloState.SCARED) {
                this.tickScaredState();
            } else if (state == ArmadilloState.UNROLLING) {
                this.tickUnrollingState();
            }
        }

        private void tickScaredState() {
            if (this.armadillo.nextPeekTicks > 0) {
                this.armadillo.nextPeekTicks--;
            }
            if (this.armadillo.nextPeekTicks == 0 && this.armadillo.dangerTicks > ArmadilloState.UNROLLING.animationDuration() && this.armadillo.level() instanceof ServerLevel serverLevel) {
                serverLevel.broadcastEntityEvent(this.armadillo, (byte) PEEK_EVENT);
                this.armadillo.nextPeekTicks = this.pickNextPeekTimer();
            }
            if (this.armadillo.dangerTicks < ArmadilloState.UNROLLING.animationDuration()) {
                this.armadillo.playSound(SoundEvents.TURTLE_EGG_CRACK, 1.0F, 1.0F);
                this.armadillo.switchToState(ArmadilloState.UNROLLING);
            }
        }

        private void tickUnrollingState() {
            if (this.armadillo.dangerTicks > ArmadilloState.UNROLLING.animationDuration()) {
                this.armadillo.switchToState(ArmadilloState.SCARED);
            } else if (this.armadillo.inStateTicks > ArmadilloState.UNROLLING.animationDuration()) {
                this.armadillo.rollOut();
            }
        }

        private int pickNextPeekTimer() {
            return ArmadilloState.SCARED.animationDuration() + this.armadillo.getRandom().nextIntBetweenInclusive(100, 400);
        }
    }
}
