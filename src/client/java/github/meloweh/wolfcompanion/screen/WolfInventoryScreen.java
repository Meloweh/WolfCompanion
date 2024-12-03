package github.meloweh.wolfcompanion.screen;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.accessor.WolfXpProvider;
import github.meloweh.wolfcompanion.network.DropWolfChestC2SPayload;
import github.meloweh.wolfcompanion.screenhandler.WolfInventoryScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WolfInventoryScreen extends HandledScreen<WolfInventoryScreenHandler> {
    private static final Identifier CHEST_SLOTS_TEXTURE = Identifier.ofVanilla("container/horse/chest_slots");
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/horse.png"); //WolfCompanion.id("textures/gui/container/horse .png"); //Identifier.ofVanilla("textures/gui/container/horse.png");
    private static final Identifier SLOT = Identifier.ofVanilla("container/slot");
    //private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = Identifier.ofVanilla("container/villager/experience_bar_background");
    //private static final Identifier EXPERIENCE_BAR_CURRENT_TEXTURE = Identifier.ofVanilla("container/villager/experience_bar_current");
    private static final Identifier EXPERIENCE_BAR_RESULT_TEXTURE = Identifier.ofVanilla("container/villager/experience_bar_result");
    //private static final Identifier HEART = Identifier.ofVanilla("textures/gui/sprites/hud/heart/full");
    //private static final Identifier HEART_CONTAINER = Identifier.ofVanilla("textures/gui/sprites/hud/heart/container.png");
    private static final Identifier HALF_HEART = Identifier.ofVanilla("textures/gui/sprites/hud/heart/half");

    private final WolfEntity wolf;
    private final WolfXpProvider wolfXp;
    private final int slotColumnCount;
    private float mouseX;
    private float mouseY;
    private SimpleInventory wolfInventory;
    private PlayerEntity player;
    private WolfInventoryScreenHandler handler;

    private static final Identifier BUTTON_CHEST_AVAILABLE = WolfCompanion.id("textures/gui/container/button_available.png");
    private static final Identifier BUTTON_CHEST_HIGHLIGHTED = WolfCompanion.id("textures/gui/container/button_highlighted.png");
    private static final Identifier BUTTON_CHEST_DISABLED = WolfCompanion.id("textures/gui/container/button_disabled.png");
    private static final Identifier WOLF_ARMOR_SLOT = WolfCompanion.id("textures/gui/container/icon_wolf_armor.png");
    private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = WolfCompanion.id("textures/gui/container/wolf_experience_bar_background_v2.png");
    private static final Identifier EXPERIENCE_BAR_CURRENT_TEXTURE = WolfCompanion.id("textures/gui/container/wolf_experience_bar_current_v2.png");
    private static final Identifier HEART_CONTAINER = WolfCompanion.id("textures/gui/container/container.png");
    private static final Identifier HEART = WolfCompanion.id("textures/gui/container/heart.png");

    public WolfInventoryScreen(WolfInventoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 184;
        this.playerInventoryTitleY = this.backgroundHeight - 111;
        this.slotColumnCount = 5;
        this.wolf = handler.getWolf();
        this.wolfXp = (WolfXpProvider) this.wolf;
        this.wolfInventory = handler.getWolfInventory();
        this.player = inventory.player;
        this.handler = handler;
    }

    @Override
    public void close() {
        super.close();
    }

    private boolean clickedDropChest(double mouseX, double mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        return mouseX >= i + 7 &&
                mouseX < i + 7 + 18 &&
                mouseY >= j + 35 &&
                mouseY < j + 35 + 18;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (clickedDropChest(mouseX, mouseY)) {
            ClientPlayNetworking.send(new DropWolfChestC2SPayload(wolf.getUuid()));
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3f, 0.8f);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);

        if (((WolfEntityProvider)wolf).hasChestEquipped()) {
            if (this.slotColumnCount > 0) {
                context.drawGuiTexture(RenderLayer::getGuiTextured, CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, i + 79, j + 17, this.slotColumnCount * 18, 54);
            }
        }

        if (this.wolf.isWearingBodyArmor()) {
            context.drawGuiTexture(RenderLayer::getGuiTextured, SLOT, i + 7, j + 35 - 18, 18, 18);
        } else {
            context.drawGuiTexture(RenderLayer::getGuiTextured, WOLF_ARMOR_SLOT, i + 7, j + 35 - 18, 18, 18);
        }

        if (((WolfEntityProvider)this.wolf).hasChestEquipped()) {
            if (this.mouseX >= i + 7 &&
                    this.mouseX < i + 7 + 18 &&
                    this.mouseY >= j + 35 &&
                    this.mouseY < j + 35 + 18) {
                context.drawTexture(RenderLayer::getGuiTextured, BUTTON_CHEST_HIGHLIGHTED, i + 7, j + 35, 0, 0, 18, 18, 18, 18);
                this.setTooltip(Text.of("Drop bag and items"));
            } else {
                context.drawTexture(RenderLayer::getGuiTextured, BUTTON_CHEST_AVAILABLE, i + 7, j + 35, 0, 0, 18, 18, 18, 18);
            }
        } else {
            //context.drawTexture(BUTTON_CHEST_DISABLED, i + 7, j + 35, 0, 0, 18, 18, 18, 18);
        }

        InventoryScreen.drawEntity(context, i + 26, j + 18, i + 78, j + 70, 33, 0.25F, this.mouseX, this.mouseY, this.wolf);
    }

    private void drawHearts(DrawContext context) {
        final int WIDTH = 8, HEIGHT = 9;

        int x = (this.width) / 2;
        int y = (this.height) / 2 - 10 - HEIGHT;

        final int maxHealthPoints = (int) wolf.getMaxHealth() / 4;
        //final float healthPercentage = wolf.getHealth() / wolf.getMaxHealth();
        final int healthPixels = (int) wolf.getHealth() * 2 + 1;
        //System.out.println(wolf.getMaxHealth() + " " + wolf.getHealth());

        context.drawTexture(RenderLayer::getGuiTextured, HEART_CONTAINER, x, y, 0, 0, WIDTH * maxHealthPoints + 1, HEIGHT, WIDTH, HEIGHT);
        context.drawTexture(RenderLayer::getGuiTextured, HEART, x, y, 0, 0, healthPixels, HEIGHT, WIDTH, HEIGHT);
        //context.drawTexture(WOLF_ARMOR_SLOT, i + 7, j + 35 - 18, 0, 0, 18, 18, 18, 18);

    }

    private void drawLevelInfo(DrawContext context) {
        final int WIDTH = 30;
        final int HEIGHT = 5;

        int x = this.width / 2 + this.backgroundWidth / 2 - WIDTH - 7;
        int y = (this.height - this.backgroundHeight) / 2 + HEIGHT + 3 - 1;

        final int xp = wolfXp.getXp();
        final int level = wolfXp.getLevel();
        final int maxXp = wolfXp.getNextLevelXpRequirement(level + 1);
        final int prevMaxXp = wolfXp.getNextLevelXpRequirement(level);
        final int deltaMaxXp = maxXp - prevMaxXp;
        final int deltaXp = xp - prevMaxXp;

        //System.out.println("xp: " + xp + " level: " + level + " maxXp: " + maxXp + " prevMaxXp: " + prevMaxXp + " deltaMaxXp: " + deltaMaxXp + " deltaXp: " + deltaXp);

        String xpText = (level < 1) ? "" : level + "";
        int xpTextWidth = textRenderer.getWidth(xpText);
        context.drawText(textRenderer, xpText, x + WIDTH / 2 - xpTextWidth / 2, y - 4 + 2, 0X7EFC20, true);

        context.drawTexture(RenderLayer::getGuiTextured, EXPERIENCE_BAR_BACKGROUND_TEXTURE, x, y, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        final int currentXpBar = WIDTH * (deltaXp / deltaMaxXp);
        context.drawTexture(RenderLayer::getGuiTextured, EXPERIENCE_BAR_CURRENT_TEXTURE, x, y, 0, 0, WIDTH * deltaXp / deltaMaxXp, HEIGHT, WIDTH, HEIGHT);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        this.drawLevelInfo(context);
        this.drawHearts(context);
        //context.drawText(MinecraftClient.getInstance().textRenderer, "Lv. 3", i, j, 0xFF0000, true);
    }
}