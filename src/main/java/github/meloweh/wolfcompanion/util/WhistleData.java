package github.meloweh.wolfcompanion.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.function.Consumer;

public final class WhistleData {
    private static final String KEY = "WhistleData";

    /** Read a copy of your subtag. Empty if absent. */
    public static NbtCompound read(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null || comp.isEmpty()) return new NbtCompound();
        NbtCompound root = comp.copyNbt();
        return root.contains(KEY, NbtElement.COMPOUND_TYPE) ? root.getCompound(KEY).copy() : new NbtCompound();
    }

    /** Overwrite your subtag. Pass null/empty to remove it. Preserves other CUSTOM_DATA keys. */
    public static void write(ItemStack stack, NbtCompound sub) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound root = comp == null ? new NbtCompound() : comp.copyNbt();

        if (sub == null || sub.isEmpty()) {
            root.remove(KEY);
            if (root.isEmpty()) stack.remove(DataComponentTypes.CUSTOM_DATA);
            else NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, root);
            return;
        }
        root.put(KEY, sub.copy());
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, root);
    }

    /** In-place edit: loads, lets you mutate, then writes back. */
    public static void remove(ItemStack stack, Consumer<NbtCompound> edit) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound root = comp == null ? new NbtCompound() : comp.copyNbt();

        root.remove(KEY);
        stack.remove(DataComponentTypes.CUSTOM_DATA);
    }
}

