package github.meloweh.wolfcompanion;

import com.mojang.blaze3d.platform.InputConstants;
import github.meloweh.wolfcompanion.init.ScreenHandlerTypeInit;
import github.meloweh.wolfcompanion.screen.WolfConfigScreen;
import github.meloweh.wolfcompanion.screen.WolfInventoryScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.EntityType;
import renderer.CustomWolfEntityRenderer;

public class WolfCompanionClient implements ClientModInitializer {
	private static KeyMapping openConfigKey;

	@Override
	public void onInitializeClient() {
		MenuScreens.register(ScreenHandlerTypeInit.WOLF_INVENTORY_SCREEN_HANDLER, WolfInventoryScreen::new);

		EntityRendererRegistry.register(EntityType.WOLF, CustomWolfEntityRenderer::new);

		openConfigKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.wolfcompanion.open_config",
				InputConstants.Type.KEYSYM,
				InputConstants.UNKNOWN.getValue(),
				KeyMapping.Category.MISC
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openConfigKey.consumeClick()) {
				openConfigScreen(client);
			}
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
				ClientCommands.literal("wolfcompanion")
						.then(ClientCommands.literal("config")
								.executes(context -> {
									Minecraft client = context.getSource().getClient();
									client.execute(() -> openConfigScreen(client));
									return 1;
								}))
		));

//		ClientPlayNetworking.registerGlobalReceiver(SampleS2CPayload.ID, (payload, context) -> {
//			context.client().execute(() -> {
//				//ClientBlockHighlighting.highlightBlock(client, payload.blockPos());
//				final int myint = payload.myint();
//				final String mystring = payload.mystring();
//				System.out.println("Client: From Server: string: " + mystring + " int: " + myint);
//			});
//		});
	}

	public static void openConfigScreen(Minecraft client) {
		client.setScreen(new WolfConfigScreen(client.screen));
	}
}
