package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.block.entity.ExampleEnergyGeneratorBlockEntity;
import github.meloweh.wolfcompanion.block.entity.ExampleEnergyStorageBlockEntity;
import github.meloweh.wolfcompanion.init.BlockEntityTypeInit;
import github.meloweh.wolfcompanion.init.InitBlock;
import github.meloweh.wolfcompanion.init.InitItem;
import github.meloweh.wolfcompanion.init.ScreenHandlerTypeInit;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

public class WolfCompanion implements ModInitializer {
	public static final String MOD_ID = "wolfcompanion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loading...");
		InitItem.load();
		InitBlock.load();
		BlockEntityTypeInit.load();
		ScreenHandlerTypeInit.load();

		EnergyStorage.SIDED.registerForBlockEntity(ExampleEnergyGeneratorBlockEntity::getEnergyProvider, BlockEntityTypeInit.EXAMPLE_ENERGY_GENERATOR);
		EnergyStorage.SIDED.registerForBlockEntity(ExampleEnergyStorageBlockEntity::getEnergyProvider, BlockEntityTypeInit.EXAMPLE_ENERGY_STORAGE);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static Identifier vanillaResource(String path) {
		return Identifier.of("minecraft", path);
	}
}