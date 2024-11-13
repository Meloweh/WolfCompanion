package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.handler.WolfInventoryHandler;
import github.meloweh.wolfcompanion.init.BlockEntityTypeInit;
import github.meloweh.wolfcompanion.init.ScreenHandlerTypeInit;
import github.meloweh.wolfcompanion.model.ExampleChestModel;
import github.meloweh.wolfcompanion.network.SampleS2CPayload;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.screen.ExampleEnergyGeneratorScreen;
import github.meloweh.wolfcompanion.screen.ExampleInventoryBlockScreen;
import github.meloweh.wolfcompanion.screen.ExampleInventoryBlockScreen2;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import renderer.CustomWolfEntityRenderer;
import renderer.ExampleInventoryBER;
import renderer.WolfHeldItemFeatureRenderer;

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
		//EntityRendererRegistry.register(EntityType.WOLF, WolfHeldItemFeatureRenderer::new);

		ClientPlayNetworking.registerGlobalReceiver(SampleS2CPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				//ClientBlockHighlighting.highlightBlock(client, payload.blockPos());
				final int myint = payload.myint();
				final String mystring = payload.mystring();
				System.out.println("Client: From Server: string: " + mystring + " int: " + myint);
			});
		});
	}
}