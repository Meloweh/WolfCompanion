package github.meloweh.wolfcompanion.item;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.init.InitSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WhistleItem extends Item {
    public WhistleItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient) {
            world.playSound(
                    null, // Player - if non-null, will play sound for every nearby player *except* the specified player
                    user.getBlockPos(), // The position of where the sound will come from
                    InitSound.WHISTLE_SOUND_EVENT, // The sound that will play
                    SoundCategory.BLOCKS, // This determines which of the volume sliders affect this sound
                    2f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
                    1.1f // Pitch multiplier, 1 is normal, 0.5 is half pitch, etc
            );

        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
