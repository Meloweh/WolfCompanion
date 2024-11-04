package github.meloweh.wolfcompanion.init.list;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FoodList {
    public static final FoodComponent FOOD_FOOD_COMPONENT = new FoodComponent.Builder()
            .nutrition(4)
            .saturationModifier(0.3F)
            .alwaysEdible()
            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 60, 1), 0.8F)
            .build();

}
