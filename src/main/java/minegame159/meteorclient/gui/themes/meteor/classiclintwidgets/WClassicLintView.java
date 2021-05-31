/*
 * This file is part of the ClassicLint Client distribution (https://github.com/ClassicLintDevelopment/ClassicLint-client/).
 * Copyright (c) 2021 ClassicLint Development.
 */

package minegame159.meteorclient.gui.themes.meteor.classiclintwidgets;

import minegame159.meteorclient.gui.renderer.GuiRenderer;
import minegame159.meteorclient.gui.themes.meteor.ClassicLintWidget;
import minegame159.meteorclient.gui.widgets.containers.WView;

public class WClassicLintView extends WView implements ClassicLintWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (canScroll && hasScrollBar) {
            renderer.quad(handleX(), handleY(), handleWidth(), handleHeight(), theme().scrollbarColor.get(handlePressed, handleMouseOver));
        }
    }
}
