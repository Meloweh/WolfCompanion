package github.meloweh.wolfcompanion.model;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class WolfChestModel extends EntityModel {
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(WolfCompanion.id("wolf_chest"), "main");
	public static final Identifier TEXTURE_LOCATION = WolfCompanion.id("textures/entity/wolf_chest.png");

	private final ModelPart main;
	private final ModelPart lid;
	public WolfChestModel(ModelPart root) {
		this.main = root.getChild("main");
		this.lid = this.main.getChild("lid");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 20).cuboid(-4.0F, -4.0F, -1.0F, 5.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData lid = main.addChild("lid", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -6.0F, 0.0F, 5.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		this.main.render(matrices, vertexConsumer, light, overlay, color);
	}

	public RenderLayer getRenderLayer() {
		return RenderLayer.getEntitySolid(WolfCompanion.id("textures/entity/wolf_chest.png"));
	}

	@Override
	public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}