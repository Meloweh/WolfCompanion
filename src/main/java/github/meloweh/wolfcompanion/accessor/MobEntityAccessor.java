package github.meloweh.wolfcompanion.accessor;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.item.ItemStack;

public interface MobEntityAccessor {
    ItemStack getBodyArmor();
    void equipBodyArmor(ItemStack stack);
    GoalSelector getGoalSelector();
    //void playAttackSound();
}
