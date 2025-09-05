package github.meloweh.wolfcompanion.util;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;

public class NBTHelper {
    public static NbtCompound getWolfNBT(WolfEntity wolf) {
        if (wolf == null) {
            throw new IllegalArgumentException("Wolf entity cannot be null");
        }

        //final NbtCompound wolfNbt = writeView.getNbt();
        final NbtWriteView nbtWriteView = NbtWriteView.create(ErrorReporter.EMPTY);
        wolf.writeData(nbtWriteView);
        return nbtWriteView.getNbt();
    }

    /*public static SimpleInventory getInventory(NbtCompound nbt, WolfEntity wolf) {
        NbtList nbtList = nbt.getList("Items").get();
        final SimpleInventory items = new SimpleInventory(16);

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbtCompound = nbtList.getCompound(i).get();
            int j = nbtCompound.getByte("Slot") & 255;
            if (j < items.size() - 1) {
                final ItemStack itemStack = ItemStack.fromNbt(wolf.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY);
                System.out.println("reading ItemStack: " + itemStack.toHoverableText().getString());
                items.setStack(j + 1, itemStack);
            }
        }
        return items;
    }*/

}
