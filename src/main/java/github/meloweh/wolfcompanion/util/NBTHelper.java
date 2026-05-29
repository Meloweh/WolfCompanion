package github.meloweh.wolfcompanion.util;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.config.WolfCompanionConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;

public class NBTHelper {
    public static CompoundTag getWolfNBT(Wolf wolf) {
        if (wolf == null) {
            throw new IllegalArgumentException("Wolf entity cannot be null");
        }

        final CompoundTag nbt = new CompoundTag();
        wolf.saveWithoutId(nbt);
        return nbt;
    }

    public static void cleanRescueWolfNbt(final CompoundTag wolfNbt, final float health) {
        wolfNbt.remove("HurtTime");
        wolfNbt.remove("HurtByTimestamp");
        wolfNbt.remove("DeathTime");
        wolfNbt.remove("body_armor_item");
        wolfNbt.remove("body_armor_drop_chance");
        wolfNbt.remove("ArmorDropChances");
        wolfNbt.remove("RescueTimeout");


        wolfNbt.putFloat("FallDistance", 0f);
        wolfNbt.putFloat("Health", health);

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

    public static boolean spawnWolfFromNbt(final ServerPlayer player, final CompoundTag wolfNbt, final boolean rescue) {
        final ServerLevel world = (ServerLevel) player.level();

        Wolf newWolf = EntityType.WOLF.create(world);
        if (newWolf == null) {
            WolfCompanion.LOGGER.error("Failed to create wolf from NBT.");
            return false;
        }

        newWolf.load(wolfNbt);
        if (rescue) {
            newWolf.setHealth(newWolf.getMaxHealth());
        }
        newWolf.removeAllEffects();
        newWolf.setSharedFlagOnFire(false);
        newWolf.setRemainingFireTicks(0);
        newWolf.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        newWolf.spawnAnim();

        double x = newWolf.getX(), y = newWolf.getY(0.5), z = newWolf.getZ();
        world.sendParticles(ParticleTypes.POOF,  x, y, z, 9, 0.25, 0.20, 0.25, 0.01);
        world.sendParticles(ParticleTypes.CLOUD, x, y, z,  4, 0.20, 0.10, 0.20, 0.00);

        return world.addFreshEntity(newWolf);
    }

}
