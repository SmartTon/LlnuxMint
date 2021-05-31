package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.packets.PacketEvent;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.friends.Friend;
import minegame159.meteorclient.systems.friends.Friends;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class NewChat extends Module {
    public NewChat(){
        super(Categories.SimpleLint,"New Chat", "chat fix");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> joingray = sgGeneral.add(new BoolSetting.Builder()
            .name("join-leave-gray")
            .description("Set gray color for join/leave messages.")
            .defaultValue(true)
            .build()
    );


    private final Setting<Boolean> joinoff = sgGeneral.add(new BoolSetting.Builder()
            .name("disable-join-leave")
            .description("Disable join/leave messages.")
            .defaultValue(false)
            .build()
    );



    @EventHandler
    private void msg(PacketEvent.Receive e) {
        if (e.packet instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket p = (GameMessageS2CPacket) e.packet;


            String s = p.getMessage().getString().replaceAll("§.", "");
            String nick = s.replaceAll(" .*", "");

            if( (joinoff.get() || joingray.get()) && (
                    s.endsWith(" left the game")
                            || s.endsWith(" joined the game")
                            || s.endsWith(" присоединился к игре")
                            || s.endsWith(" покинул игру")
            ) ){
                e.cancel();

                if(joinoff.get()) return;


                if(s.endsWith(" joined the game") || s.endsWith(" присоединился к игре")) say("§7"+colorize(nick)+"§7 － зашёл на сервер");
                if(s.endsWith(" left the game") || s.endsWith(" покинул игру")) say("§8"+colorize(nick)+"§8 － вышел с сервера");
                return;
            }

            if(s.matches("^<\\w+> .*")){
                e.cancel();

                nick = colorize(nick.replaceAll("\\W+", ""));
                String msg = s.replaceFirst(" ", "\0").replaceFirst(".*\0", "");

                say("§6"+nick+" §a➠§f "+msg);
                return;
            }

            if(s.matches("^Вы прошептали \\w+: .*")){
                e.cancel();
                nick = s.replaceFirst("Вы прошептали ", "").replaceFirst(":.*", "");
                say("§9 [§6Я §c→§6 "+colorize(nick)+"§9] §a➠§f "+s.replaceFirst(": ", "\0").replaceFirst(".*\0", ""));
                return;
            }


            if(s.matches("^\\w+ шепчет вам: .*")){
                e.cancel();
                nick = s.replaceFirst(" .*", "");
                say("§9 [§6"+colorize(nick)+" §a→§6 Я§9] §a➠§f "+s.replaceFirst(": ", "\0").replaceFirst(".*\0", ""));
            }


        }
    }

    private String colorize(String s){
        if(mc.player.getName().getString().equals(s)) return "§c"+s;
        if(Friends.get().getAll().contains(new Friend(s))) return "§a"+s;
//    	462b15
        return s;

    }

    private void say(String a){mc.inGameHud.getChatHud().addMessage(new net.minecraft.text.LiteralText(a));};
}
