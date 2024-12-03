package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class InitBlock {
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
