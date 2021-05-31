package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
//import minegame159.meteorclient.events.entity.player.PushOutOfBlockEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.DoubleSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;


public class Phase extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> clipDistance = sgGeneral.add(new DoubleSetting.Builder()
            .name("Clip Distance")
            .description("The distance per clip.")
            .defaultValue(.01)
            .min(0)
            .max(1)
            .build()
    );

    public Phase() {
        super(Categories.ExtraLint, "phase", "Just fucking phase");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        ClientPlayerEntity p = mc.player;
        double blocks = clipDistance.get();

        if (!p.isOnGround()) return;

            if(mc.options.keyForward.isPressed()){
                Vec3d forward = Vec3d.fromPolar(0, p.yaw);
                p.updatePosition(p.getX() + forward.x * blocks, p.getY(), p.getZ() + forward.z * blocks);
            }

            if(mc.options.keyBack.isPressed()){
                Vec3d forward = Vec3d.fromPolar(0, p.yaw - 180);
                p.updatePosition(p.getX() + forward.x * blocks, p.getY(), p.getZ() + forward.z * blocks);
            }

            if(mc.options.keyLeft.isPressed()){
                Vec3d forward = Vec3d.fromPolar(0, p.yaw - 90);
                p.updatePosition(p.getX() + forward.x * blocks, p.getY(), p.getZ() + forward.z * blocks);
            }

            if(mc.options.keyRight.isPressed()) {
                Vec3d forward = Vec3d.fromPolar(0, p.yaw - 270);
                p.updatePosition(p.getX() + forward.x * blocks, p.getY(), p.getZ() + forward.z * blocks);
            }

            if (mc.options.keyJump.isPressed()) {
                p.updatePosition(p.getX(), p.getY() + 0.05, p.getZ());
            }

            if (mc.options.keySneak.isPressed()) {
                p.updatePosition(p.getX(), p.getY() - 0.05, p.getZ());
            }
    }

//    @EventHandler
//    private void onPushOutOfBlock(PushOutOfBlockEvent event) {
//        event.cancel();
//    }

}