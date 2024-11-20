package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.item.WhistleItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class InitItem {
//    public static final Item TOY_ITEM = register("toy_item", new Item(new Item.Settings()));
//    public static final Item FOOD_ITEM = register("food_item", new Item(
//            new Item.Settings().food(FoodList.FOOD_FOOD_COMPONENT).maxCount(16)
//    ));
    public static final Item ITEM_SINGLE_WOLF_BAG = register("wolf_single_bag", new Item(
            new Item.Settings().maxCount(16)
    ));
    public static final Item ITEM_WOLF_BAG = register("wolf_bag_item", new Item(
            new Item.Settings().maxCount(1)
    ));
    public static final Item DOG_WHISTLE_ITEM = register("dog_whistle_item", new WhistleItem(
            new Item.Settings().maxCount(1)
    ));

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, WolfCompanion.id(name), item);
    }

    public static void load() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(ITEM_WOLF_BAG);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(ITEM_SINGLE_WOLF_BAG);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(DOG_WHISTLE_ITEM);
        });
    };
}
