package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.init.InitItem;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;

public class WolfCompanionModelProvider extends FabricModelProvider {
    public WolfCompanionModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(InitItem.ITEM_SINGLE_WOLF_BAG, Models.GENERATED);
        itemModelGenerator.register(InitItem.ITEM_WOLF_BAG, Models.GENERATED);
    }

    @Override
    public void generateBlockStateModels(net.minecraft.client.data.BlockStateModelGenerator blockStateModelGenerator) {

    }


}
