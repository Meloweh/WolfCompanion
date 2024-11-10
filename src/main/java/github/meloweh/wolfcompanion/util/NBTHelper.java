package github.meloweh.wolfcompanion.util;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class NBTHelper {
    public static NbtCompound getWolfNBT(WolfEntity wolf) {
        if (wolf == null) {
            throw new IllegalArgumentException("Wolf entity cannot be null");
        }
        NbtCompound nbtData = new NbtCompound();
        wolf.writeNbt(nbtData);
        return nbtData;
    }

    public static SimpleInventory getInventory(NbtCompound nbt, WolfEntity wolf) {
        NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
        final SimpleInventory items = new SimpleInventory(16);

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j < items.size() - 1) {
                final ItemStack itemStack = ItemStack.fromNbt(wolf.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY);
                System.out.println("reading ItemStack: " + itemStack.toHoverableText().getString());
                items.setStack(j + 1, itemStack);
            }
        }
        return items;
    }

}
