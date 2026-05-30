package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class WolfCompanionEnglishLanguageProvider extends FabricLanguageProvider {
    public WolfCompanionEnglishLanguageProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.SINGLE_WOLF_BAG, "Single Wolf Bag");
        translationBuilder.add(ModItems.WOLF_BAG, "Wolf Bag");
        translationBuilder.add(ModItems.ARMADILLO_SCUTE, "Armadillo Scute");
        translationBuilder.add(ModItems.ARMADILLO_SPAWN_EGG, "Armadillo Spawn Egg");
        translationBuilder.add(ModItems.WOLF_ARMOR, "Wolf Armor");
        translationBuilder.add(ModItems.DOG_WHISTLE, "Pet Whistle");
        translationBuilder.add("key.wolfcompanion.open_config", "Open Wolf Companion Config");
        translationBuilder.add("screen.wolfcompanion.config", "Wolf Companion Config");
    }
}
