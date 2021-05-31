package minegame159.meteorclient.systems.modules.extra;

import com.sun.org.apache.xpath.internal.operations.Mod;
import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.LivingDeathEvent;
import minegame159.meteorclient.events.entity.player.AttackEntityEvent;
import minegame159.meteorclient.events.packets.PacketEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.systems.modules.combat.AutoTrap;
import minegame159.meteorclient.utils.entity.EntityUtils;
import minegame159.meteorclient.utils.entity.SortPriority;
import minegame159.meteorclient.utils.player.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class AutoEz extends Module {
    public AutoEz(){
        super(Categories.Mint, "AutoEz", "AutoEz in Chat");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> ezed = sgGeneral.add(new StringSetting.Builder()
            .name("text")
            .description("The text you want to send when you ez someone.")
            .defaultValue("Ez! {victim}, {player} owns you and all!")
            .build()
    );

    private int hasBeenCombat;
    private PlayerEntity target;

    @EventHandler
    public void packetSentEvent(PacketEvent.Sent event) {
        if(event.packet instanceof PlayerInteractEntityC2SPacket) {
            PlayerInteractEntityC2SPacket packet = (PlayerInteractEntityC2SPacket) event.packet;

            if(packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
                Entity e = packet.getEntity(mc.world);
                if(e instanceof PlayerEntity) {
                    target = (PlayerEntity) e;
                    hasBeenCombat = 500;
                }

                if(e instanceof EndCrystalEntity) {
                    PlayerEntity newTarget = null;
                    for(PlayerEntity entityPlayer: mc.world.getPlayers()) {
                        if(entityPlayer.isDead()) continue;
                        if((newTarget == null && entityPlayer.distanceTo(e) < 4) ||
                                (newTarget != null && mc.player.distanceTo(entityPlayer) < mc.player.distanceTo(newTarget))) newTarget = entityPlayer;
                    }

                    if(newTarget != null) {
                        target = newTarget;
                        hasBeenCombat = 40;
                    }
                }
            }
        }
    }

    @EventHandler
    public void deathEvent(LivingDeathEvent event) {
        if(event.getEntity() instanceof PlayerEntity) {
            String fart = farte(ezed, target);
            if(hasBeenCombat > 0 && (target.getHealth() <= 0 || target.isDead() || !mc.world.getPlayers().contains(target))) mc.player.sendChatMessage(fart);

            hasBeenCombat = 0;
        }
    }

    private int sinceLastMessage = 0;

    @EventHandler
    private void onTick() {
        if(mc.player.isDead()) hasBeenCombat = 0;

        if(sinceLastMessage == 0 && hasBeenCombat > 0 && (target.getHealth() <= 0 || target.isDead())) {
            String fart = farte(ezed, target);
            mc.player.sendChatMessage(fart);
            sinceLastMessage = 80;
            hasBeenCombat = 0;
        }

        if(sinceLastMessage > 0) sinceLastMessage--;

        hasBeenCombat--;
    }
    private String farte(Setting<String> line, PlayerEntity player) {
        if (line.get().length() > 0) return line.get().replace("{player}", getName()).replace("{victim}", player.getGameProfile().getName());
        else return null;
    }

    private String getName(){
        return mc.player.getGameProfile().getName();
    }



}
