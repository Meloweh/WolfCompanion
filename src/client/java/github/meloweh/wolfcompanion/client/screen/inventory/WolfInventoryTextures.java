package github.meloweh.wolfcompanion.client.screen.inventory;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.resources.ResourceLocation;

final class WolfInventoryTextures {
    static final ResourceLocation BACKGROUND = new ResourceLocation("minecraft", "textures/gui/container/horse.png");

    static final ResourceLocation RELEASE_WOLF = WolfCompanion.id("textures/gui/container/release_wolf_available.png");
    static final ResourceLocation RELEASE_WOLF_HOVERED = WolfCompanion.id("textures/gui/container/release_wolf_highlighted.png");
    static final ResourceLocation OFFENSIVE_MODE = WolfCompanion.id("textures/gui/container/offensive_mode_available.png");
    static final ResourceLocation OFFENSIVE_MODE_HOVERED = WolfCompanion.id("textures/gui/container/offensive_mode_highlighted.png");
    static final ResourceLocation VANILLA_MODE = WolfCompanion.id("textures/gui/container/vanilla_mode_available.png");
    static final ResourceLocation VANILLA_MODE_HOVERED = WolfCompanion.id("textures/gui/container/vanilla_mode_highlighted.png");
    static final ResourceLocation LOCKED = WolfCompanion.id("textures/gui/container/locked.png");
    static final ResourceLocation LOCKED_HOVERED = WolfCompanion.id("textures/gui/container/locked_highlighted.png");
    static final ResourceLocation UNLOCKED = WolfCompanion.id("textures/gui/container/unlocked.png");
    static final ResourceLocation UNLOCKED_HOVERED = WolfCompanion.id("textures/gui/container/unlocked_highlighted.png");
    static final ResourceLocation CHEST = WolfCompanion.id("textures/gui/container/drop_chest_available.png");
    static final ResourceLocation CHEST_HOVERED = WolfCompanion.id("textures/gui/container/drop_chest_highlighted.png");

    static final ResourceLocation WOLF_ARMOR_SLOT = WolfCompanion.id("textures/gui/container/icon_wolf_armor.png");
    static final ResourceLocation XP_BACKGROUND = WolfCompanion.id("textures/gui/container/wolf_experience_bar_background_v2.png");
    static final ResourceLocation XP_CURRENT = WolfCompanion.id("textures/gui/container/wolf_experience_bar_current_v2.png");
    static final ResourceLocation HEART_CONTAINER = WolfCompanion.id("textures/gui/container/container.png");
    static final ResourceLocation HEART = WolfCompanion.id("textures/gui/container/heart.png");

    private WolfInventoryTextures() {
    }
}
