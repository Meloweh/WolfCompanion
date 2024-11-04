package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.block.entity.ExampleBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockEntityTypeInit {
    public static final BlockEntityType<ExampleBlockEntity> EXAMPLE_BLOCK_ENTITY = register("example_block_entity",
            BlockEntityType.Builder.create(ExampleBlockEntity::new, InitBlock.EXAMPLE_BE_BLOCK)
                    .build());

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, WolfCompanion.id(name), type);
    }

    public static void load() {}
}
