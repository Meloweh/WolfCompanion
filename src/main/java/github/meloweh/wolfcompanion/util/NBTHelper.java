package github.meloweh.wolfcompanion.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntityDataObject;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;

public class NBTHelper {
        public static NbtCompound getWolfNBT(WolfEntity wolf) {
            if (wolf == null) {
                throw new IllegalArgumentException("Wolf entity cannot be null");
            }

            final NbtWriteView nbtWriteView = NbtWriteView.create(ErrorReporter.EMPTY);
            wolf.writeData(nbtWriteView);
            return nbtWriteView.getNbt();
        }

        public static void cleanRescueWolfNbt(final NbtCompound wolfNbt, final float health) {
            wolfNbt.remove(WolfEntity.HURT_TIME_KEY);
            wolfNbt.remove(WolfEntity.HURT_BY_TIMESTAMP_KEY);
            wolfNbt.remove(WolfEntity.DEATH_TIME_KEY);
            wolfNbt.remove("body_armor_item");
            wolfNbt.remove("body_armor_drop_chance");
            wolfNbt.remove(WolfEntity.DROP_CHANCES_KEY);
            wolfNbt.putFloat(WolfEntity.FALL_DISTANCE_KEY, 0f);
            wolfNbt.putFloat(WolfEntity.HEALTH_KEY, health);

            if (!ConfigManager.config.keepWolfInventory) {
                if (!ConfigManager.config.keepWolfArmor)
                    wolfNbt.remove("ArmorItems");
                if (!ConfigManager.config.keepWolfBag)
                    wolfNbt.remove("ChestedWolf");
                wolfNbt.remove("Items");
                wolfNbt.putInt("XP", 0);
            }
        }

        /*

            ServerWorld world = this.self.getEntityWorld();

            WolfEntity newWolf = EntityType.WOLF.create(
                    world,
                    e -> {
                        final NbtComponent nbtComponent = NbtComponent.of(wolfNbt);
                        nbtComponent.applyToEntity(e);
                    },
                    pos,
                    SpawnReason.MOB_SUMMONED,
                    true,  // align position to center
                    false  // spawn in water allowed?
            );

            if (newWolf != null) {
                final boolean success = world.spawnNewEntityAndPassengers(newWolf);
                if (success) canDelete.add(wolfNbt);
            }
         */

        /*public static boolean spawnWolfFromNbt(final ServerPlayerEntity player, final NbtCompound wolfNbt, final boolean rescue) {
            final ServerWorld world = (ServerWorld) player.getEntityWorld();
            final WolfEntity newWolf = EntityType.WOLF.create(world);

            if (rescue) newWolf.setHealth(newWolf.getMaxHealth());
            newWolf.clearStatusEffects();
            newWolf.readNbt(wolfNbt);
            newWolf.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            newWolf.playSpawnEffects();

            ServerWorld sw = (ServerWorld) newWolf.getEntityWorld();
            double x = newWolf.getX(), y = newWolf.getBodyY(0.5), z = newWolf.getZ();
            sw.spawnParticles(ParticleTypes.POOF,  x, y, z, 9, 0.25, 0.20, 0.25, 0.01);
            sw.spawnParticles(ParticleTypes.CLOUD, x, y, z,  4, 0.20, 0.10, 0.20, 0.00);

            return world.spawnEntity(newWolf);
        }*/

    public static void applyEntityNbt(Entity target, NbtCompound nbt) {
        try {
            new EntityDataObject(target).setNbt(nbt); // mirrors /data merge entity
        } catch (CommandSyntaxException e) {
            throw new RuntimeException("Invalid NBT for entity", e);
        }
    }


    public static boolean spawnWolfFromNbt(final ServerPlayerEntity player, final NbtCompound wolfNbt, final boolean rescue) {
            final ServerWorld world = (ServerWorld) player.getEntityWorld();

            WolfEntity newWolf = EntityType.WOLF.create(
                    world,
                    e -> {
                        final NbtComponent nbtComponent = NbtComponent.of(wolfNbt);
                        applyEntityNbt(e, nbtComponent.copyNbt());
                        //nbtComponent.applyToEntity(e);
                    },
                    player.getBlockPos(),
                    SpawnReason.MOB_SUMMONED,
                    true,  // align position to center
                    true  // spawn in water allowed?
            );

            if (newWolf == null) {
                System.out.println("ERROR: New wolf is null.");
                return false;
            }

            if (rescue) newWolf.setHealth(newWolf.getMaxHealth());
            newWolf.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            newWolf.clearStatusEffects();
            newWolf.setOnFire(false);
            newWolf.setFireTicks(0);
            newWolf.playSpawnEffects();

            ServerWorld sw = (ServerWorld) newWolf.getEntityWorld();
            double x = newWolf.getX(), y = newWolf.getBodyY(0.5), z = newWolf.getZ();
            sw.spawnParticles(ParticleTypes.POOF,  x, y, z, 9, 0.25, 0.20, 0.25, 0.01);
            sw.spawnParticles(ParticleTypes.CLOUD, x, y, z,  4, 0.20, 0.10, 0.20, 0.00);

            return world.spawnNewEntityAndPassengers(newWolf);
        }

    }