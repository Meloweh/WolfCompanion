package github.meloweh.wolfcompanion.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class WolfNbtList {
    final private List<CompoundTag> wolfNbts = new ArrayList<>();

    public void writeDataToNbt(final CompoundTag nbt, final String KEY) {
        if (!this.wolfNbts.isEmpty()) {
            for (int i = 0; i < this.wolfNbts.size(); i++) {
                final CompoundTag wolfNbt = this.wolfNbts.get(i);
                nbt.put(KEY + i, wolfNbt);
            }
        }
    }

    public void readDataToNbt(CompoundTag nbt, final String KEY) {
        for (int i = 0; nbt.contains(KEY + i); i++) {
            Tag wolfElement = nbt.get(KEY + i);
            if (wolfElement instanceof CompoundTag wolfNbt) {
                queueWolfNbt(wolfNbt);
            }
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

    public List<CompoundTag> dequeueElapsedTimeout() {
        final List<CompoundTag> results = new ArrayList<>();

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

    public void queueWolfNbt(CompoundTag nbt) {
        this.wolfNbts.add(nbt);
    }

    public List<CompoundTag> getWolfNbts() {
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


    public void clear() {
        this.wolfNbts.clear();
    }

    public static int getIntOrDefault(CompoundTag nbt, String key, int fallback) {
        return nbt.contains(key) ? nbt.getInt(key) : fallback;
    }
}
