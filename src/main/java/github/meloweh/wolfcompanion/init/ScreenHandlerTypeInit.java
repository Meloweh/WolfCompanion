package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.screenhandler.WolfInventoryScreenHandler;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ScreenHandlerTypeInit {

    public static final MenuType<WolfInventoryScreenHandler> WOLF_INVENTORY_SCREEN_HANDLER =
            register("wolf_inventory", WolfInventoryScreenHandler::new, UuidPayload.PACKET_CODEC);

    public static <T extends AbstractContainerMenu, D> ExtendedMenuType<T, D> register(String name, ExtendedMenuType.ExtendedFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        return Registry.register(BuiltInRegistries.MENU, WolfCompanion.id(name), new ExtendedMenuType<>(factory, codec));
    }

    public static void load() {}
}
