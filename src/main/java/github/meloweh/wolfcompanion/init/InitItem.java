package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.item.WhistleItem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import java.util.function.Function;

public class InitItem {
    //    public static final Item TOY_ITEM = register("toy_item", new Item(new Item.Settings()));
//    public static final Item FOOD_ITEM = register("food_item", new Item(
//            new Item.Settings().food(FoodList.FOOD_FOOD_COMPONENT).maxCount(16)
//    ));
    public static final Item ITEM_SINGLE_WOLF_BAG = register("wolf_single_bag", new Item(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, WolfCompanion.id("wolf_single_bag")))
                    .stacksTo(16)
    ));
    public static final Item ITEM_WOLF_BAG = register("wolf_bag_item", new Item(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, WolfCompanion.id("wolf_bag_item")))
                    .stacksTo(1)
    ));
    public static final Item DOG_WHISTLE_ITEM = register("dog_whistle_item", new WhistleItem(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, WolfCompanion.id("dog_whistle_item")))
                    .stacksTo(1)
    ));

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, WolfCompanion.id(name), item);
    }

    public static void load() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.accept(ITEM_WOLF_BAG);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.accept(ITEM_SINGLE_WOLF_BAG);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.accept(DOG_WHISTLE_ITEM);
        });
    };
}
