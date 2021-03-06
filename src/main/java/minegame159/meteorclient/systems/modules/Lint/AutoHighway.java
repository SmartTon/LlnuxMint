package minegame159.meteorclient.systems.modules.Lint;

import minegame159.meteorclient.events.entity.player.ClipAtLedgeEvent;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.movement.AutoWalk;
import minegame159.meteorclient.utils.misc.input.Input;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AutoHighway extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> disableOnJump = sgGeneral.add(new BoolSetting.Builder()
            .name("disable-on-jump")
            .description("Automatically disables when you jump.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> size = sgGeneral.add(new IntSetting.Builder()
            .name("highway-size")
            .description("The size of highway.")
            .defaultValue(3)
            .min(3)
            .sliderMin(3)
            .max(7)
            .sliderMax(7)
            .build()
    );

    private final Setting<Boolean> safeWalk = sgGeneral.add(new BoolSetting.Builder()
            .name("safe-walk")
            .description("safe walk")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> autoWalk = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-walk")
            .description("auto walk")
            .defaultValue(false)
            .build()
    );

    private final Setting<AutoWalk.Direction> direction2 = sgGeneral.add(new EnumSetting.Builder<AutoWalk.Direction>()
            .name("simple-direction")
            .description("The direction to walk in simple mode.")
            .defaultValue(AutoWalk.Direction.Forwards)
            .onChanged(direction1 -> {
                if (isActive()) unpress();
            })
            .build()
    );


    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Automatically faces towards the obsidian being placed.")
            .defaultValue(true)
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
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private boolean return_;
    private int highwaySize;





    public AutoHighway() {

        super(Categories.ExtraLint, "auto-highway", "Automatically build highway.");
    }

    @Override
    public void onActivate() {
        direction = getDirection(mc.player);
        blockPos.set(mc.player.getBlockPos());
        changeBlockPos(0,-1,0);
    }

    @EventHandler
    private void onClipAtLedge(ClipAtLedgeEvent event) {
        if (mc.player.input.sneaking) {
            event.setClip(false);
            return;
        }

        if (safeWalk.get()) event.setClip(true);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (disableOnJump.get() && mc.options.keyJump.isPressed()) {
            toggle();
            return;
        }

        if (autoWalk.get()){
            switch (direction2.get()) {
                case Forwards:
                    setPressed(mc.options.keyForward, true);
                    break;
                case Backwards:
                    setPressed(mc.options.keyBack, true);
                    break;
                case Left:
                    setPressed(mc.options.keyLeft, true);
                    break;
                case Right:
                    setPressed(mc.options.keyRight, true);
                    break;
            }
        }

        // Check Obsidian
        if(InvUtils.findItemInHotbar(Items.OBSIDIAN) == -1) return;
        // Get Size
        highwaySize = getSize();
        // Place
        return_ = false;
        // Distance Check
        if(getDistance(mc.player) > 12) return;
        // Placing
        if(direction == Direction.SOUTH){
            if(highwaySize == 3) {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, 0);
                if (return_) return;
                boolean p3 = place(-1, 0, 0);
                if (return_) return;
                boolean p4 = place(-2, 1, 0);
                if (return_) return;
                boolean p5 = place(2, 1, 0);
                if(p1&&p2&&p3&&p4&&p5) nextLayer();
            }else if(highwaySize == 5){
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, 0);
                if (return_) return;
                boolean p3 = place(-1, 0, 0);
                if (return_) return;
                boolean p4 = place(-2, 0, 0);
                if (return_) return;
                boolean p5 = place(2, 0, 0);
                if (return_) return;
                boolean p6 = place(-3, 1, 0);
                if (return_) return;
                boolean p7 = place(3, 1, 0);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7) nextLayer();
            }else {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, 0);
                if (return_) return;
                boolean p3 = place(-1, 0, 0);
                if (return_) return;
                boolean p4 = place(-2, 0, 0);
                if (return_) return;
                boolean p5 = place(2, 0, 0);
                if (return_) return;
                boolean p6 = place(-3, 0, 0);
                if (return_) return;
                boolean p7 = place(3, 0, 0);
                if (return_) return;
                boolean p8 = place(-4, 1, 0);
                if (return_) return;
                boolean p9 = place(4, 1, 0);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9) nextLayer();
            }
        }else if(direction == Direction.WEST){
            if(highwaySize == 3) {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(0, 0, 1);
                if (return_) return;
                boolean p3 = place(0, 0, -1);
                if (return_) return;
                boolean p4 = place(0, 1, -2);
                if (return_) return;
                boolean p5 = place(0, 1, 2);
                if(p1&&p2&&p3&&p4&&p5) nextLayer();
            }else if(highwaySize == 5){
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(0, 0, 1);
                if (return_) return;
                boolean p3 = place(0, 0, -1);
                if (return_) return;
                boolean p4 = place(0, 0, -2);
                if (return_) return;
                boolean p5 = place(0, 0, 2);
                if (return_) return;
                boolean p6 = place(0, 1, -3);
                if (return_) return;
                boolean p7 = place(0, 1, 3);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7) nextLayer();
            }else {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(0, 0, 1);
                if (return_) return;
                boolean p3 = place(0, 0, -1);
                if (return_) return;
                boolean p4 = place(0, 0, -2);
                if (return_) return;
                boolean p5 = place(0, 0, 2);
                if (return_) return;
                boolean p6 = place(0, 0, -3);
                if (return_) return;
                boolean p7 = place(0, 0, 3);
                if (return_) return;
                boolean p8 = place(0, 1, -4);
                if (return_) return;
                boolean p9 = place(0, 1, 4);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9) nextLayer();
            }
        }else if(direction == Direction.NORTH){
            if(highwaySize == 3) {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, 0);
                if (return_) return;
                boolean p3 = place(-1, 0, 0);
                if (return_) return;
                boolean p4 = place(-2, 1, 0);
                if (return_) return;
                boolean p5 = place(2, 1, 0);
                if(p1&&p2&&p3&&p4&&p5) nextLayer();
            }else if(highwaySize == 5){
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, 0);
                if (return_) return;
                boolean p3 = place(-1, 0, 0);
                if (return_) return;
                boolean p4 = place(-2, 0, 0);
                if (return_) return;
                boolean p5 = place(2, 0, 0);
                if (return_) return;
                boolean p6 = place(-3, 1, 0);
                if (return_) return;
                boolean p7 = place(3, 1, 0);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7) nextLayer();
            }else {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, 0);
                if (return_) return;
                boolean p3 = place(-1, 0, 0);
                if (return_) return;
                boolean p4 = place(-2, 0, 0);
                if (return_) return;
                boolean p5 = place(2, 0, 0);
                if (return_) return;
                boolean p6 = place(-3, 0, 0);
                if (return_) return;
                boolean p7 = place(3, 0, 0);
                if (return_) return;
                boolean p8 = place(-4, 1, 0);
                if (return_) return;
                boolean p9 = place(4, 1, 0);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9) nextLayer();
            }
        }else if(direction == Direction.EAST){
            if(highwaySize == 3) {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(0, 0, 1);
                if (return_) return;
                boolean p3 = place(0, 0, -1);
                if (return_) return;
                boolean p4 = place(0, 1, -2);
                if (return_) return;
                boolean p5 = place(0, 1, 2);
                if(p1&&p2&&p3&&p4&&p5) nextLayer();
            }else if(highwaySize == 5){
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(0, 0, 1);
                if (return_) return;
                boolean p3 = place(0, 0, -1);
                if (return_) return;
                boolean p4 = place(0, 0, -2);
                if (return_) return;
                boolean p5 = place(0, 0, 2);
                if (return_) return;
                boolean p6 = place(0, 1, -3);
                if (return_) return;
                boolean p7 = place(0, 1, 3);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7) nextLayer();
            }else {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(0, 0, 1);
                if (return_) return;
                boolean p3 = place(0, 0, -1);
                if (return_) return;
                boolean p4 = place(0, 0, -2);
                if (return_) return;
                boolean p5 = place(0, 0, 2);
                if (return_) return;
                boolean p6 = place(0, 0, -3);
                if (return_) return;
                boolean p7 = place(0, 0, 3);
                if (return_) return;
                boolean p8 = place(0, 1, -4);
                if (return_) return;
                boolean p9 = place(0, 1, 4);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9) nextLayer();
            }
        }else if(direction == Direction.EAST_SOUTH){
            if(highwaySize == 3) {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, -1);
                if (return_) return;
                boolean p3 = place(-1, 0, 1);
                if (return_) return;
                boolean p4 = place(1, 1, -2);
                if (return_) return;
                boolean p5 = place(-2, 1, 1);
                if (return_) return;
                boolean p6 = place(1, 0, 0);
                if (return_) return;
                boolean p7 = place(0, 0, 1);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7) nextLayer();
            }else if(highwaySize == 5){
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, -1);
                if (return_) return;
                boolean p3 = place(-1, 0, 1);
                if (return_) return;
                boolean p4 = place(2, 0, -2);
                if (return_) return;
                boolean p5 = place(-2, 0, 2);
                if (return_) return;
                boolean p6 = place(2, 1, -3);
                if (return_) return;
                boolean p7 = place(-3, 1, 2);
                if (return_) return;
                boolean p8 = place(1, 0, 0);
                if (return_) return;
                boolean p9 = place(0, 0, 1);
                if (return_) return;
                boolean p10 = place(2, 0, -1);
                if (return_) return;
                boolean p11 = place(-1, 0, 2);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9&&p10&&p11) nextLayer();
            }else {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, -1);
                if (return_) return;
                boolean p3 = place(-1, 0, 1);
                if (return_) return;
                boolean p4 = place(2, 0, -2);
                if (return_) return;
                boolean p5 = place(-2, 0, 2);
                if (return_) return;
                boolean p6 = place(3, 0, -3);
                if (return_) return;
                boolean p7 = place(-3, 0, 3);
                if (return_) return;
                boolean p8 = place(3, 1, -4);
                if (return_) return;
                boolean p9 = place(-4, 1, 3);
                if (return_) return;
                boolean p10 = place(1, 0, 0);
                if (return_) return;
                boolean p11 = place(0, 0, 1);
                if (return_) return;
                boolean p12 = place(2, 0, -1);
                if (return_) return;
                boolean p13 = place(-1, 0, 2);
                if (return_) return;
                boolean p14 = place(3, 0, -2);
                if (return_) return;
                boolean p15 = place(-2, 0, 3);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9&&p10&&p11&&p12&&p13&&p14&&p15) nextLayer();
            }
        }else if(direction == Direction.SOUTH_WEST){
            if(highwaySize == 3) {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(-1, 0, -1);
                if (return_) return;
                boolean p3 = place(1, 0, 1);
                if (return_) return;
                boolean p4 = place(-1, 1, -2);
                if (return_) return;
                boolean p5 = place(2, 1, 1);
                if (return_) return;
                boolean p6 = place(-1, 0, 0);
                if (return_) return;
                boolean p7 = place(0, 0, 1);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7) nextLayer();
            }else if(highwaySize == 5){
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(-1, 0, -1);
                if (return_) return;
                boolean p3 = place(1, 0, 1);
                if (return_) return;
                boolean p4 = place(-2, 0, -2);
                if (return_) return;
                boolean p5 = place(2, 0, 2);
                if (return_) return;
                boolean p6 = place(-2, 1, -3);
                if (return_) return;
                boolean p7 = place(3, 1, 2);
                if (return_) return;
                boolean p8 = place(-1, 0, 0);
                if (return_) return;
                boolean p9 = place(0, 0, 1);
                if (return_) return;
                boolean p10 = place(-2, 0, -1);
                if (return_) return;
                boolean p11 = place(1, 0, 2);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9&&p10&&p11) nextLayer();
            }else {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(-1, 0, -1);
                if (return_) return;
                boolean p3 = place(1, 0, 1);
                if (return_) return;
                boolean p4 = place(-2, 0, -2);
                if (return_) return;
                boolean p5 = place(2, 0, 2);
                if (return_) return;
                boolean p6 = place(-3, 0, -3);
                if (return_) return;
                boolean p7 = place(3, 0, 3);
                if (return_) return;
                boolean p8 = place(-3, 1, -4);
                if (return_) return;
                boolean p9 = place(4, 1, 3);
                if (return_) return;
                boolean p10 = place(-1, 0, 0);
                if (return_) return;
                boolean p11 = place(0, 0, 1);
                if (return_) return;
                boolean p12 = place(-2, 0, -1);
                if (return_) return;
                boolean p13 = place(1, 0, 2);
                if (return_) return;
                boolean p14 = place(-3, 0, -2);
                if (return_) return;
                boolean p15 = place(2, 0, 3);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9&&p10&&p11&&p12&&p13&&p14&&p15) nextLayer();
            }
        }else if(direction == Direction.WEST_NORTH){
            if(highwaySize == 3) {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(-1, 0, 1);
                if (return_) return;
                boolean p3 = place(1, 0, -1);
                if (return_) return;
                boolean p4 = place(-1, 1, 2);
                if (return_) return;
                boolean p5 = place(2, 1, -1);
                if (return_) return;
                boolean p6 = place(-1, 0, 0);
                if (return_) return;
                boolean p7 = place(0, 0, -1);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7) nextLayer();
            }else if(highwaySize == 5){
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(-1, 0, 1);
                if (return_) return;
                boolean p3 = place(1, 0, -1);
                if (return_) return;
                boolean p4 = place(-2, 0, 2);
                if (return_) return;
                boolean p5 = place(2, 0, -2);
                if (return_) return;
                boolean p6 = place(-2, 1, 3);
                if (return_) return;
                boolean p7 = place(3, 1, -2);
                if (return_) return;
                boolean p8 = place(-1, 0, 0);
                if (return_) return;
                boolean p9 = place(0, 0, -1);
                if (return_) return;
                boolean p10 = place(-2, 0, 1);
                if (return_) return;
                boolean p11 = place(1, 0, -2);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9&&p10&&p11) nextLayer();
            }else {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(-1, 0, 1);
                if (return_) return;
                boolean p3 = place(1, 0, -1);
                if (return_) return;
                boolean p4 = place(-2, 0, 2);
                if (return_) return;
                boolean p5 = place(2, 0, -2);
                if (return_) return;
                boolean p6 = place(-3, 0, 3);
                if (return_) return;
                boolean p7 = place(3, 0, -3);
                if (return_) return;
                boolean p8 = place(-3, 1, 4);
                if (return_) return;
                boolean p9 = place(4, 1, -3);
                if (return_) return;
                boolean p10 = place(-1, 0, 0);
                if (return_) return;
                boolean p11 = place(0, 0, -1);
                if (return_) return;
                boolean p12 = place(-2, 0, 1);
                if (return_) return;
                boolean p13 = place(1, 0, -2);
                if (return_) return;
                boolean p14 = place(-3, 0, 2);
                if (return_) return;
                boolean p15 = place(2, 0, -3);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9&&p10&&p11&&p12&&p13&&p14&&p15) nextLayer();
            }
        }else if(direction == Direction.NORTH_EAST){
            if(highwaySize == 3) {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, 1);
                if (return_) return;
                boolean p3 = place(-1, 0, -1);
                if (return_) return;
                boolean p4 = place(1, 1, 2);
                if (return_) return;
                boolean p5 = place(-2, 1, -1);
                if (return_) return;
                boolean p6 = place(1, 0, 0);
                if (return_) return;
                boolean p7 = place(0, 0, -1);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7) nextLayer();
            }else if(highwaySize == 5){
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, 1);
                if (return_) return;
                boolean p3 = place(-1, 0, -1);
                if (return_) return;
                boolean p4 = place(2, 0, 2);
                if (return_) return;
                boolean p5 = place(-2, 0, -2);
                if (return_) return;
                boolean p6 = place(2, 1, 3);
                if (return_) return;
                boolean p7 = place(-3, 1, -2);
                if (return_) return;
                boolean p8 = place(1, 0, 0);
                if (return_) return;
                boolean p9 = place(0, 0, -1);
                if (return_) return;
                boolean p10 = place(2, 0, 1);
                if (return_) return;
                boolean p11 = place(-1, 0, -2);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9&&p10&&p11) nextLayer();
            }else {
                boolean p1 = place(0, 0, 0);
                if (return_) return;
                boolean p2 = place(1, 0, 1);
                if (return_) return;
                boolean p3 = place(-1, 0, -1);
                if (return_) return;
                boolean p4 = place(2, 0, 2);
                if (return_) return;
                boolean p5 = place(-2, 0, -2);
                if (return_) return;
                boolean p6 = place(3, 0, 3);
                if (return_) return;
                boolean p7 = place(-3, 0, -3);
                if (return_) return;
                boolean p8 = place(3, 1, 4);
                if (return_) return;
                boolean p9 = place(-4, 1, -3);
                if (return_) return;
                boolean p10 = place(1, 0, 0);
                if (return_) return;
                boolean p11 = place(0, 0, -1);
                if (return_) return;
                boolean p12 = place(2, 0, 1);
                if (return_) return;
                boolean p13 = place(-1, 0, -2);
                if (return_) return;
                boolean p14 = place(3, 0, 2);
                if (return_) return;
                boolean p15 = place(-2, 0, -3);
                if(p1&&p2&&p3&&p4&&p5&&p6&&p7&&p8&&p9&&p10&&p11&&p12&&p13&&p14&&p15) nextLayer();
            }
        }
    }

    private int getDistance(PlayerEntity player){
        return (int) Math.round(player.squaredDistanceTo(blockPos.getX(), blockPos.getY()-player.getStandingEyeHeight(), blockPos.getZ()));
    }

    private boolean place(int x, int y, int z) {
        BlockPos placePos = setBlockPos(x, y, z);
        BlockState blockState = mc.world.getBlockState(placePos);

        if (!blockState.getMaterial().isReplaceable()) return true;

        int slot = findSlot();
        if (BlockUtils.place(placePos, Hand.MAIN_HAND, slot, rotate.get(), 10, true)) {
            return_ = true;
        }

        return false;
    }

    private int getSize(){
        if (size.get() % 2 == 0) return size.get()-1;
        else return size.get();
    }

    private void unpress() {
        setPressed(mc.options.keyForward, false);
        setPressed(mc.options.keyBack, false);
        setPressed(mc.options.keyLeft, false);
        setPressed(mc.options.keyRight, false);
    }

    private void setPressed(KeyBinding key, boolean pressed) {
        key.setPressed(pressed);
        Input.setKeyState(key, pressed);
    }

    private void nextLayer(){
        if(direction == Direction.SOUTH) changeBlockPos(0,0,1);
        else if(direction == Direction.WEST) changeBlockPos(-1,0,0);
        else if(direction == Direction.NORTH) changeBlockPos(0,0,-1);
        else if(direction == Direction.EAST) changeBlockPos(1,0,0);
        else if(direction == Direction.EAST_SOUTH) changeBlockPos(1,0,1);
        else if(direction == Direction.SOUTH_WEST) changeBlockPos(-1,0,1);
        else if(direction == Direction.WEST_NORTH) changeBlockPos(-1,0,-1);
        else if(direction == Direction.NORTH_EAST) changeBlockPos(1,0,-1);
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

            if (item == Items.OBSIDIAN) {
                return i;
            }
        }

        return -1;
    }



}