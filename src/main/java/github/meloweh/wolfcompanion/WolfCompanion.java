package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.config.WolfCompanionConfig;
import github.meloweh.wolfcompanion.effects.ModEffects;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.network.ModNetwork;
import github.meloweh.wolfcompanion.registry.ModEntities;
import github.meloweh.wolfcompanion.registry.ModItems;
import github.meloweh.wolfcompanion.registry.ModMenuTypes;
import github.meloweh.wolfcompanion.registry.ModSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WolfCompanion implements ModInitializer {
	public static final String MOD_ID = "wolfcompanion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final String CUSTOM_INVENTORY_UPDATE_ID = "custom_inventory_update";

	public static RegistryAccess dynamicRegistryManager;

	@Override
	public void onInitialize() {
		LOGGER.info("Loading...");
		WolfCompanionConfig.load();
		ModItems.register();
		ModEntities.register();
		ModSounds.register();
		ModMenuTypes.register();
		WolfEventHandler.init();
		ModEffects.register();

		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer ->
				dynamicRegistryManager = minecraftServer.registryAccess());

		ModNetwork.registerServerPayloads();
		ModNetwork.registerServerReceivers();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static boolean isSameEnchantment(Enchantment enchantment, ResourceKey<Enchantment> enchantmentRegistryKey) {
		if (dynamicRegistryManager == null) {
			LOGGER.error("Dynamic registry manager was not available while checking enchantments.");
			return false;
		}
		Registry<Enchantment> enchantmentRegistry = dynamicRegistryManager.registryOrThrow(Registries.ENCHANTMENT);

		Holder<Enchantment> enchantmentEntry = enchantmentRegistry.wrapAsHolder(enchantment);
		return enchantmentEntry != null && enchantmentEntry.is(enchantmentRegistryKey);
	}
}
