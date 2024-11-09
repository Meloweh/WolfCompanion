package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.network.BlockPosPayload;
import github.meloweh.wolfcompanion.network.OpenWolfScreenS2CPacket;
import github.meloweh.wolfcompanion.network.WolfInventoryPayload;
import github.meloweh.wolfcompanion.screenhandler.ExampleEnergyGeneratorScreenHandler;
import github.meloweh.wolfcompanion.screenhandler.ExampleInventoryScreenHandler;
import github.meloweh.wolfcompanion.screenhandler.WolfScreenHandler;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlerTypeInit {
    public static final ScreenHandlerType<ExampleEnergyGeneratorScreenHandler> EXAMPLE_ENERGY_GENERATOR =
            register("example_energy_generator", ExampleEnergyGeneratorScreenHandler::new, BlockPosPayload.PACKET_CODEC);

    public static final ScreenHandlerType<ExampleInventoryScreenHandler> EXAMPLE_INVENTORY_SCREEN_HANDLER =
            register("example_inventory", ExampleInventoryScreenHandler::new, BlockPosPayload.PACKET_CODEC);

    //public static final ScreenHandlerType<WolfScreenHandler> WOLF_INVENTORY_SCREEN_HANDLER =
    //        register("wolf_inventory", WolfScreenHandler::new, OpenWolfScreenS2CPacket.CODEC);

    public static <T extends ScreenHandler, D extends CustomPayload> ExtendedScreenHandlerType<T, D> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T, D> factory, PacketCodec<? super RegistryByteBuf, D> codec) {
        return Registry.register(Registries.SCREEN_HANDLER, WolfCompanion.id(name), new ExtendedScreenHandlerType<>(factory, codec));
    }

    public static void load() {}
}