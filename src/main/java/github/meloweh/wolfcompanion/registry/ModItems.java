package github.meloweh.wolfcompanion.registry;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.item.WhistleItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {
    public static final Item SINGLE_WOLF_BAG = register("wolf_single_bag", new Item(
            new Item.Properties()
                    .stacksTo(16)
    ));
    public static final Item WOLF_BAG = register("wolf_bag_item", new Item(
            new Item.Properties()
                    .stacksTo(1)
    ));
    public static final Item DOG_WHISTLE = register("dog_whistle_item", new WhistleItem(
            new Item.Properties()
                    .stacksTo(1)
    ));

    private ModItems() {
    }

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, WolfCompanion.id(name), item);
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(WOLF_BAG));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> entries.accept(SINGLE_WOLF_BAG));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(DOG_WHISTLE));
    }
}
