package github.meloweh.wolfcompanion.accessor;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;

public interface MobEntityAccessor {
    ItemStack getBodyArmor();
    void equipBodyArmor(ItemStack stack);
    GoalSelector getGoalSelector();

    Vec3i getItemPickUpRangeExpander__();

    PathNavigation getNavigator__();
}
