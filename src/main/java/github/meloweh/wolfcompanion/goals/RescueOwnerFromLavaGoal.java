package github.meloweh.wolfcompanion.goals;

import com.ibm.icu.impl.Pair;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.util.WolfInventoryHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.StreamSupport;

public class RescueOwnerFromLavaGoal extends Goal implements InventoryChangedListener {
    private final TameableEntity wolf;
    @Nullable
    private LivingEntity owner;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final WolfEntityProvider armoredWolf;
    private final List<ItemStack> inventoryContents;
    private int shootCooldown, teleportCooldown;
    private final static int TP_COOLDOWN = 45, SHOOT_COOLDOWN = 20;
    private Pair<ItemStack, RegistryEntry<Potion>> usingPotion = Pair.of(ItemStack.EMPTY, Potions.AWKWARD);

    public RescueOwnerFromLavaGoal(WolfEntity wolf, double speed, float minDistance, float maxDistance) {
        this.wolf = wolf;
        this.speed = speed;
        this.navigation = wolf.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.shootCooldown = SHOOT_COOLDOWN;
        this.teleportCooldown = TP_COOLDOWN;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        if (!(wolf.getNavigation() instanceof MobNavigation) && !(wolf.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
        this.armoredWolf = (WolfEntityProvider) wolf;
        this.inventoryContents = new ArrayList<>();
    }

    private void refreshInventoryContents(Inventory invBasic) {
        this.inventoryContents.clear();
        for(int slotIndex = 1;
            slotIndex < 16;
            ++slotIndex) {
            this.inventoryContents.add(invBasic.getStack(slotIndex));
        }
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        this.refreshInventoryContents(sender);
    }

    private void inventoryInit() {
        armoredWolf.getInventory().removeListener(this);
        armoredWolf.getInventory().addListener(this);
        refreshInventoryContents(armoredWolf.getInventory());
    }

    public boolean canStart() {
        this.owner = this.wolf.getOwner();
        if (this.wolf.getEquippedStack(EquipmentSlot.MAINHAND).contains(DataComponentTypes.POTION_CONTENTS)) {
            this.wolf.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        final boolean wouldStart = this.wolf.isTamed()
                && this.armoredWolf.hasChestEquipped()
                && this.owner != null
                && !this.owner.isSpectator()
                && !this.owner.isInCreativeMode();

        if (wouldStart) {
            refreshInventoryContents(armoredWolf.getInventory());
            if (WolfInventoryHelper.hasFittingLifesavingEffect(this.owner, inventoryContents)) return false;
        }

        return wouldStart;
    }

    public boolean shouldContinue() {
        return shootCooldown > 0;
    }

    public void start() {
        inventoryInit();

        this.shootCooldown = SHOOT_COOLDOWN;
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.wolf.getPathfindingPenalty(PathNodeType.WATER);
        this.wolf.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.teleportCooldown = TP_COOLDOWN;
        this.wolf.setSitting(false);

        usingPotion = nextPotion();

        if (usingPotion.first.isEmpty()) return;

        this.wolf.equipStack(EquipmentSlot.MAINHAND, usingPotion.first);

        shootCooldown = SHOOT_COOLDOWN;
    }

    public void stop() {
        shootCooldown = SHOOT_COOLDOWN;
        this.owner = null;
        this.navigation.stop();
        this.wolf.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
        teleportCooldown = TP_COOLDOWN;
        this.wolf.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        inventoryInit();
    }

    public void tick() {
        if (!this.wolf.getWorld().isClient &&
                this.wolf.isAlive() &&
                this.wolf.canMoveVoluntarily()) {
            this.wolf.getLookControl().lookAt(this.owner, 10.0F, (float) this.wolf.getMaxLookPitchChange());

            final Pair<ItemStack, RegistryEntry<Potion>> itemStack = usingPotion;
            shootCooldown--;
//            if (itemStack.first.isEmpty()) {
//                shootCooldown = 0;
//                return;
//            } else {
//                shootCooldown--;
//            }

            if (WolfInventoryHelper.hasFittingLifesavingEffect(this.owner, inventoryContents)) return;
            //System.out.println("A1");
            //if (WolfInventoryHelper.findLifesavingPotions(inventoryContents, this.owner).first.isEmpty()) return;

            if (this.teleportCooldown > 0) this.teleportCooldown--;

            if (--this.updateCountdownTicks <= 0) {
                this.updateCountdownTicks = this.getTickCount(10);
                if (teleportCooldown <= 0) {
                    this.wolf.tryTeleportToOwner();
                    this.teleportCooldown = wolf.getRandom().nextBetween(TP_COOLDOWN / 2, TP_COOLDOWN + TP_COOLDOWN / 2);
                } else {

                    this.navigation.startMovingTo(this.owner, this.speed);
                }

            }

            if (!itemStack.first.isEmpty()) {
                this.wolf.equipStack(EquipmentSlot.MAINHAND, itemStack.first);

                if (this.wolf.squaredDistanceTo(this.owner) <= (double) (this.maxDistance * this.maxDistance) &&
                        this.wolf.canSee(this.owner) &&
                        this.wolf.getEquippedStack(EquipmentSlot.MAINHAND) == itemStack.first) {
                    shoot(itemStack);
                    this.wolf.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    //shootCooldown = 0;
                }
            }
        }
    }

    private Pair<ItemStack, RegistryEntry<Potion>> nextPotion() {
        //Pair<ItemStack, RegistryEntry<Potion>> itemStack = this.wolf.getEquippedStack(EquipmentSlot.MAINHAND);

        //if (!itemStack.first.isEmpty()) return itemStack;

        final Pair<ItemStack, RegistryEntry<Potion>> itemStack = WolfInventoryHelper.findLifesavingPotions(inventoryContents, this.owner);
        this.wolf.equipStack(EquipmentSlot.MAINHAND, itemStack.first);

        return itemStack;
    }

    public void shoot(final Pair<ItemStack, RegistryEntry<Potion>> itemStack) {
        Vec3d vec3d = this.owner.getVelocity();
        double d = this.owner.getX() + vec3d.x - this.wolf.getX();
        double e = this.owner.getEyeY() - 1.100000023841858 - this.wolf.getY();
        double f = this.owner.getZ() + vec3d.z - this.wolf.getZ();
        double g = Math.sqrt(d * d + f * f);

        RegistryEntry<Potion> registryEntry = itemStack.second;//Potions.FIRE_RESISTANCE;

        PotionEntity potionEntity = new PotionEntity(this.wolf.getWorld(), this.wolf);
        potionEntity.setItem(PotionContentsComponent.createStack(Items.SPLASH_POTION, registryEntry));
        potionEntity.setPitch(potionEntity.getPitch() - -20.0F);
        potionEntity.setVelocity(d, e + g * 0.2, f, 0.75F, 0F);
        this.wolf.getWorld().playSound(null, this.wolf.getX(), this.wolf.getY(), this.wolf.getZ(), SoundEvents.ENTITY_SPLASH_POTION_THROW, this.wolf.getSoundCategory(), 1.0F, 0.4F + this.wolf.getRandom().nextFloat() * 0.4F);

        this.wolf.getWorld().spawnEntity(potionEntity);

        itemStack.first.decrement(1);
        ItemStack itemStack2 = itemStack.first.finishUsing(this.wolf.getWorld(), this.wolf);
        if (!itemStack2.isEmpty()) {
            this.wolf.equipStack(EquipmentSlot.MAINHAND, itemStack2);
        }
    }
}
