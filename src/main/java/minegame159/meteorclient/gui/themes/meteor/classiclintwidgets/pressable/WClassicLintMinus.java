/*
 * This file is part of the ClassicLint Client distribution (https://github.com/ClassicLintDevelopment/ClassicLint-client/).
 * Copyright (c) 2021 ClassicLint Development.
 */

package minegame159.meteorclient.gui.themes.meteor.classiclintwidgets.pressable;

import minegame159.meteorclient.gui.renderer.GuiRenderer;
import minegame159.meteorclient.gui.themes.meteor.ClassicLintWidget;
import minegame159.meteorclient.gui.widgets.pressable.WMinus;

public class WClassicLintMinus extends WMinus implements ClassicLintWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double pad = pad();
        double s = theme.scale(3);

        renderBackground(renderer, this, pressed, mouseOver);
        renderer.quad(x + pad, y + height / 2 - s / 2, width - pad * 2, s, theme().minusColor.get());
    }
}
