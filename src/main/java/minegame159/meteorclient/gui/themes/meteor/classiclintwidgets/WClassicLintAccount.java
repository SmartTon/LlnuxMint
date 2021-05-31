/*
 * This file is part of the ClassicLint Client distribution (https://github.com/ClassicLintDevelopment/ClassicLint-client/).
 * Copyright (c) 2021 ClassicLint Development.
 */

package minegame159.meteorclient.gui.themes.meteor.classiclintwidgets;

import minegame159.meteorclient.gui.WidgetScreen;
import minegame159.meteorclient.gui.themes.meteor.ClassicLintWidget;
import minegame159.meteorclient.gui.widgets.WAccount;
import minegame159.meteorclient.systems.accounts.Account;
import minegame159.meteorclient.utils.render.color.Color;

public class WClassicLintAccount extends WAccount implements ClassicLintWidget {
    public WClassicLintAccount(WidgetScreen screen, Account<?> account) {
        super(screen, account);
    }

    @Override
    protected Color loggedInColor() {
        return theme().loggedInColor.get();
    }

    @Override
    protected Color accountTypeColor() {
        return theme().textSecondaryColor.get();
    }
}
