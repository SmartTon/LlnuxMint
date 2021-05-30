/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.systems.modules.extra;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.game.GameJoinedEvent;
import minegame159.meteorclient.events.packets.PacketEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.rendering.ShapeMode;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.friends.Friends;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.player.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import java.util.Random;
import java.util.UUID;

public class AutoEz extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<ChatType> ChatChoice = sgGeneral.add(new EnumSetting.Builder<ChatType>()
            .name("chat")
            .description("How the shapes are rendered.")
            .defaultValue(ChatType.client)
            .build()
    );

    private final Object2IntMap<UUID> totemPops = new Object2IntOpenHashMap<>();
    private final Object2IntMap<UUID> chatIds = new Object2IntOpenHashMap<>();

    private final Random random = new Random();

    public AutoEz() {
        super(Categories.Mint, "AutoEzz", "Sends a chat message when a player either pops a totem or dies.");
    }

    @Override
    public void onActivate() {
        totemPops.clear();
        chatIds.clear();
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        totemPops.clear();
        chatIds.clear();
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof EntityStatusS2CPacket)) return;

        EntityStatusS2CPacket p = (EntityStatusS2CPacket) event.packet;
        if (p.getStatus() != 35) return;

        Entity entity = p.getEntity(mc.world);

        if (entity == null) return;

        synchronized (totemPops) {
            int pops = totemPops.getOrDefault(entity.getUuid(), 0);
            totemPops.put(entity.getUuid(), ++pops);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        synchronized (totemPops) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (!totemPops.containsKey(player.getUuid())) continue;

                if (player.getHealth() <= 0) {
                    switch (ChatChoice.get()){
                        case client:
                            ChatUtils.info(getChatId(player), "EZZZZ {player}, LINUX LINT ON TOP", player.getGameProfile().getName());
                            chatIds.removeInt(player.getUuid());
                            break;
                        case chat:
                            mc.player.sendChatMessage("EZZZZ, LINUX LINT ON TOP");
                    }


                }
            }
        }
    }

    private int getChatId(Entity entity) {
        return chatIds.computeIntIfAbsent(entity.getUuid(), value -> random.nextInt());
    }
    public enum ChatType{
        client,
        chat,
        both
    }
}
