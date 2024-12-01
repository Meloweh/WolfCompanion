package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.util.WolfInventoryProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class PickUpFoodGoal extends Goal implements InventoryChangedListener {
    final WolfEntity wolf;
    final WolfEntityProvider provider;
    int scanCooldown;
    final int SCAN_COOLDOWN = 20 * 10;
    final WolfInventoryProvider intenvory;

    final Predicate<ItemEntity> PICKABLE_DROP_FILTER = (item)
            -> !item.cannotPickup() && item.isAlive() && WolfInventoryProvider.canEat(item.getStack());

    public PickUpFoodGoal(final WolfEntity wolf) {
        this.setControls(EnumSet.of(Control.MOVE));
        this.wolf = wolf;
        this.provider = (WolfEntityProvider) wolf;
        this.scanCooldown = 0;
        this.intenvory = new WolfInventoryProvider(this.wolf);
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        this.intenvory.refreshInventoryContents(sender);
    }

    private boolean wantsToPickupItem() {
        // if inv has space and food is <= 8 or only rotten meat and if config allowes to
        if (!this.wolf.isSitting() && this.intenvory.hasSpace() && (this.intenvory.getFoodCount() <= 8
                || this.intenvory.onlyFood(Items.ROTTEN_FLESH))) {
            return true;
        }
        return false;
    }

    private List<ItemEntity> findPickups() {
        return wolf.getWorld().getEntitiesByClass(ItemEntity.class, wolf.getBoundingBox()
                .expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);

    }

    @Override
    public boolean canStart() {
        if (wolf.getTarget() == null && wolf.getAttacker() == null) {
            this.intenvory.inventoryInit(this);
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
    }

//    protected void loot(ItemEntity item) {
//        ItemStack itemStack = item.getStack();
//        if (this.intenvory.canEat_(itemStack) && this.intenvory.hasSpace()) {
//            int i = itemStack.getCount();
//
//            this.wolf.sendPickup(item, itemStack.getCount());
//            item.discard();
//        }
//    }

    @Override
    public void tick() {
        start();
    }

    @Override
    public boolean shouldContinue() {
        return wantsToPickupItem();
    }

    @Override
    public void start() {
        List<ItemEntity> list = findPickups();
        if (!list.isEmpty()) {
            wolf.getNavigation().startMovingTo(list.getFirst(), 1.2000000476837158);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.intenvory.inventoryInit(this);
    }
}
