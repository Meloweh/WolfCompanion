package renderer;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class WolfPixelFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {

    private static final Identifier PIXEL_TEXTURE = WolfCompanion.vanillaResource("textures/item/apple.png"); // Example texture

    public WolfPixelFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WolfEntity wolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrices.push();

        // Position the texture on the wolf’s back; adjust the values as needed
        matrices.translate(0.0, 1.0, 0.5); // X, Y, Z position
        matrices.scale(0.3f, 0.3f, 0.3f); // Scale down for a small pixel effect

        // Rotate if needed

        // Bind the texture and render a small square
        MinecraftClient.getInstance().getTextureManager().bindTexture(PIXEL_TEXTURE);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer consumer = immediate.getBuffer(RenderLayer.getEntityTranslucent(PIXEL_TEXTURE));

        // Draw a quad (square) to represent the texture on the wolf
        matrices.push();
        matrices.translate(-0.5, 0, -0.5); // Center the texture
        matrices.scale(1.0f, 1.0f, 1.0f); // Adjust the size of the texture

        consumer.vertex(matrices.peek().getPositionMatrix(), 0, 0, 0).color(255, 255, 255, 255).texture(0, 0).light(light);
        consumer.vertex(matrices.peek().getPositionMatrix(), 1, 0, 0).color(255, 255, 255, 255).texture(1, 0).light(light);
        consumer.vertex(matrices.peek().getPositionMatrix(), 1, 1, 0).color(255, 255, 255, 255).texture(1, 1).light(light);
        consumer.vertex(matrices.peek().getPositionMatrix(), 0, 1, 0).color(255, 255, 255, 255).texture(0, 1).light(light);

        matrices.pop();
        immediate.draw();

        matrices.pop();
    }
}

