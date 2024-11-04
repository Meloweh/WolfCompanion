package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.block.entity.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockEntityTypeInit {
    public static final BlockEntityType<ExampleBlockEntity> EXAMPLE_BLOCK_ENTITY = register("example_block_entity",
            BlockEntityType.Builder.create(ExampleBlockEntity::new, InitBlock.EXAMPLE_BE_BLOCK)
                    .build());

    public static final BlockEntityType<ExampleTickingBlockEntity> EXAMPLE_TICKING_BLOCK_ENTITY = register("example_ticking_block_entity",
            BlockEntityType.Builder.create(ExampleTickingBlockEntity::new, InitBlock.EXAMPLE_TICKING_BE_BLOCK)
                    .build());

    public static final BlockEntityType<ExampleEnergyGeneratorBlockEntity> EXAMPLE_ENERGY_GENERATOR = register("example_energy_generator",
            BlockEntityType.Builder.create(ExampleEnergyGeneratorBlockEntity::new, InitBlock.EXAMPLE_ENERGY_GENERATOR_BLOCK)
                    .build());

    public static final BlockEntityType<ExampleEnergyStorageBlockEntity> EXAMPLE_ENERGY_STORAGE = register("example_energy_storage",
            BlockEntityType.Builder.create(ExampleEnergyStorageBlockEntity::new, InitBlock.EXAMPLE_ENERGY_STORAGE_BLOCK)
                    .build());

    public static final BlockEntityType<ExampleInventoryBlockEntity> EXAMPLE_INVENTORY_BLOCK_ENTITY = register("example_inventory_block_entity",
            BlockEntityType.Builder.create(ExampleInventoryBlockEntity::new, InitBlock.EXAMPLE_INVENTORY_BLOCK)
                    .build());

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, WolfCompanion.id(name), type);
    }

    public static void load() {}
}
