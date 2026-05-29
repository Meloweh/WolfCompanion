package github.meloweh.wolfcompanion.registry;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.item.WhistleItem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {
    public static final Item SINGLE_WOLF_BAG = register("wolf_single_bag", new Item(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, WolfCompanion.id("wolf_single_bag")))
                    .stacksTo(16)
    ));
    public static final Item WOLF_BAG = register("wolf_bag_item", new Item(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, WolfCompanion.id("wolf_bag_item")))
                    .stacksTo(1)
    ));
    public static final Item DOG_WHISTLE = register("dog_whistle_item", new WhistleItem(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, WolfCompanion.id("dog_whistle_item")))
                    .stacksTo(1)
    ));

    private ModItems() {
    }

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, WolfCompanion.id(name), item);
    }

    public static void register() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(WOLF_BAG));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> entries.accept(SINGLE_WOLF_BAG));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(DOG_WHISTLE));
    }
}
