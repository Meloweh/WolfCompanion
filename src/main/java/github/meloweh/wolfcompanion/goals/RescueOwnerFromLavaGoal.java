package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.util.Pair;
import github.meloweh.wolfcompanion.util.WolfInventoryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class RescueOwnerFromLavaGoal extends Goal {
    private final TamableAnimal wolf;
    @Nullable
    private LivingEntity owner;
    private final double speed;
    private final PathNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final WolfEntityProvider armoredWolf;
    private final List<ItemStack> inventoryContents;
    private int shootCooldown, teleportCooldown;
    private final static int TP_COOLDOWN = 45, SHOOT_COOLDOWN = 20;
    private Pair<ItemStack, Holder<Potion>> usingPotion = Pair.of(ItemStack.EMPTY, Potions.AWKWARD);

    public RescueOwnerFromLavaGoal(Wolf wolf, double speed, float minDistance, float maxDistance) {
        this.wolf = wolf;
        this.speed = speed;
        this.navigation = wolf.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.shootCooldown = SHOOT_COOLDOWN;
        this.teleportCooldown = TP_COOLDOWN;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(wolf.getNavigation() instanceof GroundPathNavigation) && !(wolf.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
        this.armoredWolf = (WolfEntityProvider) wolf;
        this.inventoryContents = new ArrayList<>();
    }

    private void refreshInventoryContents(Container invBasic) {
        this.inventoryContents.clear();
        for(int slotIndex = 1;
            slotIndex < 16;
            ++slotIndex) {
            this.inventoryContents.add(invBasic.getItem(slotIndex));
        }
    }

    private void inventoryInit() {
        refreshInventoryContents(armoredWolf.getInventory());
    }

    public boolean canUse() {
        if (!this.armoredWolf.hasChestEquipped()) return false;

        this.owner = this.wolf.getOwner();
        if (this.wolf.getItemBySlot(EquipmentSlot.MAINHAND).has(DataComponents.POTION_CONTENTS)) {
            this.wolf.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        final boolean wouldStart = this.wolf.isTame()
                && this.armoredWolf.hasChestEquipped()
                && this.owner != null
                && !this.owner.isSpectator()
                && !this.owner.hasInfiniteMaterials();

        if (wouldStart) {
            refreshInventoryContents(armoredWolf.getInventory());
            if (WolfInventoryHelper.hasFittingLifesavingEffect(this.owner, inventoryContents)) return false;
        }

        return wouldStart;
    }

    public boolean canContinueToUse() {
        return shootCooldown > 0;
    }

    public void start() {
        inventoryInit();

        this.shootCooldown = SHOOT_COOLDOWN;
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.wolf.getPathfindingMalus(PathType.WATER);
        this.wolf.setPathfindingMalus(PathType.WATER, 0.0F);
        this.teleportCooldown = TP_COOLDOWN;
        this.wolf.setOrderedToSit(false);

        usingPotion = nextPotion();

        if (usingPotion.first.isEmpty()) return;

        this.wolf.setItemSlot(EquipmentSlot.MAINHAND, usingPotion.first);

        shootCooldown = SHOOT_COOLDOWN;
    }

    public void stop() {
        shootCooldown = SHOOT_COOLDOWN;
        this.owner = null;
        this.navigation.stop();
        this.wolf.setPathfindingMalus(PathType.WATER, this.oldWaterPathfindingPenalty);
        teleportCooldown = TP_COOLDOWN;
        this.wolf.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        inventoryInit();
    }

    public void tick() {
        if (!this.wolf.level().isClientSide() &&
                this.wolf.isAlive() &&
                this.wolf.isEffectiveAi()) {
            this.wolf.getLookControl().setLookAt(this.owner, 10.0F, (float) this.wolf.getMaxHeadXRot());

            final Pair<ItemStack, Holder<Potion>> itemStack = usingPotion;
            shootCooldown--;

            if (WolfInventoryHelper.hasFittingLifesavingEffect(this.owner, inventoryContents)) return;

            if (this.teleportCooldown > 0) this.teleportCooldown--;

            if (--this.updateCountdownTicks <= 0) {
                this.updateCountdownTicks = this.adjustedTickDelay(10);
                if (teleportCooldown <= 0) {
                    this.wolf.tryToTeleportToOwner();
                    this.teleportCooldown = wolf.getRandom().nextIntBetweenInclusive(TP_COOLDOWN / 2, TP_COOLDOWN + TP_COOLDOWN / 2);
                } else {

                    this.navigation.moveTo(this.owner, this.speed);
                }

            }

            if (!itemStack.first.isEmpty()) {
                this.wolf.setItemSlot(EquipmentSlot.MAINHAND, itemStack.first);

                if (this.wolf.distanceToSqr(this.owner) <= (double) (this.maxDistance * this.maxDistance) &&
                        this.wolf.hasLineOfSight(this.owner) &&
                        this.wolf.getItemBySlot(EquipmentSlot.MAINHAND) == itemStack.first) {
                    shoot(itemStack);
                    this.wolf.setItemSlot(EquipmentSlot.MAINHAND, nextPotion().first);
                }
            }
        }
    }

    private Pair<ItemStack, Holder<Potion>> nextPotion() {
        final Pair<ItemStack, Holder<Potion>> itemStack = WolfInventoryHelper.findLifesavingPotions(inventoryContents, this.owner);
        this.wolf.setItemSlot(EquipmentSlot.MAINHAND, itemStack.first);

        return itemStack;
    }

    public void shoot(final Pair<ItemStack, Holder<Potion>> itemStack) {
        Vec3 vec3d = this.owner.getDeltaMovement();
        double d = this.owner.getX() + vec3d.x - this.wolf.getX();
        double e = this.owner.getEyeY() - 1.100000023841858 - this.wolf.getY();
        double f = this.owner.getZ() + vec3d.z - this.wolf.getZ();
        double g = Math.sqrt(d * d + f * f);

        Holder<Potion> registryEntry = itemStack.second;

        ThrownPotion potionEntity = new ThrownPotion(this.wolf.level(), this.wolf);
        potionEntity.setItem(PotionContents.createItemStack(Items.SPLASH_POTION, registryEntry));
        potionEntity.setXRot(potionEntity.getXRot() - -20.0F);
        potionEntity.shoot(d, e + g * 0.2, f, 0.75F, 0F);
        this.wolf.level().playSound(null, this.wolf.getX(), this.wolf.getY(), this.wolf.getZ(), SoundEvents.SPLASH_POTION_THROW, this.wolf.getSoundSource(), 1.0F, 0.4F + this.wolf.getRandom().nextFloat() * 0.4F);

        this.wolf.level().addFreshEntity(potionEntity);

        itemStack.first.shrink(1);
        ItemStack itemStack2 = itemStack.first.finishUsingItem(this.wolf.level(), this.wolf);
        if (!itemStack2.isEmpty()) {
            this.wolf.setItemSlot(EquipmentSlot.MAINHAND, itemStack2);
        }
    }
}
