package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.config.WolfCompanionConfig;
import github.meloweh.wolfcompanion.util.LineScan;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

public class WolfMeleeAttackGoal extends Goal {
    protected final Wolf mob;
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

    public WolfMeleeAttackGoal(Wolf mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.wolf = (WolfEntityProvider) this.mob;
        this.ORIGINAL_SPEED = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private void pickAttacker() {
        if (!this.mob.isTame()) return;
        if (this.mob.getTarget() != null) return;
        if (!this.wolf.isAggressive__()) return;
        final Player player = (Player) this.mob.getOwner();
        if (player == null) return;
        final AABB playerArea = player.getBoundingBox().inflate(10);
        final ServerLevel serverWorld = (ServerLevel) player.level();
        if (serverWorld == null) return;
        final List<Wolf> pack = serverWorld.getEntitiesOfClass(Wolf.class, playerArea,
                e -> e.isTame() && !e.getUUID().equals(this.mob.getUUID()));
        final List<Mob> attackers = serverWorld.getEntitiesOfClass(Mob.class, playerArea, attacker ->
                attacker instanceof Enemy &&
                        attacker.getTarget() != null &&
                        attacker.isAlive() &&
                        !attacker.isRemoved() &&
                        attacker.getTarget().getUUID() == player.getUUID() &&
                        !WolfCompanionConfig.isEntityAttackBlocked(attacker));

        if (attackers.isEmpty()) return;
        attackers.sort(Comparator.comparingDouble(e -> e.distanceTo(player)));

        if (!this.mob.isWearingBodyArmor() || pack.stream().anyMatch(e -> !e.isWearingBodyArmor())) {
            //Without spread
            final Optional<Mob> coop = attackers.stream().filter(attacker -> pack.stream().anyMatch(w ->
                    w.getTarget() != null && w.getTarget().getUUID().equals(attacker.getUUID()))).findFirst();
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

    public boolean canUse() {
        long l = this.mob.level().getGameTime();
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
                this.path = this.mob.getNavigation().createPath(livingEntity, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.mob.isWithinMeleeAttackRange(livingEntity);
                }
            }
        }
    }

    public boolean canContinueToUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive() || livingEntity.isInLava()) {
            return false;
        } else if (!this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isDone();
        } else if (!this.mob.isWithinRestriction(livingEntity.blockPosition())) {
            return false;
        } else {
            return !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player)livingEntity).isCreative();
        }
    }

    public void start() {
        this.speed = this.ORIGINAL_SPEED;

        this.mob.getNavigation().moveTo(this.path, this.speed);
        this.mob.setAggressive(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
    }

    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.mob.setTarget((LivingEntity)null);
        }

        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
            if ((this.pauseWhenMobIdle || this.mob.getSensing().hasLineOfSight(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || livingEntity.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.targetX = livingEntity.getX();
                this.targetY = livingEntity.getY();
                this.targetZ = livingEntity.getZ();
                this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
                double d = this.mob.distanceToSqr(livingEntity);
                if (d > 1024.0) {
                    this.updateCountdownTicks += 10;
                } else if (d > 256.0) {
                    this.updateCountdownTicks += 5;
                }

                this.speed *= WolfCompanionConfig.current().attackAcceleration;
                this.speed = Math.min(WolfCompanionConfig.current().maxSpeed, this.speed);

                if (!this.mob.getNavigation().moveTo(livingEntity, this.speed)) {
                    this.updateCountdownTicks += 15;
                }

                this.updateCountdownTicks = this.adjustedTickDelay(this.updateCountdownTicks);
            }

            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.attack(livingEntity);
        }
    }

    protected void attack(LivingEntity target) {
        if (this.canAttack(target)) {
            this.resetCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.wolf.tryAttack__(target);
        }

    }

    protected void resetCooldown() {
        this.cooldown = this.adjustedTickDelay(20);
    }

    protected boolean isCooledDown() {
        return this.cooldown <= 0;
    }

    protected boolean canAttack(LivingEntity target) {
        return this.isCooledDown() && this.mob.isWithinMeleeAttackRange(target) && this.mob.getSensing().hasLineOfSight(target);
    }

    protected int getCooldown() {
        return this.cooldown;
    }

    protected int getMaxCooldown() {
        return this.adjustedTickDelay(20);
    }
}
