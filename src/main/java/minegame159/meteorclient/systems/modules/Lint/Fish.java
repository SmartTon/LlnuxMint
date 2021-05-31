package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.mixininterface.IVec3d;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import net.minecraft.util.math.Vec3d;



public class Fish extends Module {

    // Descriptions for the ClickGUI
    public Fish() {
        super(Categories.SimpleLint, "fish", "Disables underwater gravity.");
    }

    @EventHandler
    //On every single tick, check these things:
    private void onTick(TickEvent.Post event) {

        // If the sneak key is pressed, keep gravity because you want to go down.
        if (mc.options.keySneak.isPressed()) {
            return;
        }

        //If in water, set upwards velocity equal to downwards velocity, in order to stay stable.
        if (mc.player.isTouchingWater()) {
            Vec3d velocity = mc.player.getVelocity();
            ((IVec3d) velocity).set(velocity.x, 0.005, velocity.z);
        }
    }
}
