package github.meloweh.wolfcompanion.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.config.WolfCompanionConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.TagValueOutput;

public class NBTHelper {
    public static CompoundTag getWolfNBT(Wolf wolf) {
        if (wolf == null) {
            throw new IllegalArgumentException("Wolf entity cannot be null");
        }

        final TagValueOutput nbtWriteView = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        wolf.saveWithoutId(nbtWriteView);
        return nbtWriteView.buildResult();
    }

    public static void cleanRescueWolfNbt(final CompoundTag wolfNbt, final float health) {
        wolfNbt.remove(Wolf.TAG_HURT_TIME);
        wolfNbt.remove(Wolf.TAG_HURT_BY_TIMESTAMP);
        wolfNbt.remove(Wolf.TAG_DEATH_TIME);
        wolfNbt.remove("body_armor_item");
        wolfNbt.remove("body_armor_drop_chance");
        wolfNbt.remove(Wolf.TAG_DROP_CHANCES);
        wolfNbt.remove("RescueTimeout");


        wolfNbt.putFloat(Wolf.TAG_FALL_DISTANCE, 0f);
        wolfNbt.putFloat(Wolf.TAG_HEALTH, health);

        if (!WolfCompanionConfig.current().keepWolfInventory) {
            if (!WolfCompanionConfig.current().keepWolfArmor)
                wolfNbt.remove("ArmorItems");
            if (!WolfCompanionConfig.current().keepWolfBag)
                wolfNbt.remove("ChestedWolf");
            wolfNbt.remove("Items");

            if (!WolfCompanionConfig.current().keepXp)
                wolfNbt.putInt("XP", 0);
        }
    }

    public static void applyEntityNbt(Entity target, CompoundTag nbt) {
        try {
            new EntityDataAccessor(target).setData(nbt); // mirrors /data merge entity
        } catch (CommandSyntaxException e) {
            throw new RuntimeException("Invalid NBT for entity", e);
        }
    }


    public static boolean spawnWolfFromNbt(final ServerPlayer player, final CompoundTag wolfNbt, final boolean rescue) {
            final ServerLevel world = (ServerLevel) player.level();

            Wolf newWolf = EntityType.WOLF.create(
                    world,
                    e -> {
                        final CustomData nbtComponent = CustomData.of(wolfNbt);
                        applyEntityNbt(e, nbtComponent.copyTag());
                        //nbtComponent.applyToEntity(e);
                    },
                    player.blockPosition(),
                    EntitySpawnReason.MOB_SUMMONED,
                    true,  // align position to center
                    true  // spawn in water allowed?
            );

            if (newWolf == null) {
                WolfCompanion.LOGGER.error("Failed to create wolf from NBT.");
                return false;
            }

            if (rescue) newWolf.setHealth(newWolf.getMaxHealth());
            newWolf.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            newWolf.removeAllEffects();
            newWolf.setSharedFlagOnFire(false);
            newWolf.setRemainingFireTicks(0);
            newWolf.spawnAnim();

            ServerLevel sw = (ServerLevel) newWolf.level();
            double x = newWolf.getX(), y = newWolf.getY(0.5), z = newWolf.getZ();
            sw.sendParticles(ParticleTypes.POOF,  x, y, z, 9, 0.25, 0.20, 0.25, 0.01);
            sw.sendParticles(ParticleTypes.CLOUD, x, y, z,  4, 0.20, 0.10, 0.20, 0.00);

            return world.tryAddFreshEntityWithPassengers(newWolf);
        }

    }
