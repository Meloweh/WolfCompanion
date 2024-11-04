package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.init.InitBlock;
import github.meloweh.wolfcompanion.init.InitItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;

public class WolfCompanionModelProvider extends FabricModelProvider {
    public WolfCompanionModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(InitBlock.EXAMPLE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(InitBlock.EXAMPLE_BE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(InitBlock.EXAMPLE_TICKING_BE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(InitBlock.EXAMPLE_ENERGY_GENERATOR_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(InitBlock.EXAMPLE_ENERGY_STORAGE_BLOCK);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(InitItem.FOOD_ITEM, Models.GENERATED);
        itemModelGenerator.register(InitItem.TOY_ITEM, Models.GENERATED);
        //itemModelGenerator.register(InitItem.EXAMPLE_BLOCK, Models.GENERATED);

    }
}
