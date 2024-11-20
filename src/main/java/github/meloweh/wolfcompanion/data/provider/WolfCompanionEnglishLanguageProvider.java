package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.init.InitItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class WolfCompanionEnglishLanguageProvider extends FabricLanguageProvider {
    public WolfCompanionEnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
//        translationBuilder.add(InitItem.FOOD_ITEM, "Food Item");
//        translationBuilder.add(InitItem.TOY_ITEM, "Toy Item");
//        translationBuilder.add(InitBlock.EXAMPLE_BLOCK, "Example Block");
//        translationBuilder.add(InitBlock.EXAMPLE_BE_BLOCK, "Example BE Block");
//        translationBuilder.add(InitBlock.EXAMPLE_TICKING_BE_BLOCK, "Example Ticking Block");
//        translationBuilder.add(InitBlock.EXAMPLE_INVENTORY_BLOCK, "Example Inventory Block");
        translationBuilder.add(InitItem.ITEM_SINGLE_WOLF_BAG, "Single Wolf Bag");
        translationBuilder.add(InitItem.ITEM_WOLF_BAG, "Wolf Bag");
        translationBuilder.add(InitItem.DOG_WHISTLE_ITEM, "Pet Whistle");

    }
}
