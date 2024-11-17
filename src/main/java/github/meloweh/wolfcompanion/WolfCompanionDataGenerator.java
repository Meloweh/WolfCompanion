package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.data.provider.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WolfCompanionDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(WolfCompanionModelProvider::new);
		pack.addProvider(WolfCompanionEnglishLanguageProvider::new);
		pack.addProvider(WolfCompanionLootTableProvider::new);
		pack.addProvider(WolfCompanionBlockTagProvider::new);
		pack.addProvider(WolfCompanionRecipeProvider::new);
		WolfCompanion.LOGGER.warn("LOADED onInitializeDataGenerator");
	}
}
