package github.meloweh.wolfcompanion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.world.entity.ExperienceOrb;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbEntityMixin {

//    @Inject(method = "tick", at = @At("HEAD"))
//    private void collectXP(CallbackInfo ci) {
//        final ExperienceOrbEntity orb = (ExperienceOrbEntity) (Object) this;
//        if (!orb.getWorld().isClient()) {
//            Box range = orb.getBoundingBox().expand(1.0D);
//
//            // Get all nearby entities (living entities only)
//            List<LivingEntity> entities = orb.getWorld().getEntitiesByClass(LivingEntity.class, range, entity -> entity instanceof WolfEntity);
//
//            if (!entities.isEmpty()) {
//                // Let the first nearby zombie collect the XP
//                WolfEntity wolf = (WolfEntity) entities.get(0);
//                //wolf.addExperience(orb.getExperienceAmount());
//                orb.discard(); // Remove the orb from the world
//            }
//        }
//    }
}
