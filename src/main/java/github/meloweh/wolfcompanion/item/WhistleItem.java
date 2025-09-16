package github.meloweh.wolfcompanion.item;

import github.meloweh.wolfcompanion.accessor.MobEntityAccessor;
import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.init.InitSound;
import github.meloweh.wolfcompanion.util.ConfigManager;
import github.meloweh.wolfcompanion.util.NBTHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

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
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // No state to clear; next press will retrigger 1st then 2nd.
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient) return;
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

            if (serverPlayerAccessor.getWhistleWolfNbts__().isEmpty()) {
                user.getServer().getWorlds().forEach(world2 -> {
                    world2.getEntitiesByType(EntityType.WOLF, wolf ->
                            wolf.isTamed() &&
                                    wolf.getOwner() != null &&
                                    wolf.getOwner().getUuid() == user.getUuid()
                    ).forEach(wolf -> {
                        final NbtCompound nbt = NBTHelper.getWolfNBT(wolf);
                        serverPlayerAccessor.queueWhistleWolfNbt__(nbt);

                        ServerWorld sw = (ServerWorld) wolf.getWorld();
                        //double x = wolf.getX(), y = wolf.getBodyY(0.5), z = wolf.getZ();
                        sw.spawnParticles(ParticleTypes.POOF,  wolf.getX(), wolf.getBodyY(0.5), wolf.getZ(), 9, 0.25, 0.20, 0.25, 0.01);
                        sw.spawnParticles(ParticleTypes.CLOUD, wolf.getX(), wolf.getBodyY(0.5), wolf.getZ(),  4, 0.20, 0.10, 0.20, 0.00);

                        wolf.discard();
                    });
                });
            } else {
                serverPlayerAccessor.spawnWhistleWolfNbts__();
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);                    // start “using” on hold

        if (!world.isClient) playWhistle(world, user, stack, 1);
        return TypedActionResult.success(stack, world.isClient());
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
            user.getServer().getWorlds().forEach(world2 -> {
                world2.getEntitiesByType(EntityType.WOLF, wolf ->
                        wolf.isTamed() &&
                                wolf.getOwner() != null &&
                                wolf.getOwner().getUuid() == user.getUuid()
                ).forEach(wolf -> {
                    if (ConfigManager.config.canTeleportSitting)
                        wolf.setSitting(false);

                    wolf.refreshPositionAndAngles(user.getX(), user.getY(), user.getZ(), user.getYaw(), user.getPitch());
                    wolf.setTarget((LivingEntity) null);
                    ((MobEntityAccessor) wolf).getNavigator__().stop();
                });
            });
        }
    }
}
