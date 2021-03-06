/*
 * This file is part of the ClassicLint Client distribution (https://github.com/ClassicLintDevelopment/ClassicLint-client/).
 * Copyright (c) 2021 ClassicLint Development.
 */

package minegame159.meteorclient.gui.themes.meteor.classiclintwidgets.input;

import minegame159.meteorclient.gui.renderer.GuiRenderer;
import minegame159.meteorclient.gui.themes.meteor.CustomLintTheme;
import minegame159.meteorclient.gui.themes.meteor.ClassicLintWidget;
import minegame159.meteorclient.gui.utils.CharFilter;
import minegame159.meteorclient.gui.widgets.input.WTextBox;
import minegame159.meteorclient.utils.Utils;

public class WClassicLintTextBox extends WTextBox implements ClassicLintWidget {
    private boolean cursorVisible;
    private double cursorTimer;

    private double animProgress;

    public WClassicLintTextBox(String text, CharFilter filter) {
        super(text, filter);
    }

    @Override
    protected void onCursorChanged() {
        cursorVisible = true;
        cursorTimer = 0;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (cursorTimer >= 1) {
            cursorVisible = !cursorVisible;
            cursorTimer = 0;
        }
        else {
            cursorTimer += delta * 1.75;
        }

        renderBackground(renderer, this, false, false);
        renderTextAndCursor(renderer, delta);
    }

    private void renderTextAndCursor(GuiRenderer renderer, double delta) {
        CustomLintTheme theme = theme();
        double pad = pad();

        double overflowWidth = getOverflowWidthForRender();

        if (!text.isEmpty()) {
            renderer.scissorStart(x + pad, y + pad, width - pad * 2, height - pad * 2);
            renderer.text(text, x + pad - overflowWidth, y + pad, theme.textColor.get(), false);
            renderer.scissorEnd();
        }

        animProgress += delta * 10 * (focused && cursorVisible ? 1 : -1);
        animProgress = Utils.clamp(animProgress, 0, 1);

        if ((focused && cursorVisible) || animProgress > 0) {
            renderer.setAlpha(animProgress);
            renderer.quad(x + pad + getCursorTextWidth() - overflowWidth, y + pad, theme.scale(1), theme.textHeight(), theme.textColor.get());
            renderer.setAlpha(1);
        }
    }
}
