package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.init.ScreenHandlerTypeInit;
import github.meloweh.wolfcompanion.screen.WolfInventoryScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.EntityType;
import renderer.CustomWolfEntityRenderer;

public class WolfCompanionClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ScreenHandlerTypeInit.WOLF_INVENTORY_SCREEN_HANDLER, WolfInventoryScreen::new);

		EntityRendererRegistry.register(EntityType.WOLF, CustomWolfEntityRenderer::new);

//		ClientPlayNetworking.registerGlobalReceiver(SampleS2CPayload.ID, (payload, context) -> {
//			context.client().execute(() -> {
//				//ClientBlockHighlighting.highlightBlock(client, payload.blockPos());
//				final int myint = payload.myint();
//				final String mystring = payload.mystring();
//				System.out.println("Client: From Server: string: " + mystring + " int: " + myint);
//			});
//		});
	}
}