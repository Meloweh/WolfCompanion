package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class PickUpFoodGoal {/* extends Goal {
        final WolfEntity wolf;
        final WolfEntityProvider provider;
        int scanCooldown;
        final int SCAN_COOLDOWN = 20 * 10;
        public PickUpFoodGoal(final WolfEntity wolf) {
            this.setControls(EnumSet.of(Control.MOVE));
            this.wolf = wolf;
            this.provider = (WolfEntityProvider) wolf;
            this.scanCooldown = 0;
        }

        private boolean wantsToPickupItem() {

        }

        public boolean canStart() {
            if (!wolf.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                return false;
            } else if (wolf.getTarget() == null && wolf.getAttacker() == null) {
                if (!wantsToPickupItem()) {
                    return false;
                } else if (wolf.getRandom().nextInt(toGoalTicks(10)) != 0) {
                    return false;
                } else {
                    List<ItemEntity> list = wolf.getWorld().getEntitiesByClass(ItemEntity.class, wolf.getBoundingBox().expand(8.0, 8.0, 8.0), FOOD);
                    return !list.isEmpty() && wolf.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
                }
            } else {
                return false;
            }
        }

        public void tick() {
            List<ItemEntity> list = wolf.getWorld().getEntitiesByClass(ItemEntity.class, wolf.getBoundingBox().expand(8.0, 8.0, 8.0), FOOD);
            ItemStack itemStack = wolf.getEquippedStack(EquipmentSlot.MAINHAND);
            if (itemStack.isEmpty() && !list.isEmpty()) {
                wolf.getNavigation().startMovingTo((Entity)list.get(0), 1.2000000476837158);
            }

        }

        public void start() {
            List<ItemEntity> list = wolf.getWorld().getEntitiesByClass(ItemEntity.class, wolf.getBoundingBox().expand(8.0, 8.0, 8.0), FOOFOOD);
            if (!list.isEmpty()) {
                wolf.getNavigation().startMovingTo((Entity)list.get(0), 1.2000000476837158);
            }
        }*/
    }
