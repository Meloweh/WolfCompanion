package github.meloweh.wolfcompanion.block;

import github.meloweh.wolfcompanion.block.entity.ExampleBlockEntity;
import github.meloweh.wolfcompanion.init.BlockEntityTypeInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ExampleBEBLock extends Block implements BlockEntityProvider {
    public ExampleBEBLock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ExampleBlockEntity exampleBlockEntity && player != null) {
                if (player.isSneaking()) {
                    player.sendMessage(Text.of(exampleBlockEntity.getCounter() + ""), true);
                } else {
                    exampleBlockEntity.incrementCounter();
                }
            }
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityTypeInit.EXAMPLE_BLOCK_ENTITY.instantiate(pos, state);
    }
}
