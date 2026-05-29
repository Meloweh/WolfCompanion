package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.data.provider.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.Nullable;

public class WolfCompanionDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(WolfCompanionEnglishLanguageProvider::new);
		pack.addProvider(WolfCompanionRecipeProvider::new);
		WolfCompanion.LOGGER.info("Loaded data generators");
	}

	@Override
	public @Nullable String getEffectiveModId() {
		return DataGeneratorEntrypoint.super.getEffectiveModId();
	}
}
