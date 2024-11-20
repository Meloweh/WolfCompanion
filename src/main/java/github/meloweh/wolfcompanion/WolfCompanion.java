package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.events.WolfEventHandler;
import github.meloweh.wolfcompanion.init.*;
import github.meloweh.wolfcompanion.network.DropWolfChestC2SPayload;
import github.meloweh.wolfcompanion.network.SampleS2CPayload;
import github.meloweh.wolfcompanion.util.ConfigManager;
import github.meloweh.wolfcompanion.util.WolfConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WolfCompanion implements ModInitializer {
	public static final String MOD_ID = "wolfcompanion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final String CUSTOM_INVENTORY_UPDATE_ID = "custom_inventory_update";

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
//		WolfEventHandler.registerEvents();

//		EnergyStorage.SIDED.registerForBlockEntity(ExampleEnergyGeneratorBlockEntity::getEnergyProvider, BlockEntityTypeInit.EXAMPLE_ENERGY_GENERATOR);
//		EnergyStorage.SIDED.registerForBlockEntity(ExampleEnergyStorageBlockEntity::getEnergyProvider, BlockEntityTypeInit.EXAMPLE_ENERGY_STORAGE);


		/*
		PayloadTypeRegistry.playC2S().register(UuidPayload.ID, UuidPayload.PACKET_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(UuidPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				final UUID uuid = payload.uuid();
				final NbtCompound nbt = payload.nbt();
				context.server().getWorlds().forEach(world -> {
					Entity entity = world.getEntity(uuid);
					if (entity != null) {
						System.out.println("C2S:");
						final SimpleInventory inventory = NBTHelper.getInventory(nbt, (WolfEntity) entity);
					}
				});
			});
		});
		System.out.println("BB");*/


		PayloadTypeRegistry.playC2S().register(DropWolfChestC2SPayload.ID, DropWolfChestC2SPayload.PACKET_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(DropWolfChestC2SPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
//				final int myint = payload.myint();
//				final String mystring = payload.mystring();
//				System.out.println("Server: From Client: string: " + mystring + " int: " + myint);
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
						provider.dropInventory();
						System.out.println("Dropping chest.");
					}
				});
			});
		});

		//PayloadTypeRegistry.playS2C().register(SampleS2CPayload.ID, SampleS2CPayload.PACKET_CODEC);

		/*AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (entity instanceof WolfEntity && ((WolfEntity) entity).isTamed()) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});*/

	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}