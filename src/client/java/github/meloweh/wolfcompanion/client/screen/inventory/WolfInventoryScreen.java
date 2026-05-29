package github.meloweh.wolfcompanion.client.screen.inventory;

import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.accessor.WolfXpProvider;
import github.meloweh.wolfcompanion.network.AggressionWolfC2SPayload;
import github.meloweh.wolfcompanion.network.DropWolfChestC2SPayload;
import github.meloweh.wolfcompanion.network.LockWolfC2SPayload;
import github.meloweh.wolfcompanion.network.ReleaseWolfC2SPayload;
import github.meloweh.wolfcompanion.menu.WolfInventoryScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class WolfInventoryScreen extends AbstractContainerScreen<WolfInventoryScreenHandler> {
    private static final int WOLF_SLOT_COLUMNS = 5;
    private static final int CHEST_BUTTON_X = 7;
    private static final int CHEST_BUTTON_Y = 35;
    private static final int AGGRESSION_BUTTON_Y = CHEST_BUTTON_Y + 18;
    private static final int LOCK_BUTTON_X = 26;
    private static final int LOCK_BUTTON_Y = 18;
    private static final int LOCK_BUTTON_WIDTH = 10;
    private static final int LOCK_BUTTON_HEIGHT = 15;
    private static final int STANDARD_BUTTON_SIZE = 18;
    private static final int RELEASE_BUTTON_WIDTH = 12;
    private static final int RELEASE_BUTTON_HEIGHT = 10;

    private final Wolf wolf;
    private final WolfXpProvider wolfXp;
    private float mouseX;
    private float mouseY;
    private final Player player;

    public WolfInventoryScreen(WolfInventoryScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 184;
        this.inventoryLabelY = this.imageHeight - 111;
        this.wolf = handler.getWolf();
        this.wolfXp = (WolfXpProvider) this.wolf;
        this.player = inventory.player;
    }

    private WolfEntityProvider wolfState() {
        return (WolfEntityProvider) this.wolf;
    }

    private int left() {
        return (this.width - this.imageWidth) / 2;
    }

    private int top() {
        return (this.height - this.imageHeight) / 2;
    }

    private boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private boolean isMouseOver(int x, int y, int width, int height) {
        return isMouseOver(this.mouseX, this.mouseY, x, y, width, height);
    }

    private boolean clickedDropChest(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY, left() + CHEST_BUTTON_X, top() + CHEST_BUTTON_Y, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE);
    }

    private boolean clickedAggressionWolf(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY, left() + CHEST_BUTTON_X, top() + AGGRESSION_BUTTON_Y, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE);
    }

    private boolean clickedLockWolf(double mouseX, double mouseY) {
        int lockedHeightAdjustment = wolfState().isLock__() ? 3 : 0;
        return isMouseOver(mouseX, mouseY, left() + LOCK_BUTTON_X, top() + LOCK_BUTTON_Y, LOCK_BUTTON_WIDTH, LOCK_BUTTON_HEIGHT - lockedHeightAdjustment);
    }

    private boolean clickedReleaseWolf(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY, releaseButtonX(), releaseButtonY(), RELEASE_BUTTON_WIDTH, RELEASE_BUTTON_HEIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (clickedDropChest(mouseX, mouseY) && wolfState().hasChestEquipped()) {
            ClientPlayNetworking.send(new DropWolfChestC2SPayload(wolf.getUUID()));
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3f, 0.8f);
        }
        if (clickedReleaseWolf(mouseX, mouseY) && this.wolf.isTame()) {
            ClientPlayNetworking.send(new ReleaseWolfC2SPayload(wolf.getUUID()));
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3f, 0.8f);
        }
        if (clickedAggressionWolf(mouseX, mouseY) && this.wolf.isTame()) {
            ClientPlayNetworking.send(new AggressionWolfC2SPayload(wolf.getUUID()));
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3f, 1f);
        }
        if (clickedLockWolf(mouseX, mouseY) && this.wolf.isTame()) {
            ClientPlayNetworking.send(new LockWolfC2SPayload(wolf.getUUID()));
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3f, 1f);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int releaseButtonX() {
        return this.width / 2 + this.imageWidth / 2 - RELEASE_BUTTON_WIDTH - 8;
    }

    private int releaseButtonY() {
        return top() + 5;
    }

    private void drawReleaseButton(GuiGraphics context, int mouseX, int mouseY) {
        if (!this.wolf.isTame()) return;

        int x = releaseButtonX();
        int y = releaseButtonY();
        if (isMouseOver(x, y, RELEASE_BUTTON_WIDTH, RELEASE_BUTTON_HEIGHT)) {
            context.blit(WolfInventoryTextures.RELEASE_WOLF_HOVERED, x, y, 0, 0, RELEASE_BUTTON_WIDTH, RELEASE_BUTTON_HEIGHT, RELEASE_BUTTON_WIDTH, RELEASE_BUTTON_HEIGHT);
        } else {
            context.blit(WolfInventoryTextures.RELEASE_WOLF, x, y, 0, 0, RELEASE_BUTTON_WIDTH, RELEASE_BUTTON_HEIGHT, RELEASE_BUTTON_WIDTH, RELEASE_BUTTON_HEIGHT);
        }
    }

    private void drawAggressionButton(GuiGraphics context, int mouseX, int mouseY) {
        if (!this.wolf.isTame()) return;

        int x = left() + CHEST_BUTTON_X;
        int y = top() + AGGRESSION_BUTTON_Y;

        final ResourceLocation hoveredTexture = wolfState().isAggressive__() ? WolfInventoryTextures.OFFENSIVE_MODE_HOVERED : WolfInventoryTextures.VANILLA_MODE_HOVERED;
        final ResourceLocation texture = wolfState().isAggressive__() ? WolfInventoryTextures.OFFENSIVE_MODE : WolfInventoryTextures.VANILLA_MODE;

        if (isMouseOver(x, y, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE)) {
            context.blit(hoveredTexture, x, y, 0, 0, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE);
        } else {
            context.blit(texture, x, y, 0, 0, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE);
        }
    }

    private void drawLockButton(GuiGraphics context, int mouseX, int mouseY) {
        if (!this.wolf.isTame()) return;

        int x = left() + LOCK_BUTTON_X;
        int y = top() + LOCK_BUTTON_Y;
        int height = LOCK_BUTTON_HEIGHT - (wolfState().isLock__() ? 3 : 0);

        final ResourceLocation hoveredTexture = wolfState().isLock__() ? WolfInventoryTextures.LOCKED_HOVERED : WolfInventoryTextures.UNLOCKED_HOVERED;
        final ResourceLocation texture = wolfState().isLock__() ? WolfInventoryTextures.LOCKED : WolfInventoryTextures.UNLOCKED;

        if (isMouseOver(x, y, LOCK_BUTTON_WIDTH, height)) {
            context.blit(hoveredTexture, x, y, 0, 0, LOCK_BUTTON_WIDTH, LOCK_BUTTON_HEIGHT, LOCK_BUTTON_WIDTH, LOCK_BUTTON_HEIGHT);
        } else {
            context.blit(texture, x, y, 0, 0, LOCK_BUTTON_WIDTH, LOCK_BUTTON_HEIGHT, LOCK_BUTTON_WIDTH, LOCK_BUTTON_HEIGHT);
        }
    }

    private void drawChestButton(GuiGraphics context, int mouseX, int mouseY) {
        if (!this.wolf.isTame()) return;
        if (!wolfState().hasChestEquipped()) return;

        int x = left() + CHEST_BUTTON_X;
        int y = top() + CHEST_BUTTON_Y;

        if (isMouseOver(x, y, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE)) {
            context.blit(WolfInventoryTextures.CHEST_HOVERED, x, y, 0, 0, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE);
        } else {
            context.blit(WolfInventoryTextures.CHEST, x, y, 0, 0, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE, STANDARD_BUTTON_SIZE);
        }
    }

    @Override
    protected void renderBg(GuiGraphics context, float deltaTicks, int mouseX, int mouseY) {
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
        int i = left();
        int j = top();
        context.blit(WolfInventoryTextures.BACKGROUND, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        if (wolfState().hasChestEquipped()) {
            context.blitSprite(WolfInventoryTextures.CHEST_SLOTS, 90, 54, 0, 0, i + 79, j + 17, WOLF_SLOT_COLUMNS * 18, 54);
        }

        if (this.wolf.isWearingBodyArmor()) {
            context.blitSprite(WolfInventoryTextures.SLOT, i + 7, j + 35 - 18, 18, 18);
        } else {
            context.blit(WolfInventoryTextures.WOLF_ARMOR_SLOT, i + 7, j + 35 - 18, 0, 0, 18, 18, 18, 18);
        }

        drawChestButton(context, mouseX, mouseY);
        drawAggressionButton(context, mouseX, mouseY);
        drawReleaseButton(context, mouseX, mouseY);
        drawLockButton(context, mouseX, mouseY);

        InventoryScreen.renderEntityInInventoryFollowsMouse(context, i + 26, j + 18, i + 78, j + 70, 33, 0.25F, this.mouseX, this.mouseY, this.wolf);
    }

    private Component tooltipAt(int mouseX, int mouseY) {
        if (!this.wolf.isTame()) {
            return null;
        }
        if (clickedReleaseWolf(mouseX, mouseY)) {
            return Component.literal("Release Wolf");
        }
        if (clickedLockWolf(mouseX, mouseY)) {
            return Component.literal("Resist Whistle");
        }
        if (clickedAggressionWolf(mouseX, mouseY)) {
            return Component.literal("Toggle Aggression");
        }
        if (wolfState().hasChestEquipped() && clickedDropChest(mouseX, mouseY)) {
            return Component.literal("Drop Bag and Items");
        }
        return null;
    }

    private void drawHearts(GuiGraphics context) {
        final int WIDTH = 8, HEIGHT = 9;

        int x = (this.width) / 2;
        int y = (this.height) / 2 - 10 - HEIGHT;

        final int maxHealthPoints = (int) wolf.getMaxHealth() / 4;
        final int healthPixels = (int) wolf.getHealth() * 2 + 1;

        context.blit(WolfInventoryTextures.HEART_CONTAINER, x, y, 0, 0, WIDTH * maxHealthPoints + 1, HEIGHT, WIDTH, HEIGHT);
        context.blit(WolfInventoryTextures.HEART, x, y, 0, 0, healthPixels, HEIGHT, WIDTH, HEIGHT);
    }

    private void drawLevelInfo(GuiGraphics context) {
        final int WIDTH = 30;
        final int HEIGHT = 5;

        int x = this.width / 2 + this.imageWidth / 2 - WIDTH - 7 - 18;
        int y = (this.height - this.imageHeight) / 2 + HEIGHT + 3 - 1;

        final int xp = wolfXp.getXp();
        final int level = wolfXp.getLevel();
        final int maxXp = wolfXp.getNextLevelXpRequirement(level + 1);
        final int prevMaxXp = wolfXp.getNextLevelXpRequirement(level);
        final int deltaMaxXp = maxXp - prevMaxXp;
        final int deltaXp = xp - prevMaxXp;

        String xpText = (level < 1) ? "" : level + "";
        int xpTextWidth = font.width(xpText);

        context.blit(WolfInventoryTextures.XP_BACKGROUND, x, y, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        context.blit(WolfInventoryTextures.XP_CURRENT, x, y, 0, 0, WIDTH * deltaXp / deltaMaxXp, HEIGHT, WIDTH, HEIGHT);
        context.drawString(font, xpText, x + WIDTH / 2 - xpTextWidth / 2, y - 4 + 2, 0xFF7EFC20, true);

    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
        super.render(context, mouseX, mouseY, delta);
        this.drawLevelInfo(context);
        this.drawHearts(context);

        Component tooltip = tooltipAt(mouseX, mouseY);
        if (tooltip != null) {
            context.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }
}
