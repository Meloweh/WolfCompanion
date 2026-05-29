package github.meloweh.wolfcompanion.menu;

import github.meloweh.wolfcompanion.accessor.WolfEntityMixinProvider;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.registry.ModMenuTypes;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.shadow.ShadowArmorSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class WolfInventoryScreenHandler extends AbstractContainerMenu {
    private static final int WOLF_SLOTS = 16;
    private static final double WOLF_LOOKUP_RADIUS = 20.0;

    private final Wolf wolf;
    private final SimpleContainer wolfInventory;
    private Slot armorSlot;

    private static Wolf getWolfEntity(Inventory playerInventory, UuidPayload payload) {
        final Player player = playerInventory.player;
        final AABB area = new AABB(
                player.getX() - WOLF_LOOKUP_RADIUS,
                player.getY() - WOLF_LOOKUP_RADIUS,
                player.getZ() - WOLF_LOOKUP_RADIUS,
                player.getX() + WOLF_LOOKUP_RADIUS,
                player.getY() + WOLF_LOOKUP_RADIUS,
                player.getZ() + WOLF_LOOKUP_RADIUS
        );

        return player.level().getEntities(player, area).stream()
                .filter(Wolf.class::isInstance)
                .map(Wolf.class::cast)
                .filter(wolf -> wolf.getUUID().equals(payload.uuid()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Client does not have wolf."));
    }

    public WolfInventoryScreenHandler(int syncId, Inventory playerInventory, UuidPayload payload) {
        this(syncId, playerInventory, WolfInventoryScreenHandler.getWolfEntity(playerInventory, payload), payload.nbt());
    }

    public Wolf getWolf() {
        return wolf;
    }

    public SimpleContainer getWolfInventory() {
        return wolfInventory;
    }

    public WolfInventoryScreenHandler(int syncId, Inventory playerInventory, Wolf wolf, CompoundTag ignoredNbt) {
        super(ModMenuTypes.WOLF_INVENTORY, syncId);

        this.wolf = wolf;
        wolfInventory = ((WolfEntityMixinProvider)(wolf)).wolfcompanion_template_1_21_1$getItemsInventory();
        checkContainerSize(wolfInventory, WOLF_SLOTS);
        wolfInventory.startOpen(playerInventory.player);

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addWolfInventory(wolfInventory);
    }

    private void addPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 84 + (row * 18)));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 142));
        }
    }

    private void addWolfInventory(SimpleContainer inventory) {
        armorSlot = addSlot(new ShadowArmorSlot(inventory, wolf, EquipmentSlot.BODY, 0, 8, 18, null));
        final ItemStack armorStack = wolf.getItemBySlot(EquipmentSlot.BODY);
        armorSlot.setByPlayer(armorStack);

        if (((WolfEntityProvider)wolf).hasChestEquipped()) {
            for (int k = 0; k < WOLF_SLOTS / 5; k++) {
                for (int l = 0; l < WOLF_SLOTS / 3; l++) {
                    addSlot(new WolfBagInventorySlot(inventory, 1 + l + k * 5, 80 + l * 18, 18 + k * 18));
                }
            }
        }

    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = getSlot(slotIndex);
        if(slot != null && slot.hasItem()) {
            ItemStack inSlot = slot.getItem();
            newStack = inSlot.copy();

            if(slotIndex < WOLF_SLOTS) {
                if(!moveItemStackTo(inSlot, WOLF_SLOTS, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!moveItemStackTo(inSlot, 0, WOLF_SLOTS, false))
                return ItemStack.EMPTY;

            if(inSlot.isEmpty())
                slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }

        return newStack;
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        super.clicked(slotIndex, button, actionType, player);
        if (!armorSlot.getItem().isEmpty() && !wolf.isWearingBodyArmor()) {
            wolf.setBodyArmorItem(armorSlot.getItem());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
