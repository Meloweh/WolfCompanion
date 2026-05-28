package github.meloweh.wolfcompanion.model;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import java.util.function.Function;

public class WolfBagModelV2 extends Model<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(WolfCompanion.id("wb"), "main");
	public static final Identifier STRAP_LAYER_TEXTURE = WolfCompanion.id("textures/entity/wa.png");
	public static final Identifier TEXTURE_LOCATION = WolfCompanion.id("textures/entity/wb.png");
	private final ModelPart main;
	private final ModelPart left;
	private final ModelPart right;
	public WolfBagModelV2(ModelPart root) {
        super(root, textureId -> Sheets.solidBlockSheet());
        this.main = root.getChild("main");
		this.left = this.main.getChild("left");
		this.right = this.main.getChild("right");
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition main = modelPartData.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition right = main.addOrReplaceChild("right", CubeListBuilder.create().texOffs(23, 4).mirror().addBox(-5.0F, 2.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(28, 3).mirror().addBox(-4.0F, 2.0F, -3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(26, 12).mirror().addBox(-5.0F, 1.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(22, 19).mirror().addBox(-4.0F, 1.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left = main.addOrReplaceChild("left", CubeListBuilder.create().texOffs(23, 4).addBox(8.0F, 1.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(28, 3).addBox(7.0F, 1.0F, -3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(26, 12).addBox(8.0F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(22, 19).addBox(7.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 1.0F, 0.0F));
		return LayerDefinition.create(modelData, 32, 32);
	}

    public void copyTransform(ModelPart part) {
        this.main.loadPose(part.storePose());
		//this.main.copyTransform(part);
	}

    /*public RenderLayer getRenderLayer() {
		return RenderLayer.getEntitySolid(WolfCompanion.id("textures/entity/wb.png"));
	}*/
}