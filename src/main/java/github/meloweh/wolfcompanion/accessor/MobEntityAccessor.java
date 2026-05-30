package github.meloweh.wolfcompanion.accessor;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

public interface MobEntityAccessor {
    GoalSelector getGoalSelector();

    Vec3i getItemPickUpRangeExpander__();

    PathNavigation getNavigator__();
}
