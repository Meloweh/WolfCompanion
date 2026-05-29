package github.meloweh.wolfcompanion.util;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.config.WolfCompanionConfig;
import github.meloweh.wolfcompanion.config.WolfConfig;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;

public final class WolfOwnershipLimits {
    private WolfOwnershipLimits() {
    }

    public static boolean canTameMoreWolves(ServerPlayer player) {
        WolfConfig config = WolfCompanionConfig.current();
        return !config.limitTamedWolves || countOwnedWolves(player, Wolf::isTame) < config.maxTamedWolves;
    }

    public static boolean canEquipMoreWolfBags(ServerPlayer player) {
        WolfConfig config = WolfCompanionConfig.current();
        return !config.limitWolfBags || countOwnedWolves(
                player,
                wolf -> wolf.isTame() && ((WolfEntityProvider) wolf).hasChestEquipped()
        ) < config.maxWolfBags;
    }

    private static int countOwnedWolves(ServerPlayer player, Predicate<Wolf> predicate) {
        int count = 0;
        UUID ownerId = player.getUUID();

        for (ServerLevel level : player.level().getServer().getAllLevels()) {
            for (Wolf wolf : level.getEntities(EntityType.WOLF, candidate -> isOwnedBy(candidate, ownerId) && predicate.test(candidate))) {
                count++;
            }
        }

        return count;
    }

    private static boolean isOwnedBy(Wolf wolf, UUID ownerId) {
        EntityReference<LivingEntity> ownerReference = wolf.getOwnerReference();
        return ownerReference != null && ownerId.equals(ownerReference.getUUID());
    }
}
