package github.meloweh.wolfcompanion.screen;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.accessor.WolfEntityProvider;
import github.meloweh.wolfcompanion.accessor.WolfXpProvider;
import github.meloweh.wolfcompanion.network.AggressionWolfC2SPayload;
import github.meloweh.wolfcompanion.network.DropWolfChestC2SPayload;
import github.meloweh.wolfcompanion.network.LockWolfC2SPayload;
import github.meloweh.wolfcompanion.network.ReleaseWolfC2SPayload;
import github.meloweh.wolfcompanion.screenhandler.WolfInventoryScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class WolfInventoryScreen extends AbstractContainerScreen<WolfInventoryScreenHandler> {
    private static final Identifier CHEST_SLOTS_TEXTURE = Identifier.withDefaultNamespace("container/horse/chest_slots");
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/horse.png");
    private static final Identifier SLOT = Identifier.withDefaultNamespace("container/slot");

    private final Wolf wolf;
    private final WolfXpProvider wolfXp;
    private final int slotColumnCount;
    private float mouseX;
    private float mouseY;
    final private SimpleContainer wolfInventory;
    final private Player player;
    final private WolfInventoryScreenHandler handler;

    private static final Identifier BUTTON_RELEASE_WOLF_AVAILABLE = WolfCompanion.id("textures/gui/container/release_wolf_available.png");
    private static final Identifier BUTTON_RELEASE_WOLF_HIGHLIGHTED = WolfCompanion.id("textures/gui/container/release_wolf_highlighted.png");

    private static final Identifier BUTTON_OFFENSIVE_MODE_AVAILABLE = WolfCompanion.id("textures/gui/container/offensive_mode_available.png");
    private static final Identifier BUTTON_OFFENSIVE_MODE_HIGHLIGHTED = WolfCompanion.id("textures/gui/container/offensive_mode_highlighted.png");

    private static final Identifier BUTTON_VANILLA_MODE_AVAILABLE = WolfCompanion.id("textures/gui/container/vanilla_mode_available.png");
    private static final Identifier BUTTON_VANILLA_MODE_HIGHLIGHTED = WolfCompanion.id("textures/gui/container/vanilla_mode_highlighted.png");

    private static final Identifier BUTTON_LOCKED_AVAILABLE = WolfCompanion.id("textures/gui/container/locked.png");
    private static final Identifier BUTTON_LOCKED_HIGHLIGHTED = WolfCompanion.id("textures/gui/container/locked_highlighted.png");

    private static final Identifier BUTTON_UNLOCKED_AVAILABLE = WolfCompanion.id("textures/gui/container/unlocked.png");
    private static final Identifier BUTTON_UNLOCKED_HIGHLIGHTED = WolfCompanion.id("textures/gui/container/unlocked_highlighted.png");

    private static final Identifier BUTTON_CHEST_AVAILABLE = WolfCompanion.id("textures/gui/container/drop_chest_available.png");
    private static final Identifier BUTTON_CHEST_HIGHLIGHTED = WolfCompanion.id("textures/gui/container/drop_chest_highlighted.png");

    private static final Identifier WOLF_ARMOR_SLOT = WolfCompanion.id("textures/gui/container/icon_wolf_armor.png");
    private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = WolfCompanion.id("textures/gui/container/wolf_experience_bar_background_v2.png");
    private static final Identifier EXPERIENCE_BAR_CURRENT_TEXTURE = WolfCompanion.id("textures/gui/container/wolf_experience_bar_current_v2.png");
    private static final Identifier HEART_CONTAINER = WolfCompanion.id("textures/gui/container/container.png");
    private static final Identifier HEART = WolfCompanion.id("textures/gui/container/heart.png");

    public WolfInventoryScreen(WolfInventoryScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 184;
        this.inventoryLabelY = this.imageHeight - 111;
        this.slotColumnCount = 5;
        this.wolf = handler.getWolf();
        this.wolfXp = (WolfXpProvider) this.wolf;
        this.wolfInventory = handler.getWolfInventory();
        this.player = inventory.player;
        this.handler = handler;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    private boolean clickedDropChest(double mouseX, double mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        return mouseX >= i + 7 &&
                mouseX < i + 7 + 18 &&
                mouseY >= j + 35 &&
                mouseY < j + 35 + 18;
    }

    private boolean clickedAggressionWolf(double mouseX, double mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        return mouseX >= i + 7 &&
                mouseX < i + 7 + 18 &&
                mouseY >= j + 35        + 18 &&
                mouseY < j + 35 + 18    + 18;
    }

    private boolean clickedLockWolf(double mouseX, double mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        return mouseX >= i + 7 + 19 &&
                mouseX < i + 7 + 19 + 10 &&
                mouseY >= j + 18 &&
                mouseY < j + 18 + 15 - (((WolfEntityProvider)this.wolf).isLock__() ? 3 : 0);
    }

    private boolean clickedReleaseWolf(double mouseX, double mouseY) {
        final int WIDTH = 12;
        final int HEIGHT = 10;

        int x = this.width / 2 + this.imageWidth / 2 - WIDTH - 7 - 1;
        int y = (this.height - this.imageHeight) / 2 + HEIGHT + 3 - 1;

        return mouseX >= x &&
                mouseX < x + 12 &&
                mouseY >= y - 7 &&
                mouseY < y + 3;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (clickedDropChest(click.x(), click.y()) && ((WolfEntityProvider)this.wolf).hasChestEquipped()) {
            ClientPlayNetworking.send(new DropWolfChestC2SPayload(wolf.getUUID()));
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3f, 0.8f);
        }
        if (clickedReleaseWolf(click.x(), click.y()) && this.wolf.isTame()) {
            ClientPlayNetworking.send(new ReleaseWolfC2SPayload(wolf.getUUID()));
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3f, 0.8f);
        }
        if (clickedAggressionWolf(click.x(), click.y()) && this.wolf.isTame()) {
            ClientPlayNetworking.send(new AggressionWolfC2SPayload(wolf.getUUID()));
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3f, 1f);
        }
        if (clickedLockWolf(click.x(), click.y()) && this.wolf.isTame()) {
            ClientPlayNetworking.send(new LockWolfC2SPayload(wolf.getUUID()));
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3f, 1f);
        }

        return super.mouseClicked(click, doubled);
    }

    private void drawReleaseButton(GuiGraphics context, float deltaTicks, int mouseX, int mouseY) {
        if (!this.wolf.isTame()) return;

        final int WIDTH = 12;
        final int HEIGHT = 10;

        int x = this.width / 2 + this.imageWidth / 2 - WIDTH - 7 - 1;
        int y = (this.height - this.imageHeight) / 2 + HEIGHT + 3 - 1;


        if (this.mouseX >= x &&
                this.mouseX < x + 12 &&
                this.mouseY >= y - 7 &&
                this.mouseY < y + 3) {
            context.blit(RenderPipelines.GUI_TEXTURED, BUTTON_RELEASE_WOLF_HIGHLIGHTED, x, y - 7, 0, 0, 12, 10, 12, 10);
            context.setTooltipForNextFrame(this.font, Component.nullToEmpty("Release Wolf"), mouseX, mouseY);
        } else {
            context.blit(RenderPipelines.GUI_TEXTURED, BUTTON_RELEASE_WOLF_AVAILABLE, x, y - 7, 0, 0, 12, 10, 12, 10);
        }
    }

    private void drawAggressionButton(GuiGraphics context, float deltaTicks, int mouseX, int mouseY) {
        if (!this.wolf.isTame()) return;

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        final Identifier CURRENT_HIGHLIGHTED = ((WolfEntityProvider)this.wolf).isAggressive__() ? BUTTON_OFFENSIVE_MODE_HIGHLIGHTED : BUTTON_VANILLA_MODE_HIGHLIGHTED;
        final Identifier CURRENT_AVAILABLE = ((WolfEntityProvider)this.wolf).isAggressive__() ? BUTTON_OFFENSIVE_MODE_AVAILABLE : BUTTON_VANILLA_MODE_AVAILABLE;

        if (this.mouseX >= i + 7 &&
                this.mouseX < i + 7 + 18 &&
                this.mouseY >= j + 35 + 18 &&
                this.mouseY < j + 35 + 36) {
            context.blit(RenderPipelines.GUI_TEXTURED, CURRENT_HIGHLIGHTED, i + 7, j + 35 + 18, 0, 0, 18, 18, 18, 18);
            context.setTooltipForNextFrame(this.font, Component.nullToEmpty("Toggle Aggression"), mouseX, mouseY);
        } else {
            context.blit(RenderPipelines.GUI_TEXTURED, CURRENT_AVAILABLE, i + 7, j + 35 + 18, 0, 0, 18, 18, 18, 18);
        }
    }

    private void drawLockButton(GuiGraphics context, float deltaTicks, int mouseX, int mouseY) {
        if (!this.wolf.isTame()) return;

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        final Identifier CURRENT_HIGHLIGHTED = ((WolfEntityProvider)this.wolf).isLock__() ? BUTTON_LOCKED_HIGHLIGHTED : BUTTON_UNLOCKED_HIGHLIGHTED;
        final Identifier CURRENT_AVAILABLE = ((WolfEntityProvider)this.wolf).isLock__() ? BUTTON_LOCKED_AVAILABLE : BUTTON_UNLOCKED_AVAILABLE;

        if (this.mouseX >= i + 7 + 19 &&
                this.mouseX < i + 7 + 19 + 10 &&
                this.mouseY >= j + 18 &&
                this.mouseY < j + 18 + 15 - (((WolfEntityProvider)this.wolf).isLock__() ? 3 : 0)) {
            context.blit(RenderPipelines.GUI_TEXTURED, CURRENT_HIGHLIGHTED, i + 7 + 19, j + 18, 0, 0, 10, 15, 10, 15);
            context.setTooltipForNextFrame(this.font, Component.nullToEmpty("Resist Whistle"), mouseX, mouseY);
        } else {
            context.blit(RenderPipelines.GUI_TEXTURED, CURRENT_AVAILABLE, i + 7 + 19, j + 18, 0, 0, 10, 15, 10, 15);
        }
    }

    private void drawChestButton(GuiGraphics context, float deltaTicks, int mouseX, int mouseY) {
        if (!this.wolf.isTame()) return;

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        if (((WolfEntityProvider)this.wolf).hasChestEquipped()) {
            if (this.mouseX >= i + 7 &&
                    this.mouseX < i + 7 + 18 &&
                    this.mouseY >= j + 35 &&
                    this.mouseY < j + 35 + 18) {
                context.blit(RenderPipelines.GUI_TEXTURED, BUTTON_CHEST_HIGHLIGHTED, i + 7, j + 35, 0, 0, 18, 18, 18, 18);
                context.setTooltipForNextFrame(this.font, Component.nullToEmpty("Drop Bag and Items"), mouseX, mouseY);
            } else {
                context.blit(RenderPipelines.GUI_TEXTURED, BUTTON_CHEST_AVAILABLE, i + 7, j + 35, 0, 0, 18, 18, 18, 18);
            }
        } else {
            //context.drawTexture(BUTTON_CHEST_DISABLED, i + 7, j + 35, 0, 0, 18, 18, 18, 18);
        }
    }

    @Override
    protected void renderBg(GuiGraphics context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        if (((WolfEntityProvider)wolf).hasChestEquipped()) {
            if (this.slotColumnCount > 0) {
                context.blitSprite(RenderPipelines.GUI_TEXTURED, CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, i + 79, j + 17, this.slotColumnCount * 18, 54);
            }
        }

        if (this.wolf.isWearingBodyArmor()) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT, i + 7, j + 35 - 18, 18, 18);
        } else {
            context.blit(RenderPipelines.GUI_TEXTURED, WOLF_ARMOR_SLOT, i + 7, j + 35 - 18, 0, 0, 18, 18, 18, 18);
        }

        drawChestButton(context, deltaTicks, mouseX, mouseY);

        //context.drawTexture(RenderPipelines.GUI_TEXTURED, BUTTON_RELEASE_WOLF_AVAILABLE, i + 7, j + 35 + 18, 0, 0, 18, 18, 18, 18);

        drawAggressionButton(context, deltaTicks, mouseX, mouseY);
        drawReleaseButton(context, deltaTicks, mouseX, mouseY);

        drawLockButton(context, deltaTicks, mouseX, mouseY);

        this.renderTooltip(context, mouseX, mouseY);
        InventoryScreen.renderEntityInInventoryFollowsMouse(context, i + 26, j + 18, i + 78, j + 70, 33, 0.25F, this.mouseX, this.mouseY, this.wolf);
    }

    private void drawHearts(GuiGraphics context) {
        final int WIDTH = 8, HEIGHT = 9;

        int x = (this.width) / 2;
        int y = (this.height) / 2 - 10 - HEIGHT;

        final int maxHealthPoints = (int) wolf.getMaxHealth() / 4;
        final int healthPixels = (int) wolf.getHealth() * 2 + 1;

        context.blit(RenderPipelines.GUI_TEXTURED, HEART_CONTAINER, x, y, 0, 0, WIDTH * maxHealthPoints + 1, HEIGHT, WIDTH, HEIGHT);
        context.blit(RenderPipelines.GUI_TEXTURED, HEART, x, y, 0, 0, healthPixels, HEIGHT, WIDTH, HEIGHT);
    }

    private void drawLevelInfo(GuiGraphics context) {
        final int WIDTH = 30;
        final int HEIGHT = 5;

        int x = this.width / 2 + this.imageWidth / 2 - WIDTH - 7 - 18;
        //int x = this.width / 2 - WIDTH / 2;
        int y = (this.height - this.imageHeight) / 2 + HEIGHT + 3 - 1;

        final int xp = wolfXp.getXp();
        final int level = wolfXp.getLevel();
        final int maxXp = wolfXp.getNextLevelXpRequirement(level + 1);
        final int prevMaxXp = wolfXp.getNextLevelXpRequirement(level);
        final int deltaMaxXp = maxXp - prevMaxXp;
        final int deltaXp = xp - prevMaxXp;

        //System.out.println("xp: " + xp + " level: " + level + " maxXp: " + maxXp + " prevMaxXp: " + prevMaxXp + " deltaMaxXp: " + deltaMaxXp + " deltaXp: " + deltaXp);

        String xpText = (level < 1) ? "" : level + "";
        int xpTextWidth = font.width(xpText);

        context.blit(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_BACKGROUND_TEXTURE, x, y, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        //final int currentXpBar = WIDTH * (deltaXp / deltaMaxXp);
        context.blit(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_CURRENT_TEXTURE, x, y, 0, 0, WIDTH * deltaXp / deltaMaxXp, HEIGHT, WIDTH, HEIGHT);
        context.drawString(font, xpText, x + WIDTH / 2 - xpTextWidth / 2, y - 4 + 2, 0xFF7EFC20, true);

    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
        this.drawLevelInfo(context);
        this.drawHearts(context);
    }
}