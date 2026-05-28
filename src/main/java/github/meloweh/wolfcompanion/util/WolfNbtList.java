package github.meloweh.wolfcompanion.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WolfNbtList {
    final private List<NbtCompound> wolfNbts = new ArrayList<>();

    public void writeDataToNbt(final NbtCompound nbt, final String KEY) {
        if (!this.wolfNbts.isEmpty()) {
            for (int i = 0; i < this.wolfNbts.size(); i++) {
                final NbtCompound wolfNbt = this.wolfNbts.get(i);
                nbt.put(KEY + i, wolfNbt);
            }
        }
    }

    public void readDataToNbt(NbtCompound nbt, final String KEY) {
        for (int i = 0; nbt.contains(KEY + i); i++) {
            final NbtElement wolfElement = nbt.get(KEY + i);
            if (!(wolfElement instanceof NbtCompound)) {
                throw new IllegalStateException("nbt should be compound");
            }
            queueWolfNbt((NbtCompound) wolfElement);
        }
    }

    public void removeKeys(List<String> keys) {
        this.wolfNbts.forEach(nbt -> {
            keys.forEach(nbt::remove);
        });
    }

    public void rescueTimeoutTick() {
        this.wolfNbts.forEach(nbt -> {
            final int remainingTimeoutTicks = getIntOrDefault(nbt, "RescueTimeout", 0);
            if (remainingTimeoutTicks > 0) {
                nbt.putInt("RescueTimeout", remainingTimeoutTicks - 1);
            }
        });
    }

    public List<NbtCompound> dequeueElapsedTimeout() {
        final List<NbtCompound> results = new ArrayList<>();

        this.wolfNbts.forEach(nbt -> {
            final int remainingTimeoutTicks = getIntOrDefault(nbt, "RescueTimeout", 0);
            if (remainingTimeoutTicks < 1) {
                results.add(nbt);
            }
        });

        results.forEach(nbt -> {
            final int remainingTimeoutTicks = getIntOrDefault(nbt, "RescueTimeout", 0);
            if (remainingTimeoutTicks < 1) {
                this.wolfNbts.remove(nbt);
            }
        });

        return results;
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

    public int nonElapsedSize() {
        return this.wolfNbts.stream()
                .map(nbt -> getIntOrDefault(nbt, "RescueTimeout", 0))
                .filter(e -> e > 0)
                .toList().size();
    }

    public int elapsedSize() {
        return this.wolfNbts.size() - nonElapsedSize();
    }

    public Optional<Integer> getBriefestTimeout() {
        return this.wolfNbts.stream()
                .map(nbt -> getIntOrDefault(nbt, "RescueTimeout", 0))
                .filter(e -> e > 0)
                .min(Integer::compare);
    }

    public static int getIntOrDefault(NbtCompound nbt, String key, int fallback) {
        return nbt.contains(key) ? nbt.getInt(key) : fallback;
    }

    public void clear() {
        this.wolfNbts.clear();
    }
}
