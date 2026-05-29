package github.meloweh.wolfcompanion.client.model;

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
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class WolfBagStraps extends Model<LivingEntityRenderState> {
    private final ModelPart main;
    public static final Identifier TEXTURE_LOCATION = WolfCompanion.id("textures/entity/straps.png");
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(WolfCompanion.id("straps"), "main");

    public WolfBagStraps(ModelPart root) {
        super(root, RenderTypes::entitySolid);
        this.main = root.getChild("main");
    }
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition main = modelPartData.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, -3).addBox(-3.0F, 3.0F, -3.0F, 0.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, -6).addBox(3.0F, 3.0F, -3.0F, 0.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 1).addBox(-3.0F, 3.0F, 3.0F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 2).addBox(-3.0F, 3.0F, -3.0F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(modelData, 16, 8);
    }



//    @Override
//    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
//        this.main.render(matrices, vertexConsumer, light, overlay, color);
//    }

    public void copyTransform(ModelPart part) {
        this.main.loadPose(part.storePose());
        //this.main.copyTransform(part);
    }

    /*public RenderLayer getRenderLayer() {
        return RenderLayer.getEntitySolid(WolfCompanion.id("textures/entity/wb.png"));
    }*/
}
