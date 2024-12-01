package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.MobEntityAccessor;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements MobEntityAccessor {
    @Accessor("bodyArmor")
    public abstract ItemStack getBodyArmor();

    //@Invoker("equipBodyArmor")
    //public abstract void equipBodyArmor(ItemStack stack);

    @Accessor("goalSelector")
    public abstract GoalSelector getGoalSelector();

    @Shadow
    protected abstract Vec3i getItemPickUpRangeExpander();

    @Override
    public Vec3i getItemPickUpRangeExpander__() {
        return getItemPickUpRangeExpander();
    }

    /*
    @Invoker("hasPassenger")
    public abstract boolean hasPassenger(Entity passenger);

     */
}

/*

@Mixin(WolfEntityModel.class)
public abstract class WolfEntityModelMixin implements WolfEntityModelAccessor {
    @Accessor("torso")
    public abstract ModelPart getTorso();

    @Accessor("torso")
    public abstract ModelPart getTorso();

public interface WolfEntityModelAccessor {
    ModelPart getTorso();
}
 */
