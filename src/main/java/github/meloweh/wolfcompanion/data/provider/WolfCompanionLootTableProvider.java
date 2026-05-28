package github.meloweh.wolfcompanion.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class WolfCompanionLootTableProvider extends FabricBlockLootTableProvider {

    public WolfCompanionLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        //addDrop(InitBlock.EXAMPLE_BLOCK);
    }
}
