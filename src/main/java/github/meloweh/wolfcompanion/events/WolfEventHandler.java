package github.meloweh.wolfcompanion.events;

import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

public class WolfEventHandler {

    public static final String Wolf_NBT_KEY = "SavedWolfData";

//    public static void registerEvents() {
//        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
//            if (entity instanceof WolfEntity wolf) {
//                if (wolf.isTamed() && wolf.isDead()) {
//                    //ServerPlayer
//                    //saveWolfNbtToPlayer(wolf, (PlayerEntity) wolf.getOwner());
//                    final ServerPlayerAccessor playerAccessor = (ServerPlayerAccessor) (wolf.getOwner());
//                    final NbtCompound wolfNbt = new NbtCompound();
//                    wolf.writeCustomDataToNbt(wolfNbt);
//                    wolfNbt.putFloat("Health", wolf.getMaxHealth());
//                    wolfNbt.remove("HurtTime");
//                    wolfNbt.remove("HurtByTimestamp");
//                    wolfNbt.remove("DeathTime");
//                    wolf
//                    //playerAccessor.writeWolfNbt(wolfNbt);
//                }
//            }
//        });

//        ServerPlayerEvents.AFTER_RESPAWN.register((player, pos, a) -> {
//            final ServerPlayerAccessor playerAccessor = (ServerPlayerAccessor) (player);
//            final NbtCompound wolfNbt = playerAccessor.readWolfNbt();
//            if (wolfNbt != null) {
//                System.out.println("nbt not null");
//                //respawnWolfIfSaved(player.getServerWorld(), player);
//                respawnWolf(player.getServerWorld(), wolfNbt, player);
//            } else {
//                System.out.println("nbt null");
//            }
//            //respawnWolfIfSaved(player.getServerWorld(), player);
//        });
//
//        ServerTickEvents.END_WORLD_TICK.register(world -> {
//            if (world instanceof ServerWorld serverWorld) {
//                checkSleepingPlayers(serverWorld);
//            }
//        });
    }

    /*
    private static void checkSleepingPlayers(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.isSleeping() && player.has) {
                // Player just started sleeping
                onPlayerStartSleeping(world, player);
                sleepingPlayers.add(player);
            } else if (!player.isSleeping() && sleepingPlayers.contains(player)) {
                // Player just stopped sleeping
                sleepingPlayers.remove(player);
            }
        }
    }

    private static void onPlayerStartSleeping(ServerWorld world, ServerPlayerEntity player) {
        respawnHorseIfSaved(world, player);
    }

        SleepCall.AFTER.register((player, pos) -> {
        if (!player.getWorld().isClient) {
            respawnWolfIfSaved((ServerWorld) player.getWorld(), player);
        }
    });*/

//    private static void saveWolfNbtToPlayer(WolfEntity wolf, PlayerEntity player) {
//        NbtCompound wolfNbt = new NbtCompound();
//        wolf.writeNbt(wolfNbt);
//
//        NbtCompound playerData = new NbtCompound();
//        player.writeCustomDataToNbt(playerData);
//
//        playerData.put(Wolf_NBT_KEY, wolfNbt);
//
//
//        player.readCustomDataFromNbt(playerData);
//
//        System.out.println("WROTE WOLF"); // will print
//
//        if (playerData.contains(Wolf_NBT_KEY)) {
//            System.out.println("HAS WOLF LOCALLY"); // will print
//        }
//
//        NbtCompound playerData2 = new NbtCompound();
//        player.writeCustomDataToNbt(playerData2);
//        if (playerData2.contains(Wolf_NBT_KEY)) {
//            System.out.println("HAS WOLF SEMI LOCALLY"); // will not print
//        }
//    }
//
//    private static void respawnWolfIfSaved(ServerWorld world, PlayerEntity player) {
//        NbtCompound playerData = new NbtCompound();
//        player.writeCustomDataToNbt(playerData);
//
//        if (playerData.contains(Wolf_NBT_KEY)) {
//            System.out.println("READING WOLF");
//            NbtCompound wolfNbt = playerData.getCompound(Wolf_NBT_KEY);
//            respawnWolf(world, wolfNbt, player);
//
//            playerData.remove(Wolf_NBT_KEY);
//            player.readCustomDataFromNbt(playerData);
//        } else {
//            System.out.println("WILL NOT READ WOLF");
//        }
//        ServerLivingEntityEvents.ALLOW_DEATH.register();
//    }
//
//    private static void respawnWolf(ServerWorld world, NbtCompound wolfNbt, PlayerEntity player) {
//        WolfEntity wolf = new WolfEntity(EntityType.WOLF, world);
//        wolf.readNbt(wolfNbt); // Load wolf data
//        wolf.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0.0F, 0.0F);
//        world.spawnEntity(wolf);
//    }


