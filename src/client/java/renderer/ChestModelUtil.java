package renderer;

import net.minecraft.client.model.*;

public class ChestModelUtil {
    public static ModelPart createChestRoot() {
        // Define the model data structure based on ExampleChestModel's getTexturedModelData
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Define the "main" part with its child cuboids
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create()
                        .uv(37, 0)
                        .cuboid(-6.0F, -3.5F, -6.0F, 12.0F, 8.0F, 1.0F, new Dilation(0.0F))
                        .uv(36, 30)
                        .cuboid(-6.0F, -3.5F, 5.0F, 12.0F, 8.0F, 1.0F, new Dilation(0.0F))
                        .uv(23, 30)
                        .cuboid(5.0F, -3.5F, -5.0F, 1.0F, 8.0F, 10.0F, new Dilation(0.0F))
                        .uv(0, 30)
                        .cuboid(-6.0F, -3.5F, -5.0F, 1.0F, 8.0F, 10.0F, new Dilation(0.0F))
                        .uv(0, 16)
                        .cuboid(-6.0F, -4.5F, -6.0F, 12.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 4.5F, 0.0F));

        // Define the "lid" part as a child of "main" with its cuboids
        main.addChild("lid", ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-6.0F, 0.0F, -12.0F, 12.0F, 3.0F, 12.0F, new Dilation(0.0F))
                        .uv(0, 0)
                        .cuboid(-1.0F, -2.0F, -13.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 4.5F, 6.0F));

        // Return the root ModelPart with the completed structure
        return TexturedModelData.of(modelData, 64, 64).createModel();
    }
}

