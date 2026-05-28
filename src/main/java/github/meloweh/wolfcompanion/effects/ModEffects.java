package github.meloweh.wolfcompanion.effects;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class ModEffects {
    public static MobEffect[] DEFEATED_WOLVES_PARTICLE_EFFECT = new MobEffect[12];
    public static Holder<MobEffect>[] DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY = new Holder[12];

    public static void register() {
        for (int i = 1; i < DEFEATED_WOLVES_PARTICLE_EFFECT.length; i++) {
            DEFEATED_WOLVES_PARTICLE_EFFECT[i] = Registry.register(
                    BuiltInRegistries.MOB_EFFECT,
                    Identifier.fromNamespaceAndPath(WolfCompanion.MOD_ID, "defeated_wolves_" + i),
                    new DefeatedWolvesStatusEffect(MobEffectCategory.BENEFICIAL, 0x7FBCD2)
            );

            DEFEATED_WOLVES_PARTICLE_EFFECT_ENTRY[i] = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(DEFEATED_WOLVES_PARTICLE_EFFECT[i]);
        }
    }

    private ModEffects() {}
}
