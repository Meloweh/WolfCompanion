package github.meloweh.wolfcompanion.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface TickableBlockEntity {
    void tick();
    static <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pWorld) {
        return pWorld.isClientSide() ? null : ((world, pos, state, blockEntity) -> { // remove pWorld stuff if client tick wanted
            if (blockEntity instanceof TickableBlockEntity tickableBlockEntity) {
                tickableBlockEntity.tick();
            }
        });
    }
}
