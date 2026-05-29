package github.meloweh.wolfcompanion.client.screen.config;

import github.meloweh.wolfcompanion.config.WolfCompanionConfig;
import github.meloweh.wolfcompanion.config.WolfConfig;
import net.minecraft.client.gui.GuiGraphics;
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
        this.draft = WolfCompanionConfig.editableCopy();
        this.draft.normalize();
    }

    @Override
    protected void init() {
        labels.clear();
        inputs.clear();

        int contentWidth = Math.min(390, this.width - 40);
        int left = this.width / 2 - contentWidth / 2;
        int y = 58;

        page().build(this, draft, left, y, contentWidth);

        addNavigationButtons(contentWidth);
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        this.renderMenuBackground(context);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 17, 0xFFFFFFFF);
        context.drawCenteredString(this.font, Component.literal(page().title() + " (" + (pageIndex + 1) + "/" + ConfigPage.values().length + ")"), this.width / 2, 34, MUTED_TEXT_COLOR);

        for (Label label : labels) {
            context.drawString(this.font, label.text(), label.x(), label.y(), label.color());
        }

        if (statusMessage != null) {
            int color = statusIsError ? ERROR_TEXT_COLOR : 0xFF55FF55;
            context.drawCenteredString(this.font, Component.literal(statusMessage), this.width / 2, this.height - 50, color);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    private ConfigPage page() {
        return ConfigPage.values()[pageIndex];
    }

    int addBoolean(int left, int y, int width, String label, boolean selected, Consumer<Boolean> setter) {
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

    int addInteger(int left, int y, int width, String label, int value, int minimum, Consumer<Integer> setter) {
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

    int addIntegerChoice(int left, int y, int width, String label, int value, Consumer<Integer> setter, int... allowedValues) {
        addField(left, y, width, label, Integer.toString(value), raw -> {
            try {
                int parsed = Integer.parseInt(raw.trim());
                for (int allowedValue : allowedValues) {
                    if (parsed == allowedValue) {
                        setter.accept(parsed);
                        return true;
                    }
                }
                return false;
            } catch (NumberFormatException ignored) {
                return false;
            }
        });
        return y + ROW_HEIGHT;
    }

    int addDouble(int left, int y, int width, String label, double value, double minimum, Consumer<Double> setter) {
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

    int addStringList(int left, int y, int width, String label, List<String> values, Consumer<List<String>> setter) {
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
        next.active = pageIndex < ConfigPage.values().length - 1;
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
        pageIndex = Math.max(0, Math.min(ConfigPage.values().length - 1, pageIndex + delta));
        rebuildWidgets();
    }

    private void saveAndClose() {
        if (!applyVisibleInputs()) {
            return;
        }
        WolfCompanionConfig.apply(draft);
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

    void clearStatus() {
        statusMessage = null;
        statusIsError = false;
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
