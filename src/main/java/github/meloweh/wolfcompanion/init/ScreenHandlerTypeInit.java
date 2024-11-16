package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.network.BlockPosPayload;
import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.screenhandler.ExampleInventoryScreenHandler;
import github.meloweh.wolfcompanion.screenhandler.WolfInventoryScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenHandlerTypeInit {
    public static final ScreenHandlerType<ExampleInventoryScreenHandler> EXAMPLE_INVENTORY_SCREEN_HANDLER =
            register("example_inventory", ExampleInventoryScreenHandler::new, BlockPosPayload.PACKET_CODEC);

    public static final ScreenHandlerType<WolfInventoryScreenHandler> EXAMPLE_INVENTORY_SCREEN_HANDLER_2 =
            register("example_inventory2", WolfInventoryScreenHandler::new, UuidPayload.PACKET_CODEC);

    //public static final ScreenHandlerType<WolfScreenHandler> WOLF_INVENTORY_SCREEN_HANDLER =
    //        register("wolf_inventory", WolfScreenHandler::new, OpenWolfScreenS2CPacket.CODEC);

    public static <T extends ScreenHandler, D extends CustomPayload> ExtendedScreenHandlerType<T, D> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T, D> factory, PacketCodec<? super RegistryByteBuf, D> codec) {
        return Registry.register(Registries.SCREEN_HANDLER, WolfCompanion.id(name), new ExtendedScreenHandlerType<>(factory, codec));
    }

    public static void load() {}
}