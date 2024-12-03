package github.meloweh.wolfcompanion.model;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class WolfBagStraps extends Model {
    private final ModelPart main;
    public static final Identifier TEXTURE_LOCATION = WolfCompanion.id("textures/entity/straps.png");
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(WolfCompanion.id("straps"), "main");

    public WolfBagStraps(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.main = root.getChild("main");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(0, -3).cuboid(-3.0F, 3.0F, -3.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(0, -6).cuboid(3.0F, 3.0F, -3.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 1).cuboid(-3.0F, 3.0F, 3.0F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 2).cuboid(-3.0F, 3.0F, -3.0F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 16, 8);
    }



    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        this.main.render(matrices, vertexConsumer, light, overlay, color);
    }

    public void copyTransform(ModelPart part) {
        this.main.copyTransform(part);
    }

    public RenderLayer getRenderLayer() {
        return RenderLayer.getEntitySolid(WolfCompanion.id("textures/entity/wb.png"));
    }
}
