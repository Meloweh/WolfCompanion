package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.init.list.FoodList;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class InitItem {
    public static final Item TOY_ITEM = register("toy_item", new Item(new Item.Settings()));
    public static final Item FOOD_ITEM = register("food_item", new Item(
            new Item.Settings().food(FoodList.FOOD_FOOD_COMPONENT).maxCount(16)
    ));

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, WolfCompanion.id(name), item);
    }

    public static void load() {};
}
