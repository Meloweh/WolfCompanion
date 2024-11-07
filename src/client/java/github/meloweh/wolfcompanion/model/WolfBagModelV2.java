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
public class WolfBagModelV2 extends Model {
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(WolfCompanion.id("wb"), "main");
	public static final Identifier TEXTURE_LOCATION = WolfCompanion.id("textures/entity/wb.png");
	private final ModelPart main;
	private final ModelPart left;
	private final ModelPart right;
	public WolfBagModelV2(ModelPart root) {
		super(RenderLayer::getEntitySolid);
        this.main = root.getChild("main");
		this.left = this.main.getChild("left");
		this.right = this.main.getChild("right");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData right = main.addChild("right", ModelPartBuilder.create().uv(23, 4).mirrored().cuboid(-5.0F, 0.0F, -2.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
				.uv(26, 0).mirrored().cuboid(-4.0F, 5.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
				.uv(28, 3).mirrored().cuboid(-4.0F, 0.0F, -3.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
				.uv(24, 8).mirrored().cuboid(-4.0F, -1.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)).mirrored(false)
				.uv(26, 12).mirrored().cuboid(-5.0F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
				.uv(22, 19).mirrored().cuboid(-4.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left = main.addChild("left", ModelPartBuilder.create().uv(23, 4).cuboid(8.0F, -1.0F, -2.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
				.uv(26, 0).cuboid(7.0F, 4.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
				.uv(28, 3).cuboid(7.0F, -1.0F, -3.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
				.uv(24, 8).cuboid(7.0F, -2.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
				.uv(26, 12).cuboid(8.0F, -1.0F, -1.0F, 1.0F, 5.0F, 2.0F, new Dilation(0.0F))
				.uv(22, 19).cuboid(7.0F, -1.0F, -2.0F, 1.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 1.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
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