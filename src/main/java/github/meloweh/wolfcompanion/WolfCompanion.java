package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.block.entity.ExampleEnergyGeneratorBlockEntity;
import github.meloweh.wolfcompanion.block.entity.ExampleEnergyStorageBlockEntity;
import github.meloweh.wolfcompanion.init.BlockEntityTypeInit;
import github.meloweh.wolfcompanion.init.InitBlock;
import github.meloweh.wolfcompanion.init.InitItem;
import github.meloweh.wolfcompanion.init.ScreenHandlerTypeInit;
import github.meloweh.wolfcompanion.network.SampleC2SPayload;
import github.meloweh.wolfcompanion.network.SampleS2CPayload;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.util.NBTHelper;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

import java.util.UUID;

public class WolfCompanion implements ModInitializer {
	public static final String MOD_ID = "wolfcompanion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final String CUSTOM_INVENTORY_UPDATE_ID = "custom_inventory_update";

	@Override
	public void onInitialize() {
		LOGGER.info("Loading...");
		InitItem.load();
		InitBlock.load();
		BlockEntityTypeInit.load();
		ScreenHandlerTypeInit.load();

		EnergyStorage.SIDED.registerForBlockEntity(ExampleEnergyGeneratorBlockEntity::getEnergyProvider, BlockEntityTypeInit.EXAMPLE_ENERGY_GENERATOR);
		EnergyStorage.SIDED.registerForBlockEntity(ExampleEnergyStorageBlockEntity::getEnergyProvider, BlockEntityTypeInit.EXAMPLE_ENERGY_STORAGE);


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

		PayloadTypeRegistry.playC2S().register(SampleC2SPayload.ID, SampleC2SPayload.PACKET_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(SampleC2SPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				final int myint = payload.myint();
				final String mystring = payload.mystring();
				System.out.println("Server: From Client: string: " + mystring + " int: " + myint);
			});
		});

		PayloadTypeRegistry.playS2C().register(SampleS2CPayload.ID, SampleS2CPayload.PACKET_CODEC);

	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}