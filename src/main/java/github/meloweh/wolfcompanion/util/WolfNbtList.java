package github.meloweh.wolfcompanion.util;

import github.meloweh.wolfcompanion.events.WolfEventHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WolfNbtList {
    final private List<NbtCompound> wolfNbts = new ArrayList<>();

    public void writeDataToNbt(final WriteView nbt, final String KEY) {
        if (!this.wolfNbts.isEmpty()) {
            for (int i = 0; i < this.wolfNbts.size(); i++) {
                final NbtCompound wolfNbt = this.wolfNbts.get(i);
                nbt.put(KEY + i, NbtCompound.CODEC, wolfNbt);
            }
        }
    }

    public void readDataToNbt(ReadView nbt, final String KEY) {
        for (int i = 0; nbt.contains(KEY + i); i++) {
            final Optional<NbtCompound> wolfElement = nbt.read(KEY + i, NbtCompound.CODEC);
            queueWolfNbt(wolfElement.get());
        }
    }

    public void removeKeys(List<String> keys) {
        this.wolfNbts.forEach(nbt -> {
            keys.forEach(nbt::remove);
        });
    }

    public void queueWolfNbt(NbtCompound nbt) {
        this.wolfNbts.add(nbt);
    }

    public List<NbtCompound> getWolfNbts() {
        return this.wolfNbts;
    }

    public boolean isEmpty() {
        return this.wolfNbts.isEmpty();
    }

    public void clear() {
        this.wolfNbts.clear();
    }
}