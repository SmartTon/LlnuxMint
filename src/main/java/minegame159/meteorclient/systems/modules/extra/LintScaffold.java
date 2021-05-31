package minegame159.meteorclient.systems.modules.extra;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LintScaffold extends Module {
    public LintScaffold(){
        super(Categories.Mint, "LintScaffold", "Extra Scaffold");
    }
    public enum Dir{
        UP,
        DOWN
    }

    private final SettingGroup sgGeneral  = settings.getDefaultGroup();


    private final Setting<Dir> direction = sgGeneral.add(new EnumSetting.Builder<Dir>()
            .name("Direction")
            .defaultValue(Dir.DOWN)
            .build()
    );

    private final Setting<Integer> shift = sgGeneral.add(new IntSetting.Builder()
            .name("shift")
            .description("Shift your scaffold. (Up / Down)")
            .defaultValue(0)
            .min(-4)
            .max(6)
            .sliderMin(-4)
            .sliderMax(6)
            .build()
    );

    private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
            .name("radius")
            .description("The radius of your scaffold.")
            .defaultValue(0)
            .min(0)
            .sliderMax(6)
            .build()
    );


    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
            .name("Center first")
            .description("Place center block first")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> fall = sgGeneral.add(new BoolSetting.Builder()
            .name("Allow Falling blocks")
            .defaultValue(true)
            .build()
    );

    @EventHandler
    private void onTick(TickEvent.Post event) {


        if(!(mc.player.getMainHandStack().getItem() instanceof BlockItem)) return;
        Block block = ((BlockItem) mc.player.getMainHandStack().getItem()).getBlock();
        if(block instanceof ShulkerBoxBlock) return;
        if(!fall.get() && block instanceof FallingBlock) return;

        int px = mc.player.getBlockPos().getX();
        int py = mc.player.getBlockPos().getY()-1+shift.get();
        int pz = mc.player.getBlockPos().getZ();


        for (int x = px - radius.get(); x <= px + radius.get(); x++) {
            for (int z = pz - radius.get(); z <= pz + radius.get(); z++) {

                if(center.get() && place(new Vec3d(px,py,pz))) return;

                if (distance(x, py, z) <= mc.interactionManager.getReachDistance() && place(new Vec3d(x, py, z))) return;
            }
        }

    }

    private boolean place(Vec3d pos){
        BlockPos bpos = new BlockPos(pos);
        if(!BlockUtils.canPlace(bpos, true)) return false;
        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(pos, Direction.valueOf(direction.get().toString()), bpos, false));
        return true;
    }

    private double distance(double x, double y, double z) {
        double d = mc.player.getBlockPos().getX() - x;
        double e = mc.player.getBlockPos().getY() - y;
        double f = mc.player.getBlockPos().getZ() - z;
        return MathHelper.sqrt(d * d + e * e + f * f);
    }
}
