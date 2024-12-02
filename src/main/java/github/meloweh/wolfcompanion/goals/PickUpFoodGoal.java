package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.util.WolfInventoryProvider;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.GameRules;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class PickUpFoodGoal extends Goal implements InventoryChangedListener {
    final WolfEntity wolf;
    final WolfEntityProvider provider;
    int scanCooldown;
    //final int SCAN_COOLDOWN = 20 * 10;
    final WolfInventoryProvider inventory;

    final Predicate<ItemEntity> PICKABLE_DROP_FILTER = (item)
            -> !item.cannotPickup() && item.isAlive() && WolfInventoryProvider.canEat(item.getStack());

    public PickUpFoodGoal(final WolfEntity wolf) {
        this.setControls(EnumSet.of(Control.MOVE));
        this.wolf = wolf;
        this.provider = (WolfEntityProvider) wolf;
        this.scanCooldown = 0;
        this.inventory = new WolfInventoryProvider(this.wolf);
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        if (provider.hasChestEquipped()) this.inventory.refreshInventoryContents(sender);
    }

    public static boolean playerFoodEnough(final WolfEntity wolf) {
        if (wolf.getOwner() != null) {
            final PlayerInventory inv = ((PlayerEntity)wolf.getOwner()).getInventory();
            final List<ItemStack> ic = new ArrayList<>();
            for(int slotIndex = 0;
                slotIndex < inv.size();
                ++slotIndex) {
                ic.add(inv.getStack(slotIndex));
            }
            return ic.stream().filter(WolfInventoryProvider::canPlayerEat).mapToInt(ItemStack::getCount).sum() >= 10;
        }
        return true;
    }

    private boolean nakedAndHungry() {
        return !this.provider.hasChestEquipped()
                && this.wolf.getHealth() <= this.wolf.getMaxHealth() * 0.8f
                && this.wolf.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
    }

    private boolean wantsToPickupItem() {
        if (provider.hasChestEquipped()) {
            this.inventory.inventoryInit(this);

            if (this.inventory.hasSpace() && (this.inventory.getFoodCount() <= 8
                    || this.inventory.onlyFood(Items.ROTTEN_FLESH))) {
                return true;
            }
        }
        return nakedAndHungry() && playerFoodEnough(this.wolf);
    }

    private List<ItemEntity> findPickups() {
        return wolf.getWorld().getEntitiesByClass(ItemEntity.class, wolf.getBoundingBox()
                .expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
    }

    @Override
    public boolean canStart() {
        if (!wolf.getWorld().isClient && wolf.isAlive() && !wolf.isDead() && wolf.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            if (wolf.isTamed()
                    && !wolf.getWorld().isClient
                    && !wolf.isSitting()
                    && wolf.getTarget() == null
                    && wolf.getAttacker() == null) {
                if (!wantsToPickupItem()) {
                    return false;
                } else if (wolf.getRandom().nextInt(toGoalTicks(10)) != 0) {
                    return false;
                } else {
                    List<ItemEntity> list = findPickups();
                    return !list.isEmpty();
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void tick() {
        navigate();
    }

    @Override
    public boolean shouldContinue() {
        return wantsToPickupItem();
    }

    private void navigate() {
        List<ItemEntity> list = findPickups();
        if (!list.isEmpty()) {
            final ItemEntity first = list.getFirst();
            provider.setTargetPickup__(first);
            wolf.getNavigation().startMovingTo(first, 1.2000000476837158);
        }
    }

    @Override
    public void start() {
        navigate();
    }

    @Override
    public void stop() {
        super.stop();
        if (provider.hasChestEquipped()) this.inventory.inventoryInit(this);
        provider.setTargetPickup__(null);
    }
}
