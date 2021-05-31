package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class AutoPortalBuilder extends Module {
    public AutoPortalBuilder(){
        super(Categories.SimpleLint,"AutoPortalBuilder", "Build Portal");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();



    private final Setting<Boolean> disableOnMove = sgGeneral.add(new BoolSetting.Builder()
            .name("disable-on-move")
            .description("Automatically disables when you move.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Automatically faces towards the obsidian being placed.")
            .defaultValue(false)
            .build()
    );

    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private boolean return_;

    byte[] x,z;
    int[] ppos = {0,0,0};

    @SuppressWarnings("incomplete-switch")
    @Override
    public void onActivate() {
        ppos[0] = MathHelper.floor(mc.player.getPos().x);
        ppos[1] = MathHelper.floor(mc.player.getPos().y);
        ppos[2] = MathHelper.floor(mc.player.getPos().z);
        byte[] v1 = {2,2, 2,2, 2,2, 2,2, 2,2, 2};
        byte[] v_1 = {-2,-2, -2,-2, -2,-2, -2,-2, -2,-2, -2};
        byte[] vf = {0,-1, 1,-2, 1,-2, 1,-2, 0,-1, 0};

        switch (mc.player.getHorizontalFacing()) {
            case EAST:
                x = v1;
                z = vf;
                break;
            case SOUTH:
                x = vf;
                z = v1;
                break;
            case WEST:
                x = v_1;
                z = vf;
                break;
            case NORTH:
                x = vf;
                z = v_1;
                break;
        }

    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (disableOnMove.get() &&
                (mc.options.keyJump.isPressed()||
                        ppos[0]!= MathHelper.floor(mc.player.getPos().x)||
                        ppos[1]!=MathHelper.floor(mc.player.getPos().y)||
                        ppos[2]!=MathHelper.floor(mc.player.getPos().z)
                )
        ) {
            toggle();
            return;
        }





        // Place
        return_ = false;

        boolean p1  = place(x[0],  0, z[0]);	if (return_) return;
        boolean p2  = place(x[1],  0, z[1]);	if (return_) return;
        boolean p3  = place(x[2],  1, z[2]);	if (return_) return;
        boolean p4  = place(x[3],  1, z[3]);	if (return_) return;
        boolean p5  = place(x[4],  2, z[4]);	if (return_) return;
        boolean p6  = place(x[5],  2, z[5]);	if (return_) return;
        boolean p7  = place(x[6],  3, z[6]);	if (return_) return;
        boolean p8  = place(x[7],  3, z[7]);	if (return_) return;
        boolean p9  = place(x[8],  4, z[8]);	if (return_) return;
        boolean p10 = place(x[9],  4, z[9]);	if (return_) return;



        // Auto turn off
        if (
                p1
                        && p2
                        && p3
                        && p4
                        && p5
                        && p6
                        && p7
                        && p8
                        && p9
                        && p10
        ){

            int fire = InvUtils.findItemInHotbar(Items.FLINT_AND_STEEL);

            if (fire == -1) fire = InvUtils.findItemInHotbar(Items.FIRE_CHARGE);

            if (fire == -1) return;

            if(fire!=-1){
                BlockPos pos = new BlockPos(mc.player.getX()+x[10], mc.player.getY()+1, mc.player.getZ()+z[10]);
                int preSlot = mc.player.inventory.selectedSlot;
                mc.player.inventory.selectedSlot = fire;
                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND,
                        new BlockHitResult(mc.player.getPos(), Direction.UP, pos, true));

                mc.player.inventory.selectedSlot = preSlot;
            }

            toggle();
        }


    };

    private boolean place(int x, int y, int z) {
        setBlockPos(x, y, z);

        if( (mc.player.getY()+y)<0 || (mc.player.getY()+y)>254)
            return true;

        BlockState blockState = mc.world.getBlockState(blockPos);
        boolean placed = !blockState.getMaterial().isReplaceable();

        int slot = InvUtils.findItemInHotbar(Items.OBSIDIAN);
        if (BlockUtils.place(blockPos, Hand.MAIN_HAND, slot, rotate.get(), 100,true)) {
            return_ = true;
        }

        return placed;
    }


    private void setBlockPos(int x, int y, int z) {
        blockPos.set(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);
    }
}
