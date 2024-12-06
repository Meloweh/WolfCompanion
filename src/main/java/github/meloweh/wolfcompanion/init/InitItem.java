package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.item.WhistleItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class InitItem {
//    public static final Item TOY_ITEM = register("toy_item", new Item(new Item.Settings()));
//    public static final Item FOOD_ITEM = register("food_item", new Item(
//            new Item.Settings().food(FoodList.FOOD_FOOD_COMPONENT).maxCount(16)
//    ));

    public static final Identifier DOG_WHISTLE_ITEM_ID = WolfCompanion.id("dog_whistle_item");
    public static final Identifier ITEM_SINGLE_WOLF_BAG_ID = WolfCompanion.id("wolf_single_bag");
    public static final Identifier ITEM_WOLF_BAG_ID = WolfCompanion.id("wolf_bag_item");

//    public static final Item ITEM_SINGLE_WOLF_BAG = register("wolf_single_bag", new Item(
//            new Item.Settings()
//                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, ITEM_SINGLE_WOLF_BAG_ID))
//                    .maxCount(16)
//    ));
//    public static final Item ITEM_WOLF_BAG = register("wolf_bag_item", new Item(
//            new Item.Settings()
//                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, ITEM_WOLF_BAG_ID))
//                    .maxCount(1)
//    ));
//    public static final Item DOG_WHISTLE_ITEM = register("dog_whistle_item", new WhistleItem(
//            new Item.Settings()
//                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, DOG_WHISTLE_ITEM_ID))
//                    .maxCount(1)
//    ));

    public static final Item ITEM_SINGLE_WOLF_BAG = registerItem(ITEM_WOLF_BAG_ID, Item::new, new Item.Settings().maxCount(16));
    public static final Item ITEM_WOLF_BAG = registerItem(ITEM_SINGLE_WOLF_BAG_ID, Item::new, new Item.Settings().maxCount(1));
    public static final Item DOG_WHISTLE_ITEM = registerItem(DOG_WHISTLE_ITEM_ID, Item::new, new Item.Settings().maxCount(1));

    public static Item registerItem(Identifier path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        return Items.register(RegistryKey.of(RegistryKeys.ITEM, path), factory, settings);
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
