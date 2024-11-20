package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class InitSound {
    public static final Identifier WHISTLE_SOUND_ID = WolfCompanion.id("whistle");
    public static final SoundEvent WHISTLE_SOUND_EVENT = SoundEvent.of(WHISTLE_SOUND_ID);

    public static final Identifier LONG_WHISTLE_SOUND_ID = WolfCompanion.id("long_whistle");
    public static final SoundEvent LONG_WHISTLE_SOUND_EVENT = SoundEvent.of(LONG_WHISTLE_SOUND_ID);

    public static void load() {
        Registry.register(Registries.SOUND_EVENT, WHISTLE_SOUND_ID, WHISTLE_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, LONG_WHISTLE_SOUND_ID, LONG_WHISTLE_SOUND_EVENT);
    }
}
