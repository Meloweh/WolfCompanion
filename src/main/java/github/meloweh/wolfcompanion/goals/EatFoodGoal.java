package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EatFoodGoal extends Goal implements InventoryChangedListener {
    private final WolfEntity entity;
    private final WolfEntityProvider armoredWolf;

    private final List<ItemStack> inventoryContents;
    private ItemStack eatingFood = ItemStack.EMPTY;

    private int eatCooldown;
    private int foodEatTime, eatingTime;
    private boolean hasHealedSinceLastReset;

    private static final float DEFAULT_EAT_SECONDS = 1.6F;

    public EatFoodGoal(@NotNull WolfEntity wolf) {
        this.entity = wolf;
        this.armoredWolf = (WolfEntityProvider) wolf;
        this.inventoryContents = new ArrayList<>();
    }

    /*
    @Override
    public boolean canStart() {
        return this.wolfProvider.hasChestEquipped() && this.entity.hurtTime == 0;
    }

    @Override
    public void onInventoryChanged(Inventory sender) {

    }*/

    private void inventoryInit() {
        armoredWolf.getInventory().removeListener(this);
        armoredWolf.getInventory().addListener(this);
        refreshInventoryContents(armoredWolf.getInventory());
    }


    @Override
    public boolean canStart() {

        final boolean wouldStart = !this.entity.isInvulnerable()
                && this.entity.hurtTime == 0 && !this.entity.getWorld().isClient;

        if (!this.armoredWolf.hasChestEquipped() && this.entity.getEquippedStack(EquipmentSlot.MAINHAND).contains(DataComponentTypes.FOOD)) {
            return wouldStart;
        }

        if (this.armoredWolf.hasChestEquipped()) {
            if (!this.entity.getEquippedStack(EquipmentSlot.MAINHAND).contains(DataComponentTypes.FOOD)) {
                this.entity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
            return wouldStart && PickUpFoodGoal.playerFoodEnough(this.entity); // && this.entity.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }

        return false;
    }

    @Override
    public void start() {
        if (eatingTime > 0 || this.entity.getTarget() != null || this.entity.getWorld().isClient) return;

        ItemStack mostEfficientFood = this.armoredWolf.hasChestEquipped() ? findFood() : this.entity.getEquippedStack(EquipmentSlot.MAINHAND);

        if (mostEfficientFood.isEmpty()) return;

        final FoodComponent foodComponent = mostEfficientFood.get(DataComponentTypes.FOOD);

        if (foodComponent == null) return;

        float damageAmount = (this.entity.getMaxHealth() - this.entity.getHealth());

        if (damageAmount < 1.0F || damageAmount < foodComponent.nutrition()) {
            if (!this.armoredWolf.hasChestEquipped()) {
                armoredWolf.spit__(this.entity.getEquippedStack(EquipmentSlot.MAINHAND));
            }
            return;
        }

        this.eatingFood = mostEfficientFood;
        foodEatTime = (int) DEFAULT_EAT_SECONDS * 20;
        eatingTime = foodEatTime / 2 + entity.getRandom().nextInt(foodEatTime);
        this.entity.equipStack(EquipmentSlot.MAINHAND, this.eatingFood);
    }

    @Override
    public boolean shouldContinue() {
        return eatingTime > 0;
    }

    public SoundEvent getEatSound(ItemStack stack) {
        return SoundEvents.ENTITY_FOX_EAT;
    }

    @Override
    public void tick() {
        if (!this.entity.getWorld().isClient &&
                this.entity.isAlive() &&
                this.entity.canMoveVoluntarily()) {
            if (!this.eatingFood.isEmpty()) {
//                if (this.armoredWolf.hasChestEquipped() && !this.inventoryContents.contains(this.eatingFood)) {
//                    stop();
//                    return;
//                }

                ItemStack itemStack = this.eatingFood;
                if (!itemStack.isEmpty()) {
                    this.eatingTime--;
                } else {
                    this.eatingTime = 0;
                    return;
                }

                if (this.eatingTime == 0) {
                    final FoodComponent foodComponent = itemStack.get(DataComponentTypes.FOOD);
                    if (foodComponent == null) {
                        this.eatingTime++;
                        return;
                    }
                    this.entity.heal(foodComponent.nutrition());
                    //itemStack.decrement(1);
                    ItemStack itemStack2 = itemStack.finishUsing(this.entity.getWorld(), this.entity);
                    this.entity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    this.eatingTime = -1;
                } else if (this.eatingTime > 0) {
                    if (this.eatingTime % 2 == 0 && this.entity.getRandom().nextFloat() < 0.5F) {
                        this.entity.playSound(this.getEatSound(itemStack), 1.0F, 1.0F);
                        this.entity.getWorld().sendEntityStatus(this.entity, EntityStatuses.CREATE_EATING_PARTICLES);
                    }

                }
            } else {
                this.eatingTime = 0;
            }
        }
    }


    @Override
    public void stop() {
        foodEatTime = 0;
        this.eatingTime = 0;
        hasHealedSinceLastReset = false;
        eatingFood = ItemStack.EMPTY;
        this.entity.equipStack(EquipmentSlot.MAINHAND, this.eatingFood);
        inventoryInit();
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        this.refreshInventoryContents(sender);
    }

    private void refreshInventoryContents(Inventory invBasic) {
        this.inventoryContents.clear();
        for(int slotIndex = 1;
            slotIndex < invBasic.size();
            ++slotIndex) {
            this.inventoryContents.add(invBasic.getStack(slotIndex));
        }
    }

    private float getMissingHealth() {
        return this.entity.getMaxHealth() - this.entity.getHealth();
    }

    private boolean canEat(final ItemStack itemStack) {
        return !itemStack.isEmpty() && entity.isBreedingItem(itemStack) && itemStack.contains(DataComponentTypes.FOOD);
    }

    @NotNull
    public ItemStack findFood() {
        final float healthDiff = this.entity.getMaxHealth() - this.entity.getHealth();
        return this.inventoryContents.stream()
                .filter(this::canEat)
                .min(Comparator.comparing(itemStack
                        -> Math.abs(healthDiff - itemStack.get(DataComponentTypes.FOOD).nutrition())))
                .orElse(ItemStack.EMPTY);
    }
}
