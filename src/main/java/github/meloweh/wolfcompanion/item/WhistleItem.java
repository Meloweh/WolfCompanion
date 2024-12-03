package github.meloweh.wolfcompanion.item;

import github.meloweh.wolfcompanion.accessor.MobEntityAccessor;
import github.meloweh.wolfcompanion.init.InitSound;
import github.meloweh.wolfcompanion.util.ConfigManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WhistleItem extends Item {
    private static final int LONG_WHISTLE_START_TIME = 45;

    private int duration = 0;
    private boolean canUse = true;

    public WhistleItem(Settings settings) {
        super(settings);
    }

//    @Override
//    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
//        super.onStoppedUsing(stack, world, user, remainingUseTicks);
//        if (user instanceof PlayerEntity player && !user.getWorld().isClient) {
//            if (duration >= 45) {
//                world.playSound(
//                        null, // Player - if non-null, will play sound for every nearby player *except* the specified player
//                        user.getBlockPos(), // The position of where the sound will come from
//                        InitSound.LONG_WHISTLE_SOUND_EVENT, // The sound that will play
//                        SoundCategory.PLAYERS, // This determines which of the volume sliders affect this sound
//                        2f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
//                        1.1f // Pitch multiplier, 1 is normal, 0.5 is half pitch, etc
//                );
//            }
//            duration = 0;
//        }
//    }
//
//
//    @Override
//    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
//        duration++;
//        super.usageTick(world, user, stack, remainingUseTicks);
//    }

    public void playPositionalSound(World world, PlayerEntity sourcePlayer, SoundEvent soundEvent, float baseVolume, float pitch, double maxDistance) {
        if (!world.isClient) {
            for (PlayerEntity player : world.getPlayers()) {
                // Calculate the distance between the sound source and the player
                double distance = player.getPos().distanceTo(sourcePlayer.getPos());

                // If the player is within the maximum distance, play the sound
                if (distance <= maxDistance) {
                    // Calculate the volume attenuation
                    float volume = (float) Math.max(0, baseVolume * (1.0 - (distance / maxDistance)));

                    // Use world.playSound with the player as the audience to preserve stereo
                    world.playSound(
                            player, // Only the specific player hears the sound
                            sourcePlayer.getBlockPos(), // Position of the sound source
                            soundEvent,
                            SoundCategory.PLAYERS,
                            volume,
                            pitch
                    );
                }
            }
        }
    }



    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient) {
            world.playSound(
                    null, // Player - if non-null, will play sound for every nearby player *except* the specified player
                    user.getBlockPos(), // The position of where the sound will come from
                    InitSound.WHISTLE_SOUND_EVENT, // The sound that will play
                    SoundCategory.BLOCKS, // This determines which of the volume sliders affect this sound
                    1f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
                    1.1f // Pitch multiplier, 1 is normal, 0.5 is half pitch, etc
            );
            //((ServerPlayerEntity)user).playSound(InitSound.WHISTLE_SOUND_EVENT, 1f, 1.1f);
            //playPositionalSound(world, user, InitSound.WHISTLE_SOUND_EVENT, 1f, 1.1f, 64);
            user.setCurrentHand(hand);

            user.getServer().getWorlds().forEach(world2 -> {
                world2.getEntitiesByType(EntityType.WOLF, wolf ->
                    wolf.isTamed() &&
                    wolf.getOwner() != null &&
                    wolf.getOwner().getUuid() == user.getUuid()
                ).forEach(wolf -> {
                    if (ConfigManager.config.canTeleportSitting)
                        wolf.setSitting(false);
                    //(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, boolean resetCamera) {
                    wolf.teleport((ServerWorld) wolf.getWorld(), user.getX(), user.getY(), user.getZ(), null, user.getYaw(), user.getPitch(), false);
                    ((MobEntityAccessor)wolf).getNavigator__().stop();
                });
            });
        }

        return ActionResult.CONSUME;
        //return new ActionResult.Success(world.isClient() ? ActionResult.SwingSource.CLIENT : ActionResult.SwingSource.SERVER, ActionResult.CONSUME.itemContext());
        //return ActionResult.success(itemStack, world.isClient());
    }
}
