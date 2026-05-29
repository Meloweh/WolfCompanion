package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.config.WolfCompanionConfig;
import github.meloweh.wolfcompanion.util.WolfInventoryProvider;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gamerules.GameRules;

public class PickUpFoodGoal extends Goal {
    final Wolf wolf;
    final WolfEntityProvider provider;
    int scanCooldown;
    //final int SCAN_COOLDOWN = 20 * 10;
    final WolfInventoryProvider inventory;

    final Predicate<ItemEntity> PICKABLE_DROP_FILTER = (item)
            -> !item.hasPickUpDelay() && item.isAlive() && WolfInventoryProvider.canEat(item.getItem());

    public PickUpFoodGoal(final Wolf wolf) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.wolf = wolf;
        this.provider = (WolfEntityProvider) wolf;
        this.scanCooldown = 0;
        this.inventory = new WolfInventoryProvider(this.wolf);
    }

    public static boolean playerFoodEnough(final Wolf wolf) {
        if (!WolfCompanionConfig.current().shouldCarePlayerFood) return true;
        if (wolf.getOwner() != null) {
            final Inventory inv = ((Player)wolf.getOwner()).getInventory();
            final List<ItemStack> ic = new ArrayList<>();
            for(int slotIndex = 0;
                slotIndex < inv.getContainerSize();
                ++slotIndex) {
                ic.add(inv.getItem(slotIndex));
            }
            return ic.stream().filter(WolfInventoryProvider::canPlayerEat).mapToInt(ItemStack::getCount).sum() >= WolfCompanionConfig.current().requiredPlayerFood;
        }
        return true;
    }

    private boolean nakedAndHungry() {
        return !this.provider.hasChestEquipped()
                && this.wolf.getHealth() <= this.wolf.getMaxHealth() * 0.8f
                && this.wolf.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
    }

    private boolean wantsToPickupItem() {
        if (!WolfCompanionConfig.current().canPickupFood) return false;
        if (provider.hasChestEquipped()) {
            this.inventory.inventoryInit();

            if (this.inventory.hasSpace() && (this.inventory.getFoodCount() <= WolfCompanionConfig.current().maxPickupFood
                    || WolfCompanionConfig.current().pickAllRottenFlesh && this.inventory.onlyFood(Items.ROTTEN_FLESH))) {
                return true;
            }
        }
        return nakedAndHungry() && playerFoodEnough(this.wolf);
    }

    private List<ItemEntity> findPickups() {
        return wolf.level().getEntitiesOfClass(ItemEntity.class, wolf.getBoundingBox()
                .inflate(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
    }

    @Override
    public boolean canUse() {
        //if (!wolf.getEntityWorld().isClient() && wolf.isAlive() && !wolf.isDead() && ((ServerWorld)wolf.getEntityWorld()).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
        final boolean can_mob_grief = ((ServerLevel)wolf.level()).getGameRules().get(GameRules.MOB_GRIEFING);
        if (!wolf.level().isClientSide() && wolf.isAlive() && !wolf.isDeadOrDying() && can_mob_grief) {
            if (wolf.isTame()
                    && !wolf.level().isClientSide()
                    && !wolf.isOrderedToSit()
                    && wolf.getTarget() == null
                    && wolf.getLastHurtByMob() == null) {
                if (!wantsToPickupItem()) {
                    return false;
                } else if (wolf.getRandom().nextInt(reducedTickDelay(10)) != 0) {
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
    public boolean canContinueToUse() {
        return wantsToPickupItem();
    }

    private void navigate() {
        List<ItemEntity> list = findPickups();
        if (!list.isEmpty()) {
            final ItemEntity first = list.getFirst();
            provider.setTargetPickup__(first);
            wolf.getNavigation().moveTo(first, 1.2000000476837158);
        }
    }

    @Override
    public void start() {
        navigate();
    }

    @Override
    public void stop() {
        super.stop();
        if (provider.hasChestEquipped()) this.inventory.inventoryInit();
        provider.setTargetPickup__(null);
    }
}
