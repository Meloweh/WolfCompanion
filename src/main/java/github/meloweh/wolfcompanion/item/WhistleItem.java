package github.meloweh.wolfcompanion.item;

import github.meloweh.wolfcompanion.accessor.MobEntityAccessor;
import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.init.InitSound;
import github.meloweh.wolfcompanion.util.ConfigManager;
import github.meloweh.wolfcompanion.util.NBTHelper;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class WhistleItem extends Item {
    private static final int SECOND_WHISTLE_TICKS = 30; // 20

    public WhistleItem(Properties settings) {
        super(settings);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.NONE; } // visual; change if desired

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) { return 72000; } // hold-to-use behavior

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        // No state to clear; next press will retrigger 1st then 2nd.
        return false;
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClientSide()) return;
        int used = getUseDuration(stack, user) - remainingUseTicks;

        if (used == SECOND_WHISTLE_TICKS) {          // fires once per hold
            playWhistle(world, user, stack, 2);
            onSecondWhistle(world, user, stack);     // your custom second-whistle logic
            // Optional hard stop after second:
            // user.stopUsingItem();
        }
    }

    /** Override or fill with effects for the second whistle. */
    protected void onSecondWhistle(Level world, LivingEntity user, ItemStack stack) {
        if (user instanceof ServerPlayer serverPlayer) {
            final ServerPlayerAccessor serverPlayerAccessor = (ServerPlayerAccessor) serverPlayer;

            if (serverPlayerAccessor.getWhistleWolfNbts__().isEmpty() && !serverPlayerAccessor.hasElapsed__() ) {
                serverPlayerAccessor.getServer__().getAllLevels().forEach(world2 -> {
                    world2.getEntities(EntityType.WOLF, wolf ->
                            wolf.isTame() &&
                                    wolf.getOwner() != null &&
                                    wolf.getOwner().getUUID() == user.getUUID() &&
                                    !((WolfEntityProvider) wolf).isLock__()
                    ).forEach(wolf -> {
                        final CompoundTag nbt = NBTHelper.getWolfNBT(wolf);
                        serverPlayerAccessor.queueWhistleWolfNbt__(nbt);

                        ServerLevel sw = (ServerLevel) wolf.level();
                        sw.sendParticles(ParticleTypes.POOF,  wolf.getX(), wolf.getY(0.5), wolf.getZ(), 9, 0.25, 0.20, 0.25, 0.01);
                        sw.sendParticles(ParticleTypes.CLOUD, wolf.getX(), wolf.getY(0.5), wolf.getZ(),  4, 0.20, 0.10, 0.20, 0.00);

                        wolf.discard();
                    });
                });
            } else {
                serverPlayerAccessor.spawnWhistleWolfNbts__();
                serverPlayerAccessor.spawnElapsedRescueWolfNbts__();
            }
        }
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        user.startUsingItem(hand);                    // start “using” on hold

        if (!world.isClientSide()) playWhistle(world, user, stack, 1);
        return InteractionResult.CONSUME;
    }

    private void playWhistle(Level world, LivingEntity user, ItemStack stack, int stage) {
        // Positional attenuation from the moving entity
        world.playSound(
                null,
                user,
                InitSound.WHISTLE_SOUND_EVENT,
                SoundSource.PLAYERS,
                1.0f,
                stage == 2 ? 1.0f : 1.1f
        );

        //user.setCurrentHand(hand);

        if (stage == 1) {
            if (user instanceof ServerPlayer serverPlayer) {
                final Optional<LivingEntity> target = getLookedAtEntity(serverPlayer);
                final ServerPlayerAccessor serverPlayerAccessor = (ServerPlayerAccessor) serverPlayer;

                if (target.isEmpty()) {
                    serverPlayerAccessor.getServer__().getAllLevels().forEach(world2 -> {
                        world2.getEntities(EntityType.WOLF, wolf ->
                                wolf.isTame() &&
                                        wolf.getOwner() != null &&
                                        wolf.getOwner().getUUID() == user.getUUID() &&
                                        !((WolfEntityProvider) wolf).isLock__()
                        ).forEach(wolf -> {
                            if (ConfigManager.config.canTeleportSitting)
                                wolf.setOrderedToSit(false);

                            wolf.snapTo(user.getX(), user.getY(), user.getZ(), user.getYRot(), user.getXRot());
                            wolf.stopBeingAngry();
                            ((MobEntityAccessor) wolf).getNavigator__().stop();
                        });
                    });
                } else {
                    serverPlayerAccessor.getServer__().getAllLevels().forEach(world2 -> {
                        world2.getEntities(EntityType.WOLF, wolf ->
                                wolf.isTame() &&
                                        wolf.getOwner() != null &&
                                        wolf.getOwner().getUUID() == user.getUUID() &&
                                        !((WolfEntityProvider) wolf).isLock__()
                        ).forEach(wolf -> {
                            if (!wolf.isOrderedToSit() && target.get() != wolf) {
                                wolf.setTarget(target.get());
                            }
                        });
                    });
                }
            }
        }
    }

    public static Optional<LivingEntity> getLookedAtEntity(ServerPlayer player) {
        float tickDelta = 1.0F;

        // Camera position
        Vec3 cameraPos = player.getEyePosition(tickDelta);

        // Look direction
        Vec3 rotationVec = player.getViewVector(tickDelta);

        // End of ray
        Vec3 endPos = cameraPos.add(rotationVec.scale(ConfigManager.config.teleportAtDistance));

        // Expand search box along ray
        AABB searchBox = player.getBoundingBox()
                .expandTowards(rotationVec.scale(ConfigManager.config.teleportAtDistance))
                .inflate(1.0D);

        // Perform entity raycast
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                player,
                cameraPos,
                endPos,
                searchBox,
                entity -> !entity.isSpectator() && entity.isPickable() && entity instanceof LivingEntity && entity.isAlive(),
                ConfigManager.config.teleportAtDistance * ConfigManager.config.teleportAtDistance
        );

        if (entityHit != null) {
            return Optional.ofNullable((LivingEntity) entityHit.getEntity());
        }

        return Optional.empty();
    }

}