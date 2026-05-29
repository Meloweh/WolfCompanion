package github.meloweh.wolfcompanion.client;

import com.mojang.blaze3d.platform.InputConstants;
import github.meloweh.wolfcompanion.client.renderer.CustomWolfEntityRenderer;
import github.meloweh.wolfcompanion.client.screen.config.WolfConfigScreen;
import github.meloweh.wolfcompanion.client.screen.inventory.WolfInventoryScreen;
import github.meloweh.wolfcompanion.registry.ModMenuTypes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.EntityType;

public final class WolfCompanionClient implements ClientModInitializer {
	private static KeyMapping openConfigKey;

	@Override
	public void onInitializeClient() {
		registerScreens();
		registerRenderers();
		registerConfigShortcut();
		registerClientCommands();
	}

	private static void registerScreens() {
		MenuScreens.register(ModMenuTypes.WOLF_INVENTORY, WolfInventoryScreen::new);
	}

	private static void registerRenderers() {
		EntityRendererRegistry.register(EntityType.WOLF, CustomWolfEntityRenderer::new);
	}

	private static void registerConfigShortcut() {
		openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.wolfcompanion.open_config",
				InputConstants.Type.KEYSYM,
				InputConstants.UNKNOWN.getValue(),
				KeyMapping.CATEGORY_MISC
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openConfigKey.consumeClick()) {
				openConfigScreen(client);
			}
		});
	}

	private static void registerClientCommands() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
				ClientCommandManager.literal("wolfcompanion")
						.then(ClientCommandManager.literal("config")
								.executes(context -> {
									Minecraft client = context.getSource().getClient();
									client.execute(() -> openConfigScreen(client));
									return 1;
								}))
		));
	}

	private static void openConfigScreen(Minecraft client) {
		client.setScreen(new WolfConfigScreen(client.screen));
	}
}
