package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.util.ConfigManager;
import github.meloweh.wolfcompanion.util.LineScan;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class WolfMeleeAttackGoal extends Goal {
    protected final WolfEntity mob;
    private final WolfEntityProvider wolf;
    private double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    private int cooldown;
    private final int attackIntervalTicks = 20;
    private long lastUpdateTime;
    private static final long MAX_ATTACK_TIME = 20L;
    private final double ORIGINAL_SPEED;

    public WolfMeleeAttackGoal(WolfEntity mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.wolf = (WolfEntityProvider) this.mob;
        this.ORIGINAL_SPEED = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private void pickAttacker() {
        if (!this.mob.isTamed()) return;
        if (this.mob.getTarget() != null) return;
        if (!this.wolf.isAggressive__()) return;
        final PlayerEntity player = (PlayerEntity) this.mob.getOwner();
        if (player == null) return;
        final Box playerArea = player.getBoundingBox().expand(10);
        final ServerWorld serverWorld = (ServerWorld) player.getEntityWorld();
        if (serverWorld == null) return;
        final List<WolfEntity> pack = serverWorld.getEntitiesByClass(WolfEntity.class, playerArea,
                e -> e.isTamed() && !e.getUuid().equals(this.mob.getUuid()));
        final List<MobEntity> attackers = serverWorld.getEntitiesByClass(MobEntity.class, playerArea, attacker ->
                attacker instanceof Monster &&
                        attacker.getTarget() != null &&
                        attacker.isAlive() &&
                        !attacker.isRemoved() &&
                        attacker.getTarget().getUuid() == player.getUuid() &&
                        !ConfigManager.isBlacklisted(attacker));

        if (attackers.isEmpty()) return;
        attackers.sort(Comparator.comparingDouble(e -> e.distanceTo(player)));

        if (!this.mob.isWearingBodyArmor() || pack.stream().anyMatch(e -> !e.isWearingBodyArmor())) {
            //Without spread
            final Optional<MobEntity> coop = attackers.stream().filter(attacker -> pack.stream().anyMatch(w ->
                    w.getTarget() != null && w.getTarget().getUuid().equals(attacker.getUuid()))).findFirst();
            if (coop.isPresent()) {
                this.mob.setTarget(coop.get());
            } else if (!attackers.isEmpty()) {
                this.mob.setTarget(attackers.getFirst());
            }
        } else {
            //With spread
            this.mob.setTarget(attackers.getFirst());
        }

    }

    public boolean canStart() {
        long l = this.mob.getEntityWorld().getTime();
        if (l - this.lastUpdateTime < 20L) {
            return false;
        } else {
            this.lastUpdateTime = l;
            pickAttacker();
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive() || livingEntity.isInLava()) {
                return false;
            } else if (LineScan.hasFloorLava(this.mob)) {
                return false;
            } else {
                this.path = this.mob.getNavigation().findPathTo(livingEntity, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.mob.isInAttackRange(livingEntity);
                }
            }
        }
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive() || livingEntity.isInLava()) {
            return false;
        } else if (!this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isIdle();
        } else if (!this.mob.isInPositionTargetRange(livingEntity.getBlockPos())) {
            return false;
        } else {
            return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative();
        }
    }

    public void start() {
        this.speed = this.ORIGINAL_SPEED;

        this.mob.getNavigation().startMovingAlong(this.path, this.speed);
        this.mob.setAttacking(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
    }

    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.mob.setTarget((LivingEntity)null);
        }

        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
            this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
            if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.targetX = livingEntity.getX();
                this.targetY = livingEntity.getY();
                this.targetZ = livingEntity.getZ();
                this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
                double d = this.mob.squaredDistanceTo(livingEntity);
                if (d > 1024.0) {
                    this.updateCountdownTicks += 10;
                } else if (d > 256.0) {
                    this.updateCountdownTicks += 5;
                }

                this.speed *= 1.2f;
                this.speed = Math.min(3f, this.speed);
                System.out.println(this.speed + " --- " + this.ORIGINAL_SPEED);

                if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {
                    this.updateCountdownTicks += 15;
                }

                this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
            }

            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.attack(livingEntity);
        }
    }

    protected void attack(LivingEntity target) {
        if (this.canAttack(target)) {
            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            this.wolf.tryAttack__(getServerWorld(this.mob), target);
        }

    }

    protected void resetCooldown() {
        this.cooldown = this.getTickCount(20);
    }

    protected boolean isCooledDown() {
        return this.cooldown <= 0;
    }

    protected boolean canAttack(LivingEntity target) {
        return this.isCooledDown() && this.mob.isInAttackRange(target) && this.mob.getVisibilityCache().canSee(target);
    }

    protected int getCooldown() {
        return this.cooldown;
    }

    protected int getMaxCooldown() {
        return this.getTickCount(20);
    }
}
