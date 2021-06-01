package minegame159.meteorclient.systems.modules.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent.Post;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.settings.StringSetting.Builder;
import minegame159.meteorclient.systems.config.Config;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.Utils;
import minegame159.meteorclient.utils.misc.Placeholders;

public class DiscordPresence extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<String> line1;
    private final Setting<String> line2;
    private final Setting<String> DiscordText;
    private final Setting<String> NaturalDiscordID_nogays;
    private static final DiscordRichPresence rpc = new DiscordRichPresence();
    private static final DiscordRPC instance;
    private DiscordPresence.SmallImage currentSmallImage;
    private int ticks;

    public DiscordPresence() {
        super(Categories.Misc, "discord-presence", "Displays a RPC for you on Discord to show that you're playing Meteor Client!");

        this.sgGeneral = this.settings.getDefaultGroup();
        this.line1 = this.sgGeneral.add((new Builder()).name("line-1").description("The text it displays on line 1 of the RPC.").defaultValue("{username} || {server}").onChanged((booleanSetting) -> {
            this.updateDetails();
        }).build());
        this.line2 = this.sgGeneral.add((new Builder()).name("line-2").description("The text it displays on line 2 of the RPC.").defaultValue("Linux mint on crack!").onChanged((booleanSetting) -> {
            this.updateDetails();
        }).build());
        this.DiscordText = this.sgGeneral.add((new Builder()).name("Discord-text").description("Choosing text of rpc").defaultValue("Linux Mint").onChanged((booleanSetting) -> {
            this.updateDetails();
        }).build());
        this.NaturalDiscordID_nogays = this.sgGeneral.add((new Builder()).name("Discord_id").description("Melon hack - 847117414847873056, Linux Mint - 848987277699186690 ").defaultValue("848987277699186690").onChanged((booleanSetting) -> {
            this.updateDetails();
        }).build());
    }

    public void onActivate() {


        DiscordEventHandlers handlers = new DiscordEventHandlers();
        instance.Discord_Initialize(NaturalDiscordID_nogays.get(), handlers, true, (String) null);
        rpc.startTimestamp = System.currentTimeMillis() / 1000L;
        rpc.largeImageKey = "icon";
        String largeText = DiscordText.get() + " v" + Config.get().version.getOriginalString();

        rpc.largeImageText = largeText;
        this.currentSmallImage = DiscordPresence.SmallImage.iro;
        this.updateDetails();
        instance.Discord_UpdatePresence(rpc);
        instance.Discord_RunCallbacks();
    }

    public void onDeactivate() {
        instance.Discord_ClearPresence();
        instance.Discord_Shutdown();
    }

    @EventHandler
    private void onTick(Post event) {
        if (Utils.canUpdate()) {
            ++this.ticks;
            if (this.ticks >= 200) {
                this.currentSmallImage = this.currentSmallImage.next();
                this.currentSmallImage.apply();
                instance.Discord_UpdatePresence(rpc);
                this.ticks = 0;
            }

            this.updateDetails();
            instance.Discord_RunCallbacks();
        }
    }

    private void updateDetails() {
        if (this.isActive() && Utils.canUpdate()) {
            rpc.details = Placeholders.apply((String) this.line1.get());
            rpc.state = Placeholders.apply((String) this.line2.get());
            instance.Discord_UpdatePresence(rpc);
        }

    }

    static {
        instance = DiscordRPC.INSTANCE;
    }

    private static enum SmallImage {
        iro("images", "iro"),
        gramms("images", "gramms");

        private final String key;
        private final String text;

        private SmallImage(String key, String text) {
            this.key = key;
            this.text = text;
        }

        void apply() {
            DiscordPresence.rpc.smallImageKey = this.key;
            DiscordPresence.rpc.smallImageText = this.text;
        }

        DiscordPresence.SmallImage next() {
            return this == iro ? gramms : gramms;
        }
    }
}
