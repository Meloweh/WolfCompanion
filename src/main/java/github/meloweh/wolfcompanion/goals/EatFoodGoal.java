package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EatFoodGoal extends Goal {
    private final Wolf entity;
    private final WolfEntityProvider armoredWolf;

    private final List<ItemStack> inventoryContents;
    private ItemStack eatingFood = ItemStack.EMPTY;

    private int eatCooldown;
    private int foodEatTime, eatingTime;
    private boolean hasHealedSinceLastReset;

    private static final float DEFAULT_EAT_SECONDS = 1.6F;

    public EatFoodGoal(@NotNull Wolf wolf) {
        this.entity = wolf;
        this.armoredWolf = (WolfEntityProvider) wolf;
        this.inventoryContents = new ArrayList<>();
    }

    private void inventoryInit() {
        refreshInventoryContents(armoredWolf.getInventory());
    }


    @Override
    public boolean canUse() {

        final boolean wouldStart = !this.entity.isInvulnerable()
                && this.entity.hurtTime == 0 && !this.entity.level().isClientSide();

        if (!this.armoredWolf.hasChestEquipped() && this.entity.getItemBySlot(EquipmentSlot.MAINHAND).isEdible()) {
            return wouldStart;
        }

        if (this.armoredWolf.hasChestEquipped()) {
            if (!this.entity.getItemBySlot(EquipmentSlot.MAINHAND).isEdible()) {
                this.entity.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
            return wouldStart && PickUpFoodGoal.playerFoodEnough(this.entity); // && this.entity.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }

        return false;
    }

    @Override
    public void start() {
        if (eatingTime > 0 || this.entity.getTarget() != null || this.entity.level().isClientSide()) return;

        ItemStack mostEfficientFood = this.armoredWolf.hasChestEquipped() ? findFood() : this.entity.getItemBySlot(EquipmentSlot.MAINHAND);

        if (mostEfficientFood.isEmpty()) return;

        final FoodProperties foodComponent = mostEfficientFood.getItem().getFoodProperties();

        if (foodComponent == null) return;

        float damageAmount = (this.entity.getMaxHealth() - this.entity.getHealth());

        if (damageAmount < 1.0F || damageAmount < foodComponent.getNutrition()) {
            if (!this.armoredWolf.hasChestEquipped()) {
                armoredWolf.spit__(this.entity.getItemBySlot(EquipmentSlot.MAINHAND));
            }
            return;
        }

        this.eatingFood = mostEfficientFood;
        foodEatTime = (int) DEFAULT_EAT_SECONDS * 20;
        eatingTime = foodEatTime / 2 + entity.getRandom().nextInt(foodEatTime);
        this.entity.setItemSlot(EquipmentSlot.MAINHAND, this.eatingFood);
    }

    @Override
    public boolean canContinueToUse() {
        return eatingTime > 0;
    }

    public SoundEvent getEatSound(ItemStack stack) {
        return SoundEvents.FOX_EAT;
    }

    @Override
    public void tick() {
        if (!this.entity.level().isClientSide() &&
                this.entity.isAlive() &&
                this.entity.isEffectiveAi()) {
            if (!this.eatingFood.isEmpty()) {
                ItemStack itemStack = this.eatingFood;
                if (!itemStack.isEmpty()) {
                    this.eatingTime--;
                } else {
                    this.eatingTime = 0;
                    return;
                }

                if (this.eatingTime == 0) {
                    final FoodProperties foodComponent = itemStack.getItem().getFoodProperties();
                    if (foodComponent == null) {
                        this.eatingTime++;
                        return;
                    }
                    this.entity.heal(foodComponent.getNutrition());
                    ItemStack itemStack2 = itemStack.finishUsingItem(this.entity.level(), this.entity);
                    this.entity.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    this.eatingTime = -1;
                } else if (this.eatingTime > 0) {
                    if (this.eatingTime % 2 == 0 && this.entity.getRandom().nextFloat() < 0.5F) {
                        this.entity.playSound(this.getEatSound(itemStack), 1.0F, 1.0F);
                        this.entity.level().broadcastEntityEvent(this.entity, EntityEvent.FOX_EAT);
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
        this.entity.setItemSlot(EquipmentSlot.MAINHAND, this.eatingFood);
        inventoryInit();
    }

    private void refreshInventoryContents(Container invBasic) {
        this.inventoryContents.clear();
        for(int slotIndex = 1;
            slotIndex < invBasic.getContainerSize();
            ++slotIndex) {
            this.inventoryContents.add(invBasic.getItem(slotIndex));
        }
    }

    private float getMissingHealth() {
        return this.entity.getMaxHealth() - this.entity.getHealth();
    }

    private boolean canEat(final ItemStack itemStack) {
        return !itemStack.isEmpty() && entity.isFood(itemStack) && itemStack.isEdible();
    }

    @NotNull
    public ItemStack findFood() {
        final float healthDiff = this.entity.getMaxHealth() - this.entity.getHealth();
        return this.inventoryContents.stream()
                .filter(this::canEat)
                .min(Comparator.comparing(itemStack
                        -> Math.abs(healthDiff - itemStack.getItem().getFoodProperties().getNutrition())))
                .orElse(ItemStack.EMPTY);
    }
}
