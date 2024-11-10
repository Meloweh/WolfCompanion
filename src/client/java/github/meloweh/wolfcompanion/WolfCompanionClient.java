package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.handler.WolfInventoryHandler;
import github.meloweh.wolfcompanion.init.BlockEntityTypeInit;
import github.meloweh.wolfcompanion.init.ScreenHandlerTypeInit;
import github.meloweh.wolfcompanion.model.ExampleChestModel;
import github.meloweh.wolfcompanion.screen.ExampleEnergyGeneratorScreen;
import github.meloweh.wolfcompanion.screen.ExampleInventoryBlockScreen;
import github.meloweh.wolfcompanion.screen.ExampleInventoryBlockScreen2;

import github.meloweh.wolfcompanion.screen.WolfScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import renderer.CustomWolfEntityRenderer;
import renderer.ExampleInventoryBER;

public class WolfCompanionClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		EntityModelLayerRegistry.registerModelLayer(ExampleChestModel.LAYER_LOCATION, ExampleChestModel::getTexturedModelData);

		HandledScreens.register(ScreenHandlerTypeInit.EXAMPLE_ENERGY_GENERATOR, ExampleEnergyGeneratorScreen::new);
		HandledScreens.register(ScreenHandlerTypeInit.EXAMPLE_INVENTORY_SCREEN_HANDLER, ExampleInventoryBlockScreen::new);
		HandledScreens.register(ScreenHandlerTypeInit.EXAMPLE_INVENTORY_SCREEN_HANDLER_2, ExampleInventoryBlockScreen2::new);


		BlockEntityRendererFactories.register(BlockEntityTypeInit.EXAMPLE_INVENTORY_BLOCK_ENTITY, ExampleInventoryBER::new);
		EntityRendererRegistry.register(EntityType.WOLF, CustomWolfEntityRenderer::new);


	}
}