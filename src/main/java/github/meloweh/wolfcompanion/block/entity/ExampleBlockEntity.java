package github.meloweh.wolfcompanion.block.entity;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.init.BlockEntityTypeInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ExampleBlockEntity extends BlockEntity {
    private int counter;

    public ExampleBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeInit.EXAMPLE_BLOCK_ENTITY, pos, state);
    }

    public int getCounter() {
        return this.counter;
    }

    public void incrementCounter() {
        this.counter++;
        markDirty();

        if (this.counter % 10 == 0 && this.world instanceof ServerWorld sWorld) {
            EntityType.PIG.spawn(sWorld, this.pos.up(), SpawnReason.TRIGGERED);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        //var modidData = new NbtCompound(); // Or just nbt.putInt("counter", this.counter);
        //modidData.putInt("counter", this.counter);
        //nbt.put(WolfCompanion.MOD_ID, modidData);
        nbt.putInt("counter", this.counter);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        //if (nbt.contains(WolfCompanion.MOD_ID, NbtElement.COMPOUND_TYPE)) { // Or just this.counter = nbt.contains("counter", NBTElement.INT_TYPE) ? nbt.getInt("counter") : 0;
        //    var modidData = nbt.getCompound(WolfCompanion.MOD_ID);
        //    this.counter = modidData.contains("counter", NbtElement.INT_TYPE) ? modidData.getInt("counter") : 0;
        //}
        this.counter = nbt.contains("counter", NbtElement.INT_TYPE) ? nbt.getInt("counter") : 0;
    }
}
