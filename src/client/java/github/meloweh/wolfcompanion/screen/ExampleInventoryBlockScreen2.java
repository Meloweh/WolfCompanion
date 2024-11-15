package github.meloweh.wolfcompanion.screen;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.accessor.WolfEntityMixinProvider;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.network.SampleC2SPayload;
import github.meloweh.wolfcompanion.screenhandler.ExampleInventoryScreenHandler2;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ExampleInventoryBlockScreen2 extends HandledScreen<ExampleInventoryScreenHandler2> {
    private static final Identifier CHEST_SLOTS_TEXTURE = Identifier.ofVanilla("container/horse/chest_slots");
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/horse.png"); //WolfCompanion.id("textures/gui/container/horse.png"); //Identifier.ofVanilla("textures/gui/container/horse.png");
    private static final Identifier SLOT = Identifier.ofVanilla("container/slot");
    private final WolfEntity wolf;
    private final int slotColumnCount;
    private float mouseX;
    private float mouseY;
    private SimpleInventory wolfInventory;
    private PlayerEntity player;
    private ExampleInventoryScreenHandler2 handler;

    private static final Identifier BUTTON_CHEST_AVAILABLE = WolfCompanion.id("textures/gui/container/button_available.png");
    private static final Identifier BUTTON_CHEST_HIGHLIGHTED = WolfCompanion.id("textures/gui/container/button_highlighted.png");
    private static final Identifier BUTTON_CHEST_DISABLED = WolfCompanion.id("textures/gui/container/button_disabled.png");
    private static final Identifier WOLF_ARMOR_SLOT = WolfCompanion.id("textures/gui/container/icon_wolf_armor.png");

    public ExampleInventoryBlockScreen2(ExampleInventoryScreenHandler2 handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 184;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        //this.entity = inventory.player;
        this.slotColumnCount = 5;
        this.wolf = handler.getWolf();
        this.wolfInventory = handler.getWolfInventory();
        this.player = inventory.player;
        this.handler = handler;
    }

    @Override
    public void close() {
        super.close();

        //if (player.getWorld().isClient)
        //    ClientPlayNetworking.send(new SampleC2SPayload("It says we are on " + player.getWorld().isClient, 69));
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (this.slotColumnCount > 0) {
            context.drawGuiTexture(CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, i + 79, j + 17, this.slotColumnCount * 18, 54);
        }

        if (this.wolf.hasArmor()) {
            context.drawGuiTexture(SLOT, i + 7, j + 35 - 18, 18, 18);
        } else {
            context.drawTexture(WOLF_ARMOR_SLOT, i + 7, j + 35 - 18, 0, 0, 18, 18, 18, 18);
        }

        if (((WolfEntityProvider)this.wolf).hasChestEquipped()) {
            if (this.mouseX >= i + 7 &&
                    this.mouseX < i + 7 + 18 &&
                    this.mouseY >= j + 35 &&
                    this.mouseY < j + 35 + 18) {
                context.drawTexture(BUTTON_CHEST_HIGHLIGHTED, i + 7, j + 35, 0, 0, 18, 18, 18, 18);
            } else {
                context.drawTexture(BUTTON_CHEST_AVAILABLE, i + 7, j + 35, 0, 0, 18, 18, 18, 18);
            }
        } else {
            context.drawTexture(BUTTON_CHEST_DISABLED, i + 7, j + 35, 0, 0, 18, 18, 18, 18);
        }

        InventoryScreen.drawEntity(context, i + 26, j + 18, i + 78, j + 70, 33, 0.25F, this.mouseX, this.mouseY, this.wolf);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}