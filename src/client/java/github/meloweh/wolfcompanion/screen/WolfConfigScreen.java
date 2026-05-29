package github.meloweh.wolfcompanion.screen;

import github.meloweh.wolfcompanion.util.ConfigManager;
import github.meloweh.wolfcompanion.util.WolfConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class WolfConfigScreen extends Screen {
    private static final int FIELD_WIDTH = 112;
    private static final int ROW_HEIGHT = 28;
    private static final int TEXT_COLOR = 0xFFE0E0E0;
    private static final int MUTED_TEXT_COLOR = 0xFFAAAAAA;
    private static final int ERROR_TEXT_COLOR = 0xFFFF5555;

    private final Screen parent;
    private final WolfConfig draft;
    private final List<Label> labels = new ArrayList<>();
    private final List<InputBinding> inputs = new ArrayList<>();
    private int pageIndex;
    private String statusMessage;
    private boolean statusIsError;

    public WolfConfigScreen(Screen parent) {
        super(Component.translatable("screen.wolfcompanion.config"));
        this.parent = parent;
        this.draft = ConfigManager.config.copy();
        this.draft.normalize();
    }

    @Override
    protected void init() {
        labels.clear();
        inputs.clear();

        int contentWidth = Math.min(390, this.width - 40);
        int left = this.width / 2 - contentWidth / 2;
        int y = 58;

        switch (page()) {
            case SURVIVAL -> {
                y = addBoolean(left, y, contentWidth, "Respawn wolves", draft.canRespawn, value -> draft.canRespawn = value);
                y = addBoolean(left, y, contentWidth, "Keep bag on wolf death", draft.keepWolfBag, value -> draft.keepWolfBag = value);
                y = addBoolean(left, y, contentWidth, "Keep inventory on wolf death", draft.keepWolfInventory, value -> draft.keepWolfInventory = value);
                y = addBoolean(left, y, contentWidth, "Keep armor on wolf death", draft.keepWolfArmor, value -> draft.keepWolfArmor = value);
                addBoolean(left, y, contentWidth, "Keep XP on wolf death", draft.keepXp, value -> draft.keepXp = value);
            }
            case RECOVERY -> {
                y = addBoolean(left, y, contentWidth, "Shake off poison", draft.canShakeOffPoison, value -> draft.canShakeOffPoison = value);
                y = addBoolean(left, y, contentWidth, "Shake off fire", draft.canShakeOffFire, value -> draft.canShakeOffFire = value);
                y = addBoolean(left, y, contentWidth, "Passive regeneration", draft.allowPassiveRegeneration, value -> draft.allowPassiveRegeneration = value);
                addInteger(left, y, contentWidth, "Passive regen rate", draft.passiveRegenerationRate, 1, value -> draft.passiveRegenerationRate = value);
            }
            case TELEPORT -> {
                y = addBoolean(left, y, contentWidth, "Allow teleport", draft.allowTeleport, value -> draft.allowTeleport = value);
                y = addBoolean(left, y, contentWidth, "Teleport sitting wolves", draft.canTeleportSitting, value -> draft.canTeleportSitting = value);
                addDouble(left, y, contentWidth, "Teleport distance", draft.teleportAtDistance, 0.0, value -> draft.teleportAtDistance = value);
            }
            case FOOD -> {
                y = addBoolean(left, y, contentWidth, "Pick up food", draft.canPickupFood, value -> draft.canPickupFood = value);
                y = addInteger(left, y, contentWidth, "Max pickup food", draft.maxPickupFood, 0, value -> draft.maxPickupFood = value);
                y = addBoolean(left, y, contentWidth, "Pick all rotten flesh", draft.pickAllRottenFlesh, value -> draft.pickAllRottenFlesh = value);
                y = addBoolean(left, y, contentWidth, "Reserve player food", draft.shouldCarePlayerFood, value -> draft.shouldCarePlayerFood = value);
                addInteger(left, y, contentWidth, "Required player food", draft.requiredPlayerFood, 0, value -> draft.requiredPlayerFood = value);
            }
            case COMBAT -> {
                y = addDouble(left, y, contentWidth, "Attack acceleration", draft.attackAcceleration, 0.0, value -> draft.attackAcceleration = value);
                y = addDouble(left, y, contentWidth, "Max attack speed", draft.maxSpeed, 0.0, value -> draft.maxSpeed = value);
                addStringList(left, y, contentWidth, "Do not attack mobs/tags", draft.doNotAttackMobs, value -> draft.doNotAttackMobs = value);
            }
        }

        addNavigationButtons(contentWidth);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        this.extractMenuBackground(context);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 17, 0xFFFFFFFF);
        context.centeredText(this.font, Component.literal(page().title + " (" + (pageIndex + 1) + "/" + Page.values().length + ")"), this.width / 2, 34, MUTED_TEXT_COLOR);

        for (Label label : labels) {
            context.text(this.font, label.text(), label.x(), label.y(), label.color());
        }

        if (statusMessage != null) {
            int color = statusIsError ? ERROR_TEXT_COLOR : 0xFF55FF55;
            context.centeredText(this.font, Component.literal(statusMessage), this.width / 2, this.height - 50, color);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    private Page page() {
        return Page.values()[pageIndex];
    }

    private int addBoolean(int left, int y, int width, String label, boolean selected, Consumer<Boolean> setter) {
        Checkbox checkbox = Checkbox.builder(Component.literal(label), this.font)
                .pos(left, y)
                .selected(selected)
                .maxWidth(width)
                .onValueChange((box, value) -> {
                    setter.accept(value);
                    clearStatus();
                })
                .build();
        addRenderableWidget(checkbox);
        return y + ROW_HEIGHT;
    }

    private int addInteger(int left, int y, int width, String label, int value, int minimum, Consumer<Integer> setter) {
        addField(left, y, width, label, Integer.toString(value), raw -> {
            try {
                int parsed = Integer.parseInt(raw.trim());
                if (parsed < minimum) {
                    return false;
                }
                setter.accept(parsed);
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        });
        return y + ROW_HEIGHT;
    }

    private int addDouble(int left, int y, int width, String label, double value, double minimum, Consumer<Double> setter) {
        addField(left, y, width, label, Double.toString(value), raw -> {
            try {
                double parsed = Double.parseDouble(raw.trim());
                if (!Double.isFinite(parsed) || parsed < minimum) {
                    return false;
                }
                setter.accept(parsed);
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        });
        return y + ROW_HEIGHT;
    }

    private int addStringList(int left, int y, int width, String label, List<String> values, Consumer<List<String>> setter) {
        labels.add(new Label(Component.literal(label), left, y, TEXT_COLOR));

        EditBox field = new EditBox(this.font, left, y + 14, width, 20, Component.literal(label));
        field.setValue(String.join(", ", values));
        field.setMaxLength(1024);
        field.setResponder(raw -> clearStatus());
        addRenderableWidget(field);
        inputs.add(new InputBinding(label, field, raw -> {
            List<String> parsed = Arrays.stream(raw.split(","))
                    .map(String::trim)
                    .filter(entry -> !entry.isEmpty())
                    .toList();
            setter.accept(new ArrayList<>(parsed));
            return true;
        }));
        return y + ROW_HEIGHT + 18;
    }

    private void addField(int left, int y, int width, String label, String value, InputApplier applier) {
        int fieldX = left + width - FIELD_WIDTH;
        labels.add(new Label(Component.literal(label), left, y + 6, TEXT_COLOR));

        EditBox field = new EditBox(this.font, fieldX, y, FIELD_WIDTH, 20, Component.literal(label));
        field.setValue(value);
        field.setMaxLength(1024);
        field.setResponder(raw -> clearStatus());
        addRenderableWidget(field);
        inputs.add(new InputBinding(label, field, applier));
    }

    private void addNavigationButtons(int contentWidth) {
        int buttonY = this.height - 28;
        int left = this.width / 2 - contentWidth / 2;
        int buttonWidth = Math.min(82, (contentWidth - 24) / 4);

        Button previous = Button.builder(Component.literal("Previous"), button -> changePage(-1))
                .bounds(left, buttonY, buttonWidth, 20)
                .build();
        previous.active = pageIndex > 0;
        addRenderableWidget(previous);

        Button next = Button.builder(Component.literal("Next"), button -> changePage(1))
                .bounds(left + buttonWidth + 8, buttonY, buttonWidth, 20)
                .build();
        next.active = pageIndex < Page.values().length - 1;
        addRenderableWidget(next);

        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.minecraft.setScreen(parent))
                .bounds(left + contentWidth - buttonWidth * 2 - 8, buttonY, buttonWidth, 20)
                .build());

        addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveAndClose())
                .bounds(left + contentWidth - buttonWidth, buttonY, buttonWidth, 20)
                .build());
    }

    private void changePage(int delta) {
        if (!applyVisibleInputs()) {
            return;
        }
        pageIndex = Math.max(0, Math.min(Page.values().length - 1, pageIndex + delta));
        rebuildWidgets();
    }

    private void saveAndClose() {
        if (!applyVisibleInputs()) {
            return;
        }
        ConfigManager.applyConfig(draft);
        this.minecraft.setScreen(parent);
    }

    private boolean applyVisibleInputs() {
        for (InputBinding input : inputs) {
            if (!input.apply()) {
                statusMessage = "Invalid value: " + input.label();
                statusIsError = true;
                return false;
            }
        }
        clearStatus();
        return true;
    }

    private void clearStatus() {
        statusMessage = null;
        statusIsError = false;
    }

    private enum Page {
        SURVIVAL("Survival"),
        RECOVERY("Recovery"),
        TELEPORT("Teleport"),
        FOOD("Food"),
        COMBAT("Combat");

        private final String title;

        Page(String title) {
            this.title = title;
        }
    }

    private record Label(Component text, int x, int y, int color) {}

    private record InputBinding(String label, EditBox field, InputApplier applier) {
        private boolean apply() {
            return applier.apply(field.getValue());
        }
    }

    @FunctionalInterface
    private interface InputApplier {
        boolean apply(String value);
    }
}
