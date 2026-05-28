package github.meloweh.wolfcompanion.effects;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public final class ModEffects {
    public static StatusEffect[] DEFEATED_WOLVES_PARTICLE_EFFECT = new StatusEffect[12];
    public static RegistryEntry<StatusEffect>[] DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY = new RegistryEntry[12];

    public static void register() {
        for (int i = 1; i < DEFEATED_WOLVES_PARTICLE_EFFECT.length; i++) {
            DEFEATED_WOLVES_PARTICLE_EFFECT[i] = Registry.register(
                    Registries.STATUS_EFFECT,
                    Identifier.of(WolfCompanion.MOD_ID, "defeated_wolves_" + i),
                    new DefeatedWolvesStatusEffect(StatusEffectCategory.BENEFICIAL, 0x7FBCD2)
            );

            DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i] = Registries.STATUS_EFFECT.getEntry(DEFEATED_WOLVES_PARTICLE_EFFECT[i]);
        }
    }

    private ModEffects() {}
}
