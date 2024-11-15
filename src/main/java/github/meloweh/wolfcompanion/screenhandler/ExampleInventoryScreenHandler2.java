package github.meloweh.wolfcompanion.screenhandler;

import github.meloweh.wolfcompanion.accessor.WolfEntityMixinProvider;
import github.meloweh.wolfcompanion.init.ScreenHandlerTypeInit;
import github.meloweh.wolfcompanion.network.SampleS2CPayload;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.shadow.ShadowArmorSlot;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Optional;

public class ExampleInventoryScreenHandler2 extends ScreenHandler {
    private final WolfEntity wolf;
    private final ScreenHandlerContext context;
    private final SimpleInventory wolfInventory;
    private Slot armorSlot;

    private static WolfEntity getWolfEntity(PlayerInventory playerInventory, UuidPayload payload) {
        final PlayerEntity player = playerInventory.player;
        final Box area = new Box(player.getX() + -20, player.getY() + -20, player.getZ() + -20, player.getX() + 20, player.getY() + 20,  player.getZ() + 20);
        final List<Entity> entities = playerInventory.player.getWorld().getOtherEntities(playerInventory.player, area);
        final List<WolfEntity> wolfes = entities.stream().filter(e -> e instanceof WolfEntity).map(e -> (WolfEntity)e).toList();
        final Optional<WolfEntity> optWolf = wolfes.stream().filter(e -> e.getUuid().equals(payload.uuid())).findFirst();

        if (optWolf.isEmpty()) {
            throw new IllegalStateException("Client does not have wolf.");
        }

        return optWolf.get();
    }

    // Client Constructor
    public ExampleInventoryScreenHandler2(int syncId, PlayerInventory playerInventory, UuidPayload payload) {
        this(syncId, playerInventory, ExampleInventoryScreenHandler2.getWolfEntity(playerInventory, payload), payload.nbt());
    }

    public WolfEntity getWolf() {
        return wolf;
    }

    public SimpleInventory getWolfInventory() {
        return wolfInventory;
    }

    // Main Constructor - (Directly called from server)
    public ExampleInventoryScreenHandler2(int syncId, PlayerInventory playerInventory, WolfEntity wolf, NbtCompound nbt) {
        super(ScreenHandlerTypeInit.EXAMPLE_INVENTORY_SCREEN_HANDLER_2, syncId);

        this.wolf = wolf;
        this.context = ScreenHandlerContext.create(this.wolf.getWorld(), null);

//        if (!playerInventory.player.getWorld().isClient)
//            ServerPlayNetworking.send((ServerPlayerEntity) playerInventory.player,
//                    new SampleS2CPayload("badabub from server with " + playerInventory.player.getWorld().isClient, 123));

        wolfInventory = ((WolfEntityMixinProvider)(wolf)).wolfcompanion_template_1_21_1$getItemsInventory(); //NBTHelper.getInventory(nbt, wolf);
        checkSize(wolfInventory, 16);
        wolfInventory.onOpen(playerInventory.player);

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addBlockInventory(wolfInventory);
    }

    private void addPlayerInventory(PlayerInventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 84 + (row * 18)));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 142));
        }
    }

    private void addBlockInventory(SimpleInventory inventory) {
        armorSlot = addSlot(new ShadowArmorSlot(inventory, wolf, EquipmentSlot.BODY, 0, 8, 18, null));
        final ItemStack armorStack = wolf.getEquippedStack(EquipmentSlot.BODY);
        armorSlot.setStack(armorStack);

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 5; l++) {
                addSlot(new Slot(inventory, 1 + l + k * 5, 80 + l * 18, 18 + k * 18));
            }
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        for (int k = 0; k < slots.size(); k++) {
            Slot slot = getSlot(k);
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;
            //System.out.println("ExampleInventoryScreenHandler2 onClosed has item: " + stack.getItem().toString() + " " + getWolf().getWorld().isClient);
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = getSlot(slotIndex);
        if(slot != null && slot.hasStack()) {
            ItemStack inSlot = slot.getStack();
            newStack = inSlot.copy();

            if(slotIndex < 16) {
                if(!insertItem(inSlot, 16, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!insertItem(inSlot, 0, 16, false))
                return ItemStack.EMPTY;

            if(inSlot.isEmpty())
                slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }

        return newStack;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        if (!armorSlot.getStack().isEmpty() && !wolf.hasArmor()) {
            wolf.equipBodyArmor(armorSlot.getStack());
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}