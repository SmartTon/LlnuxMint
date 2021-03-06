/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.gui.themes.meteor;

import minegame159.meteorclient.gui.renderer.GuiRenderer;
import minegame159.meteorclient.gui.utils.BaseWidget;
import minegame159.meteorclient.gui.widgets.WWidget;
import minegame159.meteorclient.utils.render.color.Color;

public interface ClassicLintWidget extends BaseWidget {
    default CustomLintTheme theme() {
        return (CustomLintTheme) getTheme();
    }

    default void renderBackground(GuiRenderer renderer, WWidget widget, boolean pressed, boolean mouseOver) {
        CustomLintTheme theme = theme();
        double s = theme.scale(2);

        renderer.quad(widget.x + s, widget.y + s, widget.width - s * 2, widget.height - s * 2, theme.backgroundColor.get(pressed, mouseOver));

        Color outlineColor = theme.outlineColor.get(pressed, mouseOver);
        renderer.quad(widget.x, widget.y, widget.width, s, outlineColor);
        renderer.quad(widget.x, widget.y + widget.height - s, widget.width, s, outlineColor);
        renderer.quad(widget.x, widget.y + s, s, widget.height - s * 2, outlineColor);
        renderer.quad(widget.x + widget.width - s, widget.y + s, s, widget.height - s * 2, outlineColor);
    }
}
