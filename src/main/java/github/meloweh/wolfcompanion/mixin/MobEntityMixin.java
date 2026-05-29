package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.MobEntityAccessor;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public abstract class MobEntityMixin implements MobEntityAccessor {
    @Shadow
    public abstract ItemStack getBodyArmorItem();

    @Accessor("goalSelector")
    public abstract GoalSelector getGoalSelector();

    @Shadow
    protected abstract Vec3i getPickupReach();

    @Override
    public Vec3i getItemPickUpRangeExpander__() {
        return getPickupReach();
    }

    @Accessor("navigation")
    public abstract PathNavigation getNavigator__();
}
