package github.meloweh.wolfcompanion.client.renderer;

import github.meloweh.wolfcompanion.client.model.ArmadilloModel;
import github.meloweh.wolfcompanion.entity.ArmadilloEntity;
import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ArmadilloRenderer extends MobRenderer<ArmadilloEntity, ArmadilloModel> {
    private static final ResourceLocation TEXTURE = WolfCompanion.id("textures/entity/armadillo.png");

    public ArmadilloRenderer(EntityRendererProvider.Context context) {
        super(context, new ArmadilloModel(ArmadilloModel.createBodyLayer().bakeRoot()), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(ArmadilloEntity entity) {
        return TEXTURE;
    }
}
