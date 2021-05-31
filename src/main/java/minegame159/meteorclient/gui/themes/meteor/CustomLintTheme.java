/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.gui.themes.meteor;

import minegame159.meteorclient.gui.DefaultSettingsWidgetFactory;
import minegame159.meteorclient.gui.GuiTheme;
import minegame159.meteorclient.gui.WidgetScreen;
import minegame159.meteorclient.gui.renderer.packer.GuiTexture;
import minegame159.meteorclient.gui.themes.meteor.classiclintwidgets.*;
import minegame159.meteorclient.gui.themes.meteor.classiclintwidgets.input.WClassicLintDropdown;
import minegame159.meteorclient.gui.themes.meteor.classiclintwidgets.input.WClassicLintSlider;
import minegame159.meteorclient.gui.themes.meteor.classiclintwidgets.input.WClassicLintTextBox;
import minegame159.meteorclient.gui.themes.meteor.classiclintwidgets.pressable.*;
import minegame159.meteorclient.gui.utils.AlignmentX;
import minegame159.meteorclient.gui.utils.CharFilter;
import minegame159.meteorclient.gui.widgets.*;
import minegame159.meteorclient.gui.widgets.containers.WSection;
import minegame159.meteorclient.gui.widgets.containers.WView;
import minegame159.meteorclient.gui.widgets.containers.WWindow;
import minegame159.meteorclient.gui.widgets.input.WDropdown;
import minegame159.meteorclient.gui.widgets.input.WSlider;
import minegame159.meteorclient.gui.widgets.input.WTextBox;
import minegame159.meteorclient.gui.widgets.pressable.*;
import minegame159.meteorclient.rendering.text.TextRenderer;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.accounts.Account;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.render.color.Color;
import minegame159.meteorclient.utils.render.color.SettingColor;

import static minegame159.meteorclient.utils.Utils.mc;

public class CustomLintTheme extends GuiTheme {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgColors = settings.createGroup("Colors");
    private final SettingGroup sgTextColors = settings.createGroup("Text");
    private final SettingGroup sgBackgroundColors = settings.createGroup("Background");
    private final SettingGroup sgOutline = settings.createGroup("Outline");
    private final SettingGroup sgSeparator = settings.createGroup("Separator");
    private final SettingGroup sgScrollbar = settings.createGroup("Scrollbar");
    private final SettingGroup sgSlider = settings.createGroup("Slider");

    // General

