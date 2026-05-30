package github.meloweh.wolfcompanion.registry;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.menu.WolfInventoryScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class ModMenuTypes {

    public static final MenuType<WolfInventoryScreenHandler> WOLF_INVENTORY =
            register("wolf_inventory", WolfInventoryScreenHandler::new);

    private ModMenuTypes() {
    }

    public static <T extends AbstractContainerMenu> ExtendedScreenHandlerType<T> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T> factory) {
        return Registry.register(BuiltInRegistries.MENU, WolfCompanion.id(name), new ExtendedScreenHandlerType<>(factory));
    }

    public static void register() {
    }
}
