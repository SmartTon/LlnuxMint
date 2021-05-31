package minegame159.meteorclient.systems.modules.Lint;

import com.google.common.eventbus.Subscribe;
import minegame159.meteorclient.events.entity.EventEntityRender;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;

import minegame159.meteorclient.utils.player.ChatUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoEz extends Module {
    public AutoEz(){
        super(Categories.SimpleLint, "AutoEz", "AutoEz in Chat");
    }

    public enum Mode {
        Client,
        Chat,
        Both
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Mode> ModeSettings = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("Mode")
            .defaultValue(Mode.Client)
            .build()
    );
    private final Setting<String> EzMessage = sgGeneral.add(new StringSetting.Builder()
            .name("EzMessage")
            .description("The message")
            .defaultValue("{player} died BY Linux Lint")
            .build()
    );

    private final Random rand = new Random();
    private List<String> lines = new ArrayList<>();
    private int lineCount = 0;
    public List<String> dead_uuids = new ArrayList<>();



    @Override
    public void onActivate() {
        super.onActivate();
        lineCount = 0;
    }

    @Subscribe
    public void onEntityRender(EventEntityRender event) {
        if (lines.isEmpty()) return;
        if (event.getEntity().getType().toString().contains("entity.minecraft.player")){
            if (event.getEntity() == mc.player) return;
            if (
                    !event.getEntity().isAlive()
                            && event.getEntity().distanceTo(mc.player) <= 8
                            && !dead_uuids.toString().contains(event.getEntity().getUuidAsString())
            ) {
                if (ModeSettings.get() == Mode.Chat) {
                    mc.player.sendChatMessage(lines.get(rand.nextInt(lines.size())).replace("$p", event.getEntity().getDisplayName().getString()));
                } else if (ModeSettings.get() == Mode.Client) {
                    ChatUtils.moduleInfo(this, EzMessage.get().replace("{player}", (event.getEntity()).getEntityName()));
                }

                if (lineCount >= lines.size() - 1) lineCount = 0;
                else lineCount++;
                dead_uuids.add(event.getEntity().getUuidAsString());


            } else if (
                    !event.getEntity().isAlive()
                            && event.getEntity().distanceTo(mc.player) > 8
                            && !dead_uuids.toString().contains(event.getEntity().getUuidAsString())
            ) {
                dead_uuids.add(event.getEntity().getUuidAsString());

            }

        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        assert mc.world != null;
        if (mc.player.age % 100 == 0) {
            dead_uuids.clear();
        }
    }

    @Override
    public void onDeactivate() {
        dead_uuids.clear();
        super.onDeactivate();
    }
}
