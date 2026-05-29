package github.meloweh.wolfcompanion.client.screen.inventory;

import github.meloweh.wolfcompanion.WolfCompanion;
import net.minecraft.resources.Identifier;

final class WolfInventoryTextures {
    static final Identifier BACKGROUND = Identifier.withDefaultNamespace("textures/gui/container/horse.png");
    static final Identifier CHEST_SLOTS = Identifier.withDefaultNamespace("container/horse/chest_slots");
    static final Identifier SLOT = Identifier.withDefaultNamespace("container/slot");

    static final Identifier RELEASE_WOLF = WolfCompanion.id("textures/gui/container/release_wolf_available.png");
    static final Identifier RELEASE_WOLF_HOVERED = WolfCompanion.id("textures/gui/container/release_wolf_highlighted.png");
    static final Identifier OFFENSIVE_MODE = WolfCompanion.id("textures/gui/container/offensive_mode_available.png");
    static final Identifier OFFENSIVE_MODE_HOVERED = WolfCompanion.id("textures/gui/container/offensive_mode_highlighted.png");
    static final Identifier VANILLA_MODE = WolfCompanion.id("textures/gui/container/vanilla_mode_available.png");
    static final Identifier VANILLA_MODE_HOVERED = WolfCompanion.id("textures/gui/container/vanilla_mode_highlighted.png");
    static final Identifier LOCKED = WolfCompanion.id("textures/gui/container/locked.png");
    static final Identifier LOCKED_HOVERED = WolfCompanion.id("textures/gui/container/locked_highlighted.png");
    static final Identifier UNLOCKED = WolfCompanion.id("textures/gui/container/unlocked.png");
    static final Identifier UNLOCKED_HOVERED = WolfCompanion.id("textures/gui/container/unlocked_highlighted.png");
    static final Identifier CHEST = WolfCompanion.id("textures/gui/container/drop_chest_available.png");
    static final Identifier CHEST_HOVERED = WolfCompanion.id("textures/gui/container/drop_chest_highlighted.png");

    static final Identifier WOLF_ARMOR_SLOT = WolfCompanion.id("textures/gui/container/icon_wolf_armor.png");
    static final Identifier XP_BACKGROUND = WolfCompanion.id("textures/gui/container/wolf_experience_bar_background_v2.png");
    static final Identifier XP_CURRENT = WolfCompanion.id("textures/gui/container/wolf_experience_bar_current_v2.png");
    static final Identifier HEART_CONTAINER = WolfCompanion.id("textures/gui/container/container.png");
    static final Identifier HEART = WolfCompanion.id("textures/gui/container/heart.png");

    private WolfInventoryTextures() {
    }
}
