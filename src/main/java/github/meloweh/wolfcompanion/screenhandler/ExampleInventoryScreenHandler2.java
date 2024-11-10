package github.meloweh.wolfcompanion.screenhandler;

import github.meloweh.wolfcompanion.block.entity.ExampleInventoryBlockEntity;
import github.meloweh.wolfcompanion.block.entity.ExampleInventoryNonBlockEntity;
import github.meloweh.wolfcompanion.init.InitBlock;
import github.meloweh.wolfcompanion.init.ScreenHandlerTypeInit;
import github.meloweh.wolfcompanion.network.BlockPosPayload;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.util.NBTHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExampleInventoryScreenHandler2 extends ScreenHandler {
    private final WolfEntity wolf;
    private final ScreenHandlerContext context;
    private final SimpleInventory wolfInventory;

    private static WolfEntity getWolfEntity(PlayerInventory playerInventory, UuidPayload payload) {

        final PlayerEntity player = playerInventory.player;
        //System.out.println("UUID UuidPayload: " + payload.uuid());
        final Box area = new Box(player.getX() + -20, player.getY() + -20, player.getZ() + -20, player.getX() + 20, player.getY() + 20,  player.getZ() + 20);
        final List<Entity> entities = playerInventory.player.getWorld().getOtherEntities(playerInventory.player, area);
        final List<WolfEntity> wolfes = entities.stream().filter(e -> e instanceof WolfEntity).map(e -> (WolfEntity)e).toList();
//        for(Entity wolf : entities) {
//            System.out.println("UUID wolf: " + wolf.getUuid());
//        }
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

//        NbtCompound nbt = new NbtCompound();
//        this.wolf.saveNbt(nbt);
//        NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
//        SimpleInventory items = new SimpleInventory();
//
//        for (int i = 0; i < nbtList.size(); i++) {
//            NbtCompound nbtCompound = nbtList.getCompound(i);
//            int j = nbtCompound.getByte("Slot") & 255;
//            if (j < items.size() - 1) {
//                items.setStack(j + 1, ItemStack.fromNbt(wolf.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY));
//            }
//        }


        //List<ItemStack> items = new ArrayList<>();
//        final SimpleInventory items = new SimpleInventory(5 * 3 + 1);
//
//        for (int i = 0; i < nbtList.size(); i++) {
//            NbtCompound nbtCompound = nbtList.getCompound(i);
//            int j = nbtCompound.getByte("Slot") & 255;
//            if (j < items.size() - 1) {
//                items.setStack(j + 1, ItemStack.fromNbt(wolf.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY));
//            }
//        }

        wolfInventory = NBTHelper.getInventory(nbt, wolf);
        checkSize(wolfInventory, 16);
        wolfInventory.onOpen(playerInventory.player);

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addBlockInventory(wolfInventory);
    }

    private void addPlayerInventory(PlayerInventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 112 + (row * 18)));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 160));
        }
    }

    private void addBlockInventory(SimpleInventory inventory) {
        /*for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 5; column++) {
                addSlot(new Slot(inventory, column + (row * 9), 8 + (column * 18), 18 + (row * 18)));
            }
        }*/
        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 5; l++) {
                addSlot(new Slot(inventory, 1 + l + k * 5, 80 + l * 18, 18 + k * 18));
            }
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        //this.blockEntity.getInventory().onClose(player);
        //ClientPlayNetworking.send(new UuidPayload(wolf.getUuid(), NBTHelper.getWolfNBT(wolf)));
        //ServerPlayNetworking
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
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}