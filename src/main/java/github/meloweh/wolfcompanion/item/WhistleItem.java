package github.meloweh.wolfcompanion.item;

import github.meloweh.wolfcompanion.accessor.MobEntityAccessor;
import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.init.InitSound;
import github.meloweh.wolfcompanion.util.ConfigManager;
import github.meloweh.wolfcompanion.util.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class WhistleItem extends Item {
    private static final int SECOND_WHISTLE_TICKS = 30; // 20

    public WhistleItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) { return UseAction.NONE; } // visual; change if desired

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) { return 72000; } // hold-to-use behavior

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // No state to clear; next press will retrigger 1st then 2nd.
        return false;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient()) return;
        int used = getMaxUseTime(stack, user) - remainingUseTicks;

        if (used == SECOND_WHISTLE_TICKS) {          // fires once per hold
            playWhistle(world, user, stack, 2);
            onSecondWhistle(world, user, stack);     // your custom second-whistle logic
            // Optional hard stop after second:
            // user.stopUsingItem();
        }
    }

    /** Override or fill with effects for the second whistle. */
    protected void onSecondWhistle(World world, LivingEntity user, ItemStack stack) {
        if (user instanceof ServerPlayerEntity serverPlayer) {
            final ServerPlayerAccessor serverPlayerAccessor = (ServerPlayerAccessor) serverPlayer;

            if (serverPlayerAccessor.getWhistleWolfNbts__().isEmpty() && !serverPlayerAccessor.hasElapsed__() ) {
                serverPlayerAccessor.getServer__().getWorlds().forEach(world2 -> {
                    world2.getEntitiesByType(EntityType.WOLF, wolf ->
                            wolf.isTamed() &&
                                    wolf.getOwner() != null &&
                                    wolf.getOwner().getUuid() == user.getUuid() &&
                                    !((WolfEntityProvider) wolf).isLock__()
                    ).forEach(wolf -> {
                        final NbtCompound nbt = NBTHelper.getWolfNBT(wolf);
                        serverPlayerAccessor.queueWhistleWolfNbt__(nbt);

                        ServerWorld sw = (ServerWorld) wolf.getEntityWorld();
                        sw.spawnParticles(ParticleTypes.POOF,  wolf.getX(), wolf.getBodyY(0.5), wolf.getZ(), 9, 0.25, 0.20, 0.25, 0.01);
                        sw.spawnParticles(ParticleTypes.CLOUD, wolf.getX(), wolf.getBodyY(0.5), wolf.getZ(),  4, 0.20, 0.10, 0.20, 0.00);

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
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);                    // start “using” on hold

        if (!world.isClient()) playWhistle(world, user, stack, 1);
        return ActionResult.CONSUME;
    }

    private void playWhistle(World world, LivingEntity user, ItemStack stack, int stage) {
        // Positional attenuation from the moving entity
        world.playSoundFromEntity(
                null,
                user,
                InitSound.WHISTLE_SOUND_EVENT,
                SoundCategory.PLAYERS,
                1.0f,
                stage == 2 ? 1.0f : 1.1f
        );

        //user.setCurrentHand(hand);

        if (stage == 1) {
            if (user instanceof ServerPlayerEntity serverPlayer) {
                final Optional<LivingEntity> target = getLookedAtEntity(serverPlayer);
                final ServerPlayerAccessor serverPlayerAccessor = (ServerPlayerAccessor) serverPlayer;

                if (target.isEmpty()) {
                    serverPlayerAccessor.getServer__().getWorlds().forEach(world2 -> {
                        world2.getEntitiesByType(EntityType.WOLF, wolf ->
                                wolf.isTamed() &&
                                        wolf.getOwner() != null &&
                                        wolf.getOwner().getUuid() == user.getUuid() &&
                                        !((WolfEntityProvider) wolf).isLock__()
                        ).forEach(wolf -> {
                            if (ConfigManager.config.canTeleportSitting)
                                wolf.setSitting(false);

                            wolf.refreshPositionAndAngles(user.getX(), user.getY(), user.getZ(), user.getYaw(), user.getPitch());
                            wolf.setTarget((LivingEntity) null);
                            ((MobEntityAccessor) wolf).getNavigator__().stop();
                        });
                    });
                } else {
                    serverPlayerAccessor.getServer__().getWorlds().forEach(world2 -> {
                        world2.getEntitiesByType(EntityType.WOLF, wolf ->
                                wolf.isTamed() &&
                                        wolf.getOwner() != null &&
                                        wolf.getOwner().getUuid() == user.getUuid() &&
                                        !((WolfEntityProvider) wolf).isLock__()
                        ).forEach(wolf -> {
                            if (!wolf.isSitting()) {
                                wolf.setTarget(target.get());
                            }
                        });
                    });
                }
            }
        }
    }

    public static Optional<LivingEntity> getLookedAtEntity(ServerPlayerEntity player) {
        float tickDelta = 1.0F;

        // Camera position
        Vec3d cameraPos = player.getCameraPosVec(tickDelta);

        // Look direction
        Vec3d rotationVec = player.getRotationVec(tickDelta);

        // End of ray
        Vec3d endPos = cameraPos.add(rotationVec.multiply(ConfigManager.config.teleportAtDistance));

        // Expand search box along ray
        Box searchBox = player.getBoundingBox()
                .stretch(rotationVec.multiply(ConfigManager.config.teleportAtDistance))
                .expand(1.0D);

        // Perform entity raycast
        EntityHitResult entityHit = ProjectileUtil.raycast(
                player,
                cameraPos,
                endPos,
                searchBox,
                entity -> !entity.isSpectator() && entity.canHit() && entity instanceof LivingEntity && entity.isAlive(),
                ConfigManager.config.teleportAtDistance * ConfigManager.config.teleportAtDistance
        );

        if (entityHit != null) {
            return Optional.ofNullable((LivingEntity) entityHit.getEntity());
        }

        return Optional.empty();
    }

}