package github.meloweh.wolfcompanion.model;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class WolfBagModel extends Model {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(WolfCompanion.id("wolf_bag"), "main");
    public static final Identifier TEXTURE_LOCATION = WolfCompanion.id("textures/entity/wolf_bag.png");

    private final ModelPart main;
    public WolfBagModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.main = root.getChild("main");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(2.6667F, -1.5F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(1, 6).cuboid(-2.3333F, -1.5F, -2.0F, 5.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(1, 13).cuboid(-2.3333F, 0.5F, -2.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(3, 20).cuboid(-2.3333F, 1.5F, -1.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 24).cuboid(-3.3333F, -1.5F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 27).cuboid(-2.3333F, -2.5F, -1.0F, 5.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.6667F, 20.5F, 2.0F));
        return TexturedModelData.of(modelData, 64, 32);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        this.main.render(matrices, vertexConsumer, light, overlay, color);
    }

    public RenderLayer getRenderLayer() {
        return RenderLayer.getEntitySolid(WolfCompanion.id("textures/entity/wolf_bag.png"));
    }
}