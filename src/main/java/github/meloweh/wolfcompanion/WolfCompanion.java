package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.init.*;
import github.meloweh.wolfcompanion.network.DropWolfChestC2SPayload;
import github.meloweh.wolfcompanion.util.ConfigManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WolfCompanion implements ModInitializer {
	public static final String MOD_ID = "wolfcompanion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final String CUSTOM_INVENTORY_UPDATE_ID = "custom_inventory_update";

	public static DynamicRegistryManager dynamicRegistryManager;

	//public static final WolfConfig CONFIG = WolfConfig.createAndLoad();

	@Override
	public void onInitialize() {
		LOGGER.info("Loading...");
		ConfigManager.loadConfig();
		InitItem.load();
		InitBlock.load();
		InitSound.load();
		BlockEntityTypeInit.load();
		ScreenHandlerTypeInit.load();
		WolfEventHandler.init();

		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
			dynamicRegistryManager = minecraftServer.getRegistryManager();
		});

		PayloadTypeRegistry.playC2S().register(DropWolfChestC2SPayload.ID, DropWolfChestC2SPayload.PACKET_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(DropWolfChestC2SPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				context.server().getWorlds().forEach(serverWorld -> {
					final Entity entity = serverWorld.getEntity(payload.wolfUUID());
					if (entity != null) {
						final WolfEntity wolf = (WolfEntity) entity;
						final WolfEntityProvider provider = (WolfEntityProvider) wolf;
						provider.setShouldDropChest(true);
						final LivingEntity owner = wolf.getOwner();
						if (owner instanceof ServerPlayerEntity) {
							((ServerPlayerEntity) owner).closeHandledScreen();
						}
						provider.wolfcompanion_template_1_21_1$dropInventoryByButton();
					}
				});
			});
		});
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static boolean isSameEnchantment(Enchantment enchantment, RegistryKey<Enchantment> enchantmentRegistryKey) {
		if (dynamicRegistryManager == null) {
			System.out.println("ERROR: dynamic registry manager was null");
			return false;
		}
		Registry<Enchantment> enchantmentRegistry = dynamicRegistryManager.get(RegistryKeys.ENCHANTMENT);
		RegistryEntry<Enchantment> enchantmentEntry = enchantmentRegistry.getEntry(enchantment);
		return enchantmentEntry != null && enchantmentEntry.matchesKey(enchantmentRegistryKey);
	}
}