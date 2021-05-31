package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.render.RenderEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.rendering.Renderer;
import minegame159.meteorclient.rendering.ShapeMode;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.render.color.SettingColor;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class AutoWither extends Module {
    public AutoWither() {
        super(Categories.ExtraLint, "AutoWither", "Build wither");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("place-delay")
            .description("How many ticks between block placements.")
            .defaultValue(1)
            .sliderMin(0)
            .sliderMax(10)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Automatically faces towards the obsidian being placed.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("Renders a block overlay where the obsidian will be placed.")
            .defaultValue(true)
            .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The color of the sides of the blocks being rendered.")
            .defaultValue(new SettingColor(204, 0, 0, 10))
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The color of the lines of the blocks being rendered.")
            .defaultValue(new SettingColor(204, 0, 0, 255))
            .build()
    );

    private enum Direction {
        SOUTH,
        SOUTH_WEST,
        WEST,
        WEST_NORTH,
        NORTH,
        NORTH_EAST,
        EAST,
        EAST_SOUTH
    }
    private Direction direction;

    private final List<BlockPos> placePositions = new ArrayList<>();
    private boolean return_;
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private int timer;

    @Override
    public void onActivate() {
        direction = getDirection(mc.player);
        blockPos.set(mc.player.getBlockPos());
        changeBlockPos(0,-1,0);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if(InvUtils.findItemInHotbar(Items.SOUL_SAND) < 3 && InvUtils.findItemInHotbar(Items.WITHER_SKELETON_SKULL) < 3) return;

        return_ = false;

        if(getDistance(mc.player) > 12) return;

        if(direction == Direction.SOUTH){
            boolean p1 = place(0, 1, 2);
            if (return_) return;
            boolean p2 = place(0, 2, 2);
            if (return_) return;
            boolean p3 = place(-1, 2, 2);
            if (return_) return;
            boolean p4 = place(1, 2, 2);
            if (return_) return;
            boolean p5 = placeSkull(0,3,2);
            if (return_) return;
            boolean p6 = placeSkull(-1,3,2);
            if (return_) return;
            boolean p7 = placeSkull(1,3,2);
            if (return_) return;
            return_ = false;
        }else if(direction == Direction.WEST){

            boolean p1 = place(-2, 1, 0);
            if (return_) return;
            boolean p2 = place(-2, 2, 0);
            if (return_) return;
            boolean p3 = place(-2, 2, -1);
            if (return_) return;
            boolean p4 = place(-2, 2, 1);
            if (return_) return;
            boolean p5 = placeSkull(-2,3,0);
            if (return_) return;
            boolean p6 = placeSkull(-2,3,-1);
            if (return_) return;
            boolean p7 = placeSkull(-2,3,1);
            if (return_) return;
            return_ = false;

        }else if(direction == Direction.NORTH){

            boolean p1 = place(0, 1, -2);
            if (return_) return;
            boolean p2 = place(0, 2, -2);
            if (return_) return;
            boolean p3 = place(-1, 2, -2);
            if (return_) return;
            boolean p4 = place(1, 2, -2);
            if (return_) return;
            boolean p7 = placeSkull(0,3,-2);
            if (return_) return;
            boolean p8 = placeSkull(1,3,-2);
            if (return_) return;
            boolean p9 = placeSkull(-1,3,-2);
            if (return_) return;
            return_ = false;
        }else if(direction == Direction.EAST){

            boolean p1 = place(2, 1, 0);
            if (return_) return;
            boolean p2 = place(2, 2, 0);
            if (return_) return;
            boolean p3 = place(2, 2, -1);
            if (return_) return;
            boolean p4 = place(2, 2, 1);
            if (return_) return;
            boolean p7 = placeSkull(2,3,0);
            if (return_) return;
            boolean p8 = placeSkull(2,3,1);
            if (return_) return;
            boolean p9 = placeSkull(2,3,-1);
            if (return_) return;
            return_ = false;
        }
    }

    @EventHandler
    private void onRender(RenderEvent event) {
        if (!render.get() || placePositions.isEmpty()) return;
        for (BlockPos pos : placePositions) Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
    

    private int getDistance(PlayerEntity player){
        return (int) Math.round(player.squaredDistanceTo(blockPos.getX(), blockPos.getY()-player.getStandingEyeHeight(), blockPos.getZ()));
    }

    private boolean placeSkull(int x, int y, int z){
        BlockPos placePos2 = setBlockPos(x, y, z);
        BlockState blockState2 = mc.world.getBlockState(placePos2);

        if (!blockState2.getMaterial().isReplaceable()) return true;

        int slot2 = findSkullSlot();
        if (timer >= delay.get()) {
            if (BlockUtils.place(placePos2, Hand.MAIN_HAND, slot2, rotate.get(), 10, true)) {
                return_ = true;
            }
        }
        else {
            timer++;
        }

        return false;

    }

    private boolean place(int x, int y, int z) {
        BlockPos placePos = setBlockPos(x, y, z);
        BlockState blockState = mc.world.getBlockState(placePos);

        if (!blockState.getMaterial().isReplaceable()) return true;

        int slot = findSlot();
        if (timer >= delay.get()) {
            if (BlockUtils.place(placePos, Hand.MAIN_HAND, slot, rotate.get(), 10, true)) {
            return_ = true;
            }
            else {
                timer++;
            }
        }

        return false;
    }


    private void changeBlockPos(int x, int y, int z) {
        blockPos.set(blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z);
    }
    private BlockPos setBlockPos(int x, int y, int z) {
        return new BlockPos(blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z);
    }

    private Direction getDirection(PlayerEntity player){
        double yaw = player.yaw;
        if(yaw==0) return Direction.SOUTH;
        if(yaw<0){
            yaw = yaw - MathHelper.ceil(yaw / 360) * 360;
            if(yaw<-180) {
                yaw = 360 + yaw;
            }
        }else{
            yaw = yaw - MathHelper.floor(yaw / 360)*360;
            if(yaw>180) {
                yaw = -360 + yaw;
            }
        }

        if(yaw >= 157.5 || yaw < -157.5) return Direction.NORTH;
        if(yaw >= -157.5 && yaw < -112.5) return Direction.NORTH_EAST;
        if(yaw >= -112.5 && yaw < -67.5) return Direction.EAST;
        if(yaw >= -67.5 && yaw < -22.5) return Direction.EAST_SOUTH;
        if((yaw >= -22.5 && yaw <=0) || (yaw > 0 && yaw < 22.5)) return Direction.SOUTH;
        if(yaw >= 22.5 && yaw < 67.5) return Direction.SOUTH_WEST;
        if(yaw >= 67.5 && yaw < 112.5) return Direction.WEST;
        if(yaw >= 112.5 && yaw < 157.5) return Direction.WEST_NORTH;
        return Direction.SOUTH;
    }


    private int findSlot() {
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStack(i).getItem();

            if (!(item instanceof BlockItem)) continue;

            if (item == Items.SOUL_SAND) {
                return i;
            }
        }

        return -1;
    }

    private int findSkullSlot() {
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStack(i).getItem();

            if (!(item instanceof BlockItem)) continue;

            if (item == Items.WITHER_SKELETON_SKULL) {
                return i;
            }
        }

        return -1;
    }
}
