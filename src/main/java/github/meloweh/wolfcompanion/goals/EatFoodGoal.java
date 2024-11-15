package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.network.WolfEatS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

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
        return !this.entity.isInvulnerable()
                && this.entity.hurtTime == 0
                && this.armoredWolf.hasChestEquipped();
    }

    @Override
    public void start() {
        if (eatingTime > 0 || this.entity.getTarget() != null) return;

        ItemStack mostEfficientFood = getMostEfficientFood();

        if (mostEfficientFood.isEmpty()) return;

        final FoodComponent foodComponent = mostEfficientFood.get(DataComponentTypes.FOOD);

        if (foodComponent == null) return;

        float damageAmount = (this.entity.getMaxHealth() - this.entity.getHealth());

        if (damageAmount < 1.0F || damageAmount < foodComponent.nutrition()) return;

        this.eatingFood = mostEfficientFood;
        foodEatTime = foodComponent.getEatTicks();
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

    private boolean canEat(ItemStack stack) {
        return stack.contains(DataComponentTypes.FOOD);
    }

    private ItemStack nextFood() {
        ItemStack itemStack = this.entity.getEquippedStack(EquipmentSlot.MAINHAND);

        if (!itemStack.isEmpty()) return itemStack;

        itemStack = getMostEfficientFood();
        this.entity.equipStack(EquipmentSlot.MAINHAND, itemStack);

        return itemStack;
    }

    @Override
    public void tick() {
        if (!this.entity.getWorld().isClient &&
                this.entity.isAlive() &&
                this.entity.canMoveVoluntarily() &&
                !this.eatingFood.isEmpty()) {

            ItemStack itemStack = nextFood();
            if (!itemStack.isEmpty()) {
                this.eatingTime--;
            } else {
                this.eatingTime = 0;
                return;
            }

            if (this.eatingTime <= 0) {
                final FoodComponent foodComponent = itemStack.get(DataComponentTypes.FOOD);
                this.entity.heal(foodComponent.nutrition());
                itemStack.decrement(1);
                ItemStack itemStack2 = itemStack.finishUsing(this.entity.getWorld(), this.entity);
                if (!itemStack2.isEmpty()) {
                    this.entity.equipStack(EquipmentSlot.MAINHAND, itemStack2);
                }

                this.eatingTime = 0;
            } else if (this.eatingTime > 0) {
                if (this.eatingTime % 2 == 0 && this.entity.getRandom().nextFloat() < 0.5F) {
                    this.entity.playSound(this.getEatSound(itemStack), 1.0F, 1.0F);
                    this.entity.getWorld().sendEntityStatus(this.entity, EntityStatuses.CREATE_EATING_PARTICLES);
                }

            }
        }
//        this.eatCooldown--;
//        this.eatingTime++;
//        ItemStack itemStack = this.entity.getEquippedStack(EquipmentSlot.MAINHAND);
//        if (itemStack.contains(DataComponentTypes.FOOD)) {
//            if (this.eatingTime > 600) {
//                ItemStack itemStack2 = itemStack.finishUsing(this.entity.getWorld(), this.entity);
//                if (!itemStack2.isEmpty()) {
//                    this.entity.equipStack(EquipmentSlot.MAINHAND, itemStack2);
//                }
//
//                this.eatingTime = 0;
//            } else if (this.eatingTime > 560 && this.entity.getRandom().nextFloat() < 0.1F) {
//                this.entity.playSound(this.entity.getEatSound(itemStack), 1.0F, 1.0F);
//                this.entity.getWorld().sendEntityStatus(this.entity, EntityStatuses.CREATE_EATING_PARTICLES);
//            }
//        }
        
        /*--eatCooldown;
        if (!eatingFood.isEmpty() && eatingFood.getComponents().contains(DataComponentTypes.FOOD)) {
            if (foodEatTime > 0) {
                if (--foodEatTime % 4 == 0) {
//                    for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) entity.getWorld(), entity.getBlockPos())) {
//                        ServerPlayNetworking.send(player, new WolfEatS2CPayload(entity.getId(), eatingFood));
//                    }
                    this.entity.sen
                    this.entity.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5F, (this.entity.getRandom().nextFloat() - this.entity.getRandom().nextFloat()) * 0.2F + 1);
                }
            } else if (!hasHealedSinceLastReset) {
                CreatureFoodStats foodStats = this.armoredWolf.getFoodStats();
                boolean creatureFoodStatsEnabled = this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED;
                hasHealedSinceLastReset = true;
                TargetPoint targetPoint = new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 80);
                this.connection.sendToAllAround(new WolfHealMessage(entity.getEntityId()), targetPoint);

                if (creatureFoodStatsEnabled) {
                    foodStats.addStats((ItemFood)this.eatingFood.getItem(), this.eatingFood);
                } else {
                    this.entity.heal((float) ((ItemFood) eatingFood.getItem()).getHealAmount(eatingFood));
                }
                this.eatingFood.shrink(1);
            }
        }*/
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
            slotIndex < 16;
            ++slotIndex) {
            this.inventoryContents.add(invBasic.getStack(slotIndex));
        }
    }

    private float getMissingHealth() {
        return this.entity.getMaxHealth() - this.entity.getHealth();
    }

    @NotNull
    private ItemStack getMostEfficientFood() {
        final float healthDiff = this.entity.getMaxHealth() - this.entity.getHealth();

        return this.inventoryContents.stream()
                .filter(itemStack -> !itemStack.isEmpty() && entity.isBreedingItem(itemStack) && itemStack.contains(DataComponentTypes.FOOD))
                .min(Comparator.comparing(itemStack -> {
                    FoodComponent food = itemStack.get(DataComponentTypes.FOOD);
                    return Math.abs(healthDiff - food.nutrition());
                }))
                .orElse(ItemStack.EMPTY);
    }
}
