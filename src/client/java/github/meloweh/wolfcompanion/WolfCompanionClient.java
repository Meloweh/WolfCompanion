package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.init.BlockEntityTypeInit;
import github.meloweh.wolfcompanion.init.ScreenHandlerTypeInit;
import github.meloweh.wolfcompanion.model.ExampleChestModel;
import github.meloweh.wolfcompanion.network.SampleS2CPayload;
import github.meloweh.wolfcompanion.screen.ExampleInventoryBlockScreen;
import github.meloweh.wolfcompanion.screen.WolfInventoryScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.entity.EntityType;
import renderer.CustomWolfEntityRenderer;
import renderer.ExampleInventoryBER;

public class WolfCompanionClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		EntityModelLayerRegistry.registerModelLayer(ExampleChestModel.LAYER_LOCATION, ExampleChestModel::getTexturedModelData);

		HandledScreens.register(ScreenHandlerTypeInit.EXAMPLE_INVENTORY_SCREEN_HANDLER, ExampleInventoryBlockScreen::new);
		HandledScreens.register(ScreenHandlerTypeInit.EXAMPLE_INVENTORY_SCREEN_HANDLER_2, WolfInventoryScreen::new);

		BlockEntityRendererFactories.register(BlockEntityTypeInit.EXAMPLE_INVENTORY_BLOCK_ENTITY, ExampleInventoryBER::new);
		EntityRendererRegistry.register(EntityType.WOLF, CustomWolfEntityRenderer::new);

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