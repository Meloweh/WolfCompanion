package github.meloweh.wolfcompanion.events;

import github.meloweh.wolfcompanion.accessor.WolfXpProvider;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.passive.WolfEntity;

public class WolfEventHandler {
    public static final String RESCUED_WOLF_NBT_KEY = "SavedWolfData";
    public static final String WHISTLE_WOLF_NBT_KEY = "WhistleWolfData";

    public static void init() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof WolfEntity) {
                final WolfXpProvider wolfXp = (WolfXpProvider) entity;
                final int remaining = wolfXp.repairGear(10);
                if (remaining >= 5) {
                    wolfXp.addXp(1);
                }
            }
        });
    }
}


