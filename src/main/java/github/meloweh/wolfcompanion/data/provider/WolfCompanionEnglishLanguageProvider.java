package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;

public class WolfCompanionEnglishLanguageProvider extends FabricLanguageProvider {
    public WolfCompanionEnglishLanguageProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.SINGLE_WOLF_BAG, "Single Wolf Bag");
        translationBuilder.add(ModItems.WOLF_BAG, "Wolf Bag");
        translationBuilder.add(ModItems.DOG_WHISTLE, "Pet Whistle");
        translationBuilder.add("key.wolfcompanion.open_config", "Open Wolf Companion Config");
        translationBuilder.add("screen.wolfcompanion.config", "Wolf Companion Config");
    }
}
