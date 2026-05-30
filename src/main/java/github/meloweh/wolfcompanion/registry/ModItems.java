package github.meloweh.wolfcompanion.registry;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.item.WhistleItem;
import github.meloweh.wolfcompanion.item.WolfArmorItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class ModItems {
    public static final Item SINGLE_WOLF_BAG = register("wolf_single_bag", new Item(
            new Item.Properties()
                    .stacksTo(16)
    ));
    public static final Item WOLF_BAG = register("wolf_bag_item", new Item(
            new Item.Properties()
                    .stacksTo(1)
    ));
    public static final Item ARMADILLO_SCUTE = register("armadillo_scute", new Item(
            new Item.Properties()
    ));
    public static final Item ARMADILLO_SPAWN_EGG = register("armadillo_spawn_egg", new SpawnEggItem(
            ModEntities.ARMADILLO,
            0x8D765A,
            0xD7C098,
            new Item.Properties()
    ));
    public static final Item WOLF_ARMOR = register("wolf_armor", new WolfArmorItem(
            new Item.Properties()
                    .durability(WolfArmorItem.DURABILITY)
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

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> entries.accept(ARMADILLO_SCUTE));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS)
                .register(entries -> entries.accept(ARMADILLO_SPAWN_EGG));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(WOLF_ARMOR));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(DOG_WHISTLE));
    }
}