    public final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
            .name("scale")
            .description("Scale of the GUI.")
            .defaultValue(0.968)
            .min(0.75)
            .sliderMin(0.75)
            .sliderMax(4)
            .onSliderRelease()
            .onChanged(aDouble -> {
                if (mc.currentScreen instanceof WidgetScreen) ((WidgetScreen) mc.currentScreen).invalidate();
            })
            .build()
    );

    public final Setting<AlignmentX> moduleAlignment = sgGeneral.add(new EnumSetting.Builder<AlignmentX>()
            .name("module-alignment")
            .description("How module titles are aligned.")
            .defaultValue(AlignmentX.Center)
            .build()
    );

    public final Setting<Boolean> categoryIcons = sgGeneral.add(new BoolSetting.Builder()
            .name("category-icons")
            .description("Adds item icons to module categories.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> blur = sgGeneral.add(new BoolSetting.Builder()
            .name("blur")
            .description("Apply blur behind the GUI.")
            .defaultValue(false)
            .build()
    );

    // Colors

    public final Setting<SettingColor> accentColor = color("accent", "Main color of the GUI.", new SettingColor(204, 204, 204));
    public final Setting<SettingColor> checkboxColor = color("checkbox", "Color of checkbox.", new SettingColor(35, 167, 255));
    public final Setting<SettingColor> plusColor = color("plus", "Color of plus button.", new SettingColor(255, 255, 255));
    public final Setting<SettingColor> minusColor = color("minus", "Color of minus button.", new SettingColor(255, 255, 255));

    // Text

    public final Setting<SettingColor> textColor = color(sgTextColors, "text", "Color of text.", new SettingColor(63, 63, 63));
    public final Setting<SettingColor> textSecondaryColor = color(sgTextColors, "text-secondary-text", "Color of secondary text.", new SettingColor(63, 63, 63));
    public final Setting<SettingColor> titleTextColor = color(sgTextColors, "title-text", "Color of title text.", new SettingColor(45, 45, 45));
    public final Setting<SettingColor> loggedInColor = color(sgTextColors, "logged-in-text", "Color of logged in account name.", new SettingColor(255, 255, 255));

    // Background

    public final ThreeStateColorSetting backgroundColor = new ThreeStateColorSetting(
            sgBackgroundColors,
            "background",
            new SettingColor(255, 255, 255, 230),
            new SettingColor(249, 249, 249, 255),
            new SettingColor(249, 249, 249, 200)
    );

    public final Setting<SettingColor> moduleBackground = color(sgBackgroundColors, "module-background", "Color of module background when active.", new SettingColor(45, 177, 255));

    // Outline

    public final ThreeStateColorSetting outlineColor = new ThreeStateColorSetting(
            sgOutline,
            "outline",
            new SettingColor(63, 63, 63),
            new SettingColor(10, 120, 200),
            new SettingColor(10, 100, 255)
    );

    // Separator

    public final Setting<SettingColor> separatorText = color(sgSeparator, "separator-text", "Color of separator text", new SettingColor(63, 63, 63));
    public final Setting<SettingColor> separatorCenter = color(sgSeparator, "separator-center", "Center color of separators.", new SettingColor(63, 63, 63));
    public final Setting<SettingColor> separatorEdges = color(sgSeparator, "separator-edges", "Color of separator edges.", new SettingColor(220, 220, 220, 255));

    // Scrollbar

    public final ThreeStateColorSetting scrollbarColor = new ThreeStateColorSetting(
            sgScrollbar,
            "Scrollbar",
            new SettingColor(40, 40, 40, 200),
            new SettingColor(40, 40, 40, 200),
            new SettingColor(50, 50, 40, 200)
    );

    // Slider

    public final ThreeStateColorSetting sliderHandle = new ThreeStateColorSetting(
            sgSlider,
            "slider-handle",
            new SettingColor(30, 160, 255),
            new SettingColor(30, 160, 255),
            new SettingColor(30, 160, 255)
    );

    public final Setting<SettingColor> sliderLeft = color(sgSlider, "slider-left", "Color of slider left part.", new SettingColor(10, 110, 255));
    public final Setting<SettingColor> sliderRight = color(sgSlider, "slider-right", "Color of slider right part.", new SettingColor(70, 180, 225));

    public CustomLintTheme() {
        super("Classic-Lint");

        settingsFactory = new DefaultSettingsWidgetFactory(this);
    }



    private Setting<SettingColor> color(SettingGroup group, String name, String description, SettingColor color) {
        return group.add(new ColorSetting.Builder()
                .name(name + "-color")
                .description(description)
                .defaultValue(color)
                .build());
    }
    private Setting<SettingColor> color(String name, String description, SettingColor color) {
        return color(sgColors, name, description, color);
    }

    // Widgets

    @Override
    public WWindow window(String title) {
        return w(new WClassicLintWindow(title));
    }

    @Override
    public WLabel label(String text, boolean title, double maxWidth) {
        if (maxWidth == 0) return w(new WClassicLintLabel(text, title));
        return w(new WClassicLintMultiLabel(text, title, maxWidth));
    }

    @Override
    public WHorizontalSeparator horizontalSeparator(String text) {
        return w(new WClassicLintHorizontalSeparator(text));
    }

    @Override
    public WVerticalSeparator verticalSeparator() {
        return w(new WClassicLintVerticalSeparator());
    }

    @Override
    protected WButton button(String text, GuiTexture texture) {
        return w(new WClassicLintButton(text, texture));
    }

    @Override
    public WMinus minus() {
        return w(new WClassicLintMinus());
    }

    @Override
    public WPlus plus() {
        return w(new WClassicLintPlus());
    }

    @Override
    public WCheckbox checkbox(boolean checked) {
        return w(new WClassicLintCheckbox(checked));
    }

    @Override
    public WSlider slider(double value, double min, double max) {
        return w(new WClassicLintSlider(value, min, max));
    }

    @Override
    public WTextBox textBox(String text, CharFilter filter) {
        return w(new WClassicLintTextBox(text, filter));
    }

    @Override
    public <T> WDropdown<T> dropdown(T[] values, T value) {
        return w(new WClassicLintDropdown<>(values, value));
    }

    @Override
    public WTriangle triangle() {
        return w(new WClassicLintTriangle());
    }

    @Override
    public WTooltip tooltip(String text) {
        return w(new WClassicLintTooltip(text));
    }

    @Override
    public WView view() {
        return w(new WClassicLintView());
    }

    @Override
    public WSection section(String title, boolean expanded, WWidget headerWidget) {
        return w(new WClassicLintSection(title, expanded, headerWidget));
    }

    @Override
    public WAccount account(WidgetScreen screen, Account<?> account) {
        return w(new WClassicLintAccount(screen, account));
    }

    @Override
    public WWidget module(Module module) {
        return w(new WClassicLintModule(module));
    }

    @Override
    public WQuad quad(Color color) {
        return w(new WClassicLintQuad(color));
    }

    @Override
    public WTopBar topBar() {
        return w(new WClassicLintTopBar());
    }

    // Colors

    @Override
    public Color textColor() {
        return textColor.get();
    }

    @Override
    public Color textSecondaryColor() {
        return textSecondaryColor.get();
    }

    // Other

    @Override
    public TextRenderer textRenderer() {
        return TextRenderer.get();
    }

    @Override
    public double scale(double value) {
        return value * scale.get();
    }

    @Override
    public boolean categoryIcons() {
        return categoryIcons.get();
    }

    @Override
    public boolean blur() {
        return blur.get();
    }

    public class ThreeStateColorSetting {
        private final Setting<SettingColor> normal, hovered, pressed;

        public ThreeStateColorSetting(SettingGroup group, String name, SettingColor c1, SettingColor c2, SettingColor c3) {
            normal = color(group, name, "Color of " + name + ".", c1);
            hovered = color(group, "hovered-" + name, "Color of " + name + " when hovered.", c2);
            pressed = color(group, "pressed-" + name, "Color of " + name + " when pressed.", c3);
        }

        public SettingColor get() {
            return normal.get();
        }

        public SettingColor get(boolean pressed, boolean hovered, boolean bypassDisableHoverColor) {
            if (pressed) return this.pressed.get();
            return (hovered && (bypassDisableHoverColor || !disableHoverColor)) ? this.hovered.get() : this.normal.get();
        }

        public SettingColor get(boolean pressed, boolean hovered) {
            return get(pressed, hovered, false);
        }
    }
}
