package github.meloweh.wolfcompanion.events;

import github.meloweh.wolfcompanion.accessor.WolfXpProvider;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.animal.Wolf;

public class WolfEventHandler {
    public static final String RESCUED_WOLF_NBT_KEY = "SavedWolfData";
    public static final String WHISTLE_WOLF_NBT_KEY = "WhistleWolfData";

    private static MinecraftServer server;

    public static void init() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof Wolf) {
                final WolfXpProvider wolfXp = (WolfXpProvider) entity;
                final int remaining = wolfXp.repairGear(10);
                if (remaining >= 5) {
                    wolfXp.addXp(1);
                }
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);
        ServerLifecycleEvents.SERVER_STOPPED.register(s -> server = null);
    }

    public static MinecraftServer getMinecraftServer() {
        return server;
    }
}

