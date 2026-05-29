package github.meloweh.wolfcompanion.goals;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.util.Pair;
import github.meloweh.wolfcompanion.util.WolfInventoryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;

public class RescueSelfFromLavaGoal extends Goal {
    private final TamableAnimal wolf;
    @Nullable
    private final PathNavigation navigation;
    private final WolfEntityProvider armoredWolf;
    private final List<ItemStack> inventoryContents;
    private int shootCooldown, lavaTicks;
    private static final int SHOOT_COOLDOWN = 15, LAVA_TICKS = 10;
    private Pair<ItemStack, Holder<Potion>> usingPotion = Pair.of(ItemStack.EMPTY, Potions.AWKWARD);

    public RescueSelfFromLavaGoal(Wolf wolf) {
        this.wolf = wolf;
        this.navigation = wolf.getNavigation();
        this.shootCooldown = 0;
        this.armoredWolf = (WolfEntityProvider) wolf;
        this.inventoryContents = new ArrayList<>();
        this.lavaTicks = 0;
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
        if (this.wolf.getItemBySlot(EquipmentSlot.MAINHAND).has(DataComponents.POTION_CONTENTS)) {
            this.wolf.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        final boolean wouldStart = this.wolf.isTame()
                && this.armoredWolf.hasChestEquipped();

        if (wouldStart) {
            refreshInventoryContents(armoredWolf.getInventory());
            if (WolfInventoryHelper.hasFittingLifesavingEffect(this.wolf, inventoryContents)) return false;
        }

        return wouldStart;
    }

    public boolean canContinueToUse() {
        return shootCooldown > 0;
    }

    public void start() {
        inventoryInit();

        this.shootCooldown = SHOOT_COOLDOWN;
        this.wolf.setPathfindingMalus(PathType.WATER, 0.0F);
        this.wolf.setOrderedToSit(false);

        usingPotion = nextPotion();

        if (usingPotion.first.isEmpty()) return;

        this.wolf.setItemSlot(EquipmentSlot.MAINHAND, usingPotion.first);

        shootCooldown = SHOOT_COOLDOWN;
    }

    public void stop() {
        shootCooldown = SHOOT_COOLDOWN;
        this.wolf.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        inventoryInit();
    }

    public void tick() {
        if (!this.wolf.level().isClientSide() &&
                this.wolf.isAlive() &&
                this.wolf.canSimulateMovement()) {

            Pair<ItemStack, Holder<Potion>> itemStack = usingPotion;
            shootCooldown--;
            if (WolfInventoryHelper.hasFittingLifesavingEffect(this.wolf, inventoryContents)) return;

            if (!itemStack.first.isEmpty()) {
                this.wolf.setItemSlot(EquipmentSlot.MAINHAND, itemStack.first);
                if (++lavaTicks < LAVA_TICKS) return;
                if (!this.wolf.isInLava()) lavaTicks = 0;
                if (Math.abs(this.wolf.getDeltaMovement().y()) > 0.3f) return;

                if (this.wolf.getItemBySlot(EquipmentSlot.MAINHAND) == itemStack.first) {
                    applySplashPotionEffect(itemStack);
                    this.wolf.setItemSlot(EquipmentSlot.MAINHAND, nextPotion().first);
                }
            }
        }
    }

    private Pair<ItemStack, Holder<Potion>> nextPotion() {
        final Pair<ItemStack, Holder<Potion>> itemStack = WolfInventoryHelper.findLifesavingPotions(inventoryContents, this.wolf);
        this.wolf.setItemSlot(EquipmentSlot.MAINHAND, itemStack.first);

        return itemStack;
    }

    public void applySplashPotionEffect(final Pair<ItemStack, Holder<Potion>> itemStack) {
        Level world = this.wolf.level();
        double x = this.wolf.getX();
        double y = this.wolf.getY();
        double z = this.wolf.getZ();

        AreaEffectCloud effectCloud = new AreaEffectCloud(world, x, y + 0.5f, z);
        effectCloud.setOwner(this.wolf);
        effectCloud.setRadius(1F);
        PotionContents potionContentsComponent = new PotionContents(itemStack.second);
        effectCloud.setPotionContents(potionContentsComponent);
        effectCloud.setDuration(15);
        effectCloud.setWaitTime(0);

        world.playSound(null, x, y, z, SoundEvents.SPLASH_POTION_BREAK, this.wolf.getSoundSource(), 1.0F, 0.4F + this.wolf.getRandom().nextFloat() * 0.4F);

        world.addFreshEntity(effectCloud);

        itemStack.first.shrink(1);
        ItemStack itemStack2 = itemStack.first.finishUsingItem(this.wolf.level(), this.wolf);
        if (!itemStack2.isEmpty()) {
            this.wolf.setItemSlot(EquipmentSlot.MAINHAND, itemStack2);
        }
    }

}
