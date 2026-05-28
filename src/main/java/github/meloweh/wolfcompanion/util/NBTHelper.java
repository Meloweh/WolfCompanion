package github.meloweh.wolfcompanion.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class NBTHelper {
    public static NbtCompound getWolfNBT(WolfEntity wolf) {
        if (wolf == null) {
            throw new IllegalArgumentException("Wolf entity cannot be null");
        }
        NbtCompound nbtData = new NbtCompound();
        wolf.writeNbt(nbtData);
        return nbtData;
    }

    public static void cleanRescueWolfNbt(final NbtCompound wolfNbt, final float health) {
        wolfNbt.remove("HurtTime");
        wolfNbt.remove("HurtByTimestamp");
        wolfNbt.remove("DeathTime");
        wolfNbt.remove("body_armor_item");
        wolfNbt.remove("body_armor_drop_chance");
        wolfNbt.remove("ArmorDropChances");
        wolfNbt.remove("RescueTimeout");
        wolfNbt.putFloat("FallDistance", 0f);
        wolfNbt.putFloat("Health", health);

        if (!ConfigManager.config.keepWolfInventory) {
            if (!ConfigManager.config.keepWolfArmor)
                wolfNbt.remove("ArmorItems");
            if (!ConfigManager.config.keepWolfBag)
                wolfNbt.remove("ChestedWolf");
            wolfNbt.remove("Items");
            if (!ConfigManager.config.keepXp)
                wolfNbt.putInt("XP", 0);
        }
    }

    public static boolean spawnWolfFromNbt(final ServerPlayerEntity player, final NbtCompound wolfNbt, final boolean rescue) {
        final ServerWorld world = (ServerWorld) player.getWorld();
        final WolfEntity newWolf = EntityType.WOLF.create(world);

        if (rescue) newWolf.setHealth(newWolf.getMaxHealth());
        newWolf.clearStatusEffects();
        newWolf.readNbt(wolfNbt);
        newWolf.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        newWolf.playSpawnEffects();

        ServerWorld sw = (ServerWorld) newWolf.getWorld();
        double x = newWolf.getX(), y = newWolf.getBodyY(0.5), z = newWolf.getZ();
        sw.spawnParticles(ParticleTypes.POOF,  x, y, z, 9, 0.25, 0.20, 0.25, 0.01);
        sw.spawnParticles(ParticleTypes.CLOUD, x, y, z,  4, 0.20, 0.10, 0.20, 0.00);

        return world.spawnEntity(newWolf);
    }

}
