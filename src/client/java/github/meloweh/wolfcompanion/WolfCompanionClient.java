package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.init.BlockEntityTypeInit;
import github.meloweh.wolfcompanion.init.ScreenHandlerTypeInit;
import github.meloweh.wolfcompanion.model.ExampleChestModel;
import github.meloweh.wolfcompanion.screen.ExampleEnergyGeneratorScreen;
import github.meloweh.wolfcompanion.screen.ExampleInventoryBlockScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import renderer.ExampleInventoryBER;

public class WolfCompanionClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		EntityModelLayerRegistry.registerModelLayer(ExampleChestModel.LAYER_LOCATION, ExampleChestModel::getTexturedModelData);

		HandledScreens.register(ScreenHandlerTypeInit.EXAMPLE_ENERGY_GENERATOR, ExampleEnergyGeneratorScreen::new);
		HandledScreens.register(ScreenHandlerTypeInit.EXAMPLE_INVENTORY_SCREEN_HANDLER, ExampleInventoryBlockScreen::new);

		BlockEntityRendererFactories.register(BlockEntityTypeInit.EXAMPLE_INVENTORY_BLOCK_ENTITY, ExampleInventoryBER::new);

	}
}