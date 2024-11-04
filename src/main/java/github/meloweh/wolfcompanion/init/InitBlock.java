package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.block.ExampleBEBlock;
import github.meloweh.wolfcompanion.block.ExampleEnergyGeneratorBlock;
import github.meloweh.wolfcompanion.block.ExampleEnergyStorageBlock;
import github.meloweh.wolfcompanion.block.ExampleTickingBEBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class InitBlock {
    public static final Block EXAMPLE_BLOCK = registerWithItem("example_block", new Block(AbstractBlock.Settings.create()
            .strength(1.5F, 6.0F)
            .requiresTool()
            ));

    public static final ExampleBEBlock EXAMPLE_BE_BLOCK = registerWithItem("example_be_block", new ExampleBEBlock(
            AbstractBlock.Settings.create()
            .strength(1.5F, 6.0F)
            .requiresTool()
    ));

    public static final ExampleTickingBEBlock EXAMPLE_TICKING_BE_BLOCK = registerWithItem("example_ticking_be_block", new ExampleTickingBEBlock(
            AbstractBlock.Settings.create()
                    .strength(1.5F, 6.0F)
                    .requiresTool()
    ));

    public static final ExampleEnergyGeneratorBlock EXAMPLE_ENERGY_GENERATOR_BLOCK = registerWithItem("example_energy_generator_block",
            new ExampleEnergyGeneratorBlock(AbstractBlock.Settings.create()
                    .strength(1.5F, 6.0F)
                    .requiresTool()));

    public static final ExampleEnergyStorageBlock EXAMPLE_ENERGY_STORAGE_BLOCK = registerWithItem("example_energy_storage_block",
            new ExampleEnergyStorageBlock(AbstractBlock.Settings.create()
                    .strength(1.5F, 6.0F)
                    .requiresTool()));

    //public static final ExampleInventoryBlock EXAMPLE_INVENTORY_BLOCK = registerWithItem("example_inventory_block",
    //        new ExampleInventoryBlock(AbstractBlock.Settings.create()
    //                .strength(1.5F, 6.0F)
    //                .requiresTool()));

    public static <T extends Block> T register(String name, T block) {
        return Registry.register(Registries.BLOCK, WolfCompanion.id(name), block);
    }

    public static <T extends Block> T registerWithItem(String name, T block, Item.Settings settings) {
        T registered = register(name, block);
        InitItem.register(name, new BlockItem(registered, settings));
        return registered;
    }

    public static <T extends Block> T registerWithItem(String name, T block) {
        T registered = register(name, block);
        InitItem.register(name, new BlockItem(registered, new Item.Settings()));
        return registered;
    }

    public static void load() {};

}
