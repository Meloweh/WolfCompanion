package github.meloweh.wolfcompanion.goals;

import com.ibm.icu.impl.Pair;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.util.WolfInventoryHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RescueSelfFromLavaGoal extends Goal implements InventoryChangedListener {
    private final TameableEntity wolf;
    @Nullable
    private LivingEntity owner;
    private final EntityNavigation navigation;
    private final WolfEntityProvider armoredWolf;
    private final List<ItemStack> inventoryContents;
    private int shootCooldown, lavaTicks;
    private static final int SHOOT_COOLDOWN = 20, LAVA_TICKS = 10;

    public RescueSelfFromLavaGoal(WolfEntity wolf) {
        this.wolf = wolf;
        this.navigation = wolf.getNavigation();
        this.shootCooldown = 0;
        this.armoredWolf = (WolfEntityProvider) wolf;
        this.inventoryContents = new ArrayList<>();
        this.lavaTicks = 0;
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
        if (this.wolf.getEquippedStack(EquipmentSlot.MAINHAND).contains(DataComponentTypes.POTION_CONTENTS)) {
            this.wolf.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        if (!this.wolf.isTamed()) return false;
        if (!this.armoredWolf.hasChestEquipped()) return false;
        if (WolfInventoryHelper.hasFittingLifesavingEffect(this.wolf)) return false;
        if (this.wolf.getWorld().isClient()) return false;
        return true;
    }

    public boolean shouldContinue() {
        if (WolfInventoryHelper.hasFittingLifesavingEffect(this.wolf, inventoryContents)) return false;
        return true;
    }

    public void start() {
        this.wolf.setSitting(false);
        shootCooldown = 0;
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        shootCooldown = 0;
        lavaTicks = 0;
        this.wolf.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        inventoryInit();
    }

    public void tick() {
        Pair<ItemStack, RegistryEntry<Potion>> itemStack = nextPotion();
        this.wolf.equipStack(EquipmentSlot.MAINHAND, itemStack.first);

        if (this.wolf.getWorld().isClient()) return;
        if (Math.abs(this.wolf.getVelocity().getY()) > 0.3f) return;
        if (WolfInventoryHelper.hasFittingLifesavingEffect(this.wolf, inventoryContents)) return;

        if (++lavaTicks < LAVA_TICKS) return;
        if (!this.wolf.isInLava()) lavaTicks = 0;

        if (this.shootCooldown <= 0) {
            //shoot(itemStack);
            applySplashPotionEffect(itemStack.second);

            itemStack.first.decrement(1);
            ItemStack itemStack2 = itemStack.first.finishUsing(this.wolf.getWorld(), this.wolf);
            if (!itemStack2.isEmpty()) {
                this.wolf.equipStack(EquipmentSlot.MAINHAND, itemStack2);
            }
            shootCooldown = SHOOT_COOLDOWN;
        } else {
            this.shootCooldown--;
        }
    }

    private Pair<ItemStack, RegistryEntry<Potion>> nextPotion() {
        //ItemStack itemStack = this.wolf.getEquippedStack(EquipmentSlot.MAINHAND);

        //if (!itemStack.isEmpty()) return itemStack;

        final Pair<ItemStack, RegistryEntry<Potion>> itemStack = WolfInventoryHelper.findLifesavingPotions(inventoryContents, this.wolf);
        this.wolf.equipStack(EquipmentSlot.MAINHAND, itemStack.first);

        return itemStack;
    }

    public void applySplashPotionEffect(RegistryEntry<Potion> second) {
        //RegistryEntry<Potion> registryEntry = Potions.FIRE_RESISTANCE;

        // Get the world and the wolf's position
        World world = this.wolf.getWorld();
        double x = this.wolf.getX();
        double y = this.wolf.getY();
        double z = this.wolf.getZ();

        // Create a splash effect on the wolf
        AreaEffectCloudEntity effectCloud = new AreaEffectCloudEntity(world, x, y + 0.5f, z);
        effectCloud.setOwner(this.wolf); // Set the wolf as the source
        effectCloud.setRadius(1F); // Set splash radius
        PotionContentsComponent potionContentsComponent = new PotionContentsComponent(second);
        effectCloud.setPotionContents(potionContentsComponent); // Assign the potion effects (e.g., fire resistance)
        effectCloud.setDuration(15); // Short duration since it's a splash
        effectCloud.setWaitTime(0); // Apply immediately

        // Play the splash sound effect
        world.playSound(null, x, y, z, SoundEvents.ENTITY_SPLASH_POTION_BREAK, this.wolf.getSoundCategory(), 1.0F, 0.4F + this.wolf.getRandom().nextFloat() * 0.4F);

        // Spawn the area effect cloud to apply effects
        world.spawnEntity(effectCloud);
    }

}
