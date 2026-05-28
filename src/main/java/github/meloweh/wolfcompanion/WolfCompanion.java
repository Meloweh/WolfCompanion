package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.effects.ModEffects;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.init.*;
import github.meloweh.wolfcompanion.network.AggressionWolfC2SPayload;
import github.meloweh.wolfcompanion.network.DropWolfChestC2SPayload;
import github.meloweh.wolfcompanion.network.LockWolfC2SPayload;
import github.meloweh.wolfcompanion.network.ReleaseWolfC2SPayload;
import github.meloweh.wolfcompanion.util.ConfigManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.enchantment.Enchantment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WolfCompanion implements ModInitializer {
	public static final String MOD_ID = "wolfcompanion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final String CUSTOM_INVENTORY_UPDATE_ID = "custom_inventory_update";

	public static RegistryAccess dynamicRegistryManager;

	//public static final WolfConfig CONFIG = WolfConfig.createAndLoad();

	@Override
	public void onInitialize() {
		LOGGER.info("Loading...");
		ConfigManager.loadConfig();
		InitItem.load();
		InitBlock.load();
		InitSound.load();
		//BlockEntityTypeInit.load();
		ScreenHandlerTypeInit.load();
		WolfEventHandler.init();
		ModEffects.register();

		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
			dynamicRegistryManager = minecraftServer.registryAccess();
		});

		PayloadTypeRegistry.playC2S().register(DropWolfChestC2SPayload.ID, DropWolfChestC2SPayload.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(ReleaseWolfC2SPayload.ID, ReleaseWolfC2SPayload.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(AggressionWolfC2SPayload.ID, AggressionWolfC2SPayload.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(LockWolfC2SPayload.ID, LockWolfC2SPayload.PACKET_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(DropWolfChestC2SPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				context.server().getAllLevels().forEach(serverWorld -> {
					final Entity entity = serverWorld.getEntity(payload.wolfUUID());
					if (entity != null) {
						final Wolf wolf = (Wolf) entity;
						final WolfEntityProvider provider = (WolfEntityProvider) wolf;
						provider.setShouldDropChest(true);
						final LivingEntity owner = wolf.getOwner();
						if (owner instanceof ServerPlayer) {
							((ServerPlayer) owner).closeContainer();
						}
						provider.wolfcompanion_template_1_21_1$dropInventoryByButton();
					}
				});
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(ReleaseWolfC2SPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				context.server().getAllLevels().forEach(serverWorld -> {
					final Entity entity = serverWorld.getEntity(payload.wolfUUID());
					if (entity != null) {
						final Wolf wolf = (Wolf) entity;
						final WolfEntityProvider provider = (WolfEntityProvider) wolf;
						provider.setShouldReleaseWolf(true);
						final LivingEntity owner = wolf.getOwner();
						if (owner instanceof ServerPlayer) {
							((ServerPlayer) owner).closeContainer();
						}
						provider.releaseWolfButton();
					}
				});
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(AggressionWolfC2SPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				context.server().getAllLevels().forEach(serverWorld -> {
					final Entity entity = serverWorld.getEntity(payload.wolfUUID());
					if (entity != null) {
						final Wolf wolf = (Wolf) entity;
						final WolfEntityProvider provider = (WolfEntityProvider) wolf;
						provider.setAggressive__(!provider.isAggressive__());
					}
				});
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(LockWolfC2SPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				context.server().getAllLevels().forEach(serverWorld -> {
					final Entity entity = serverWorld.getEntity(payload.wolfUUID());
					if (entity != null) {
						final Wolf wolf = (Wolf) entity;
						final WolfEntityProvider provider = (WolfEntityProvider) wolf;
						provider.setLock__(!provider.isLock__());
					}
				});
			});
		});

	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static boolean isSameEnchantment(Enchantment enchantment, ResourceKey<Enchantment> enchantmentRegistryKey) {
		if (dynamicRegistryManager == null) {
			System.out.println("ERROR: dynamic registry manager was null");
			return false;
		}
		Registry<Enchantment> enchantmentRegistry = dynamicRegistryManager.lookupOrThrow(Registries.ENCHANTMENT);

		Holder<Enchantment> enchantmentEntry = enchantmentRegistry.wrapAsHolder(enchantment);
		return enchantmentEntry != null && enchantmentEntry.is(enchantmentRegistryKey);
	}
}