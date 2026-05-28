package github.meloweh.wolfcompanion.init;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class InitSound {
    public static final Identifier WHISTLE_SOUND_ID = WolfCompanion.id("whistle");
    public static final SoundEvent WHISTLE_SOUND_EVENT = SoundEvent.createVariableRangeEvent(WHISTLE_SOUND_ID);

    public static final Identifier LONG_WHISTLE_SOUND_ID = WolfCompanion.id("long_whistle");
    public static final SoundEvent LONG_WHISTLE_SOUND_EVENT = SoundEvent.createVariableRangeEvent(LONG_WHISTLE_SOUND_ID);

    public static void load() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, WHISTLE_SOUND_ID, WHISTLE_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, LONG_WHISTLE_SOUND_ID, LONG_WHISTLE_SOUND_EVENT);
    }
}
