package minegame159.meteorclient.systems.modules.extra;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.player.SendMessageEvent;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.settings.StringSetting;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;

import java.util.Random;

public class AntiSpam extends Module {
    public AntiSpam(){
        super(Categories.Mint,"AntiSpam", "AntiSpam");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> format = sgGeneral.add(new StringSetting.Builder()
            .name("format")
            .description("%msg% = chat message.\n%random% = random value.")
            .defaultValue("%msg% >%random%<")
            .build()
    );

    @EventHandler(priority = -10000)
    private void onSendMessage(SendMessageEvent e) {

        if(e.msg.startsWith(".")) return;

        Random r = new Random();
        String eng = "abcdefghijklmnopqrstuvwxyz";
        String sum = eng + eng.toUpperCase() + "0123456789";

        int min = 6;
        int max = 15;
        int num = r.nextInt(max + 1 - min) + min;

        String random="";
        for (int x = 0; x < num; x++){
            char c = sum.charAt(r.nextInt(sum.length()));
            random=random+c;
        }

        String result = format.get().replace("%random%", random).replace("%msg%", e.msg);

        e.msg = result;
    }
}
