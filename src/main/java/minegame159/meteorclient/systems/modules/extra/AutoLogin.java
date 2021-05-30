/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.systems.modules.extra;

//Updated by squidoodly 24/07/2020

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.EntityAddedEvent;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.settings.StringSetting;
import minegame159.meteorclient.systems.friends.Friend;
import minegame159.meteorclient.systems.friends.Friends;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;

public class AutoLogin extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> message = sgGeneral.add(new StringSetting.Builder()
            .name("password")
            .description("password")
            .defaultValue("12345678")
            .build()
    );
    
    public AutoLogin() {
        super(Categories.Mint, "auto-login", "login u");
    }

    public void onActivate() {
            mc.player.sendChatMessage("/login " + message );
        mc.player.sendChatMessage("/l " + message );
        mc.player.sendChatMessage("/register " + message + " " + message );
        mc.player.sendChatMessage("/register " + message);
            toggle();
        }
    }

