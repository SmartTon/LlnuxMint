/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.LiteralText;

public class AutoLeave extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();







    public AutoLeave() {
        super(Categories.SimpleLint, "AutoLeave", "Leaves server");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("ya just left")));
            toggle();
        }

    }

