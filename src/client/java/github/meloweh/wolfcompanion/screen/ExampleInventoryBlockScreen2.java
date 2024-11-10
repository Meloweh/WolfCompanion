package github.meloweh.wolfcompanion.screen;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.screenhandler.ExampleInventoryScreenHandler;
import github.meloweh.wolfcompanion.screenhandler.ExampleInventoryScreenHandler2;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExampleInventoryBlockScreen2 extends HandledScreen<ExampleInventoryScreenHandler2> {
    private static final Identifier TEXTURE = WolfCompanion.id("textures/gui/container/example_inventory_block.png");

    public ExampleInventoryBlockScreen2(ExampleInventoryScreenHandler2 handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 184;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}