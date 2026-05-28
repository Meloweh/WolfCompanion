package github.meloweh.wolfcompanion.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WolfNbtList {
    final private List<CompoundTag> wolfNbts = new ArrayList<>();

    public void writeDataToNbt(final ValueOutput nbt, final String KEY) {
        if (!this.wolfNbts.isEmpty()) {
            for (int i = 0; i < this.wolfNbts.size(); i++) {
                final CompoundTag wolfNbt = this.wolfNbts.get(i);
                nbt.store(KEY + i, CompoundTag.CODEC, wolfNbt);
            }
        }
    }

    public void readDataToNbt(ValueInput nbt, final String KEY) {
        for (int i = 0; nbt.contains(KEY + i); i++) {
            final Optional<CompoundTag> wolfElement = nbt.read(KEY + i, CompoundTag.CODEC);
            queueWolfNbt(wolfElement.get());
        }
    }

    public void removeKeys(List<String> keys) {
        this.wolfNbts.forEach(nbt -> {
            keys.forEach(nbt::remove);
        });
    }

    public void rescueTimeoutTick() {
        this.wolfNbts.forEach(nbt -> {
            final int remainingTimeoutTicks = nbt.getIntOr("RescueTimeout", 0);
            if (remainingTimeoutTicks > 0) {
                nbt.putInt("RescueTimeout", remainingTimeoutTicks - 1);
            }
        });
    }

    public List<CompoundTag> dequeueElapsedTimeout() {
        final List<CompoundTag> results = new ArrayList<>();

        this.wolfNbts.forEach(nbt -> {
            final int remainingTimeoutTicks = nbt.getIntOr("RescueTimeout", 0);
            if (remainingTimeoutTicks < 1) {
                results.add(nbt);
            }
        });

        results.forEach(nbt -> {
            final int remainingTimeoutTicks = nbt.getIntOr("RescueTimeout", 0);
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
                .map(nbt -> nbt.getIntOr("RescueTimeout", 0))
                .filter(e -> e > 0)
                .toList().size();
    }

    public int elapsedSize() {
        return this.wolfNbts.size() - nonElapsedSize();
    }

    public Optional<Integer> getBriefestTimeout() {
        return this.wolfNbts.stream()
                .map(nbt -> nbt.getIntOr("RescueTimeout", 0))
                .filter(e -> e > 0)
                .min(Integer::compare);
    }


    public void clear() {
        this.wolfNbts.clear();
    }
}