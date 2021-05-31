package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.entity.CrystalUtils;
import minegame159.meteorclient.utils.entity.EntityUtils;
import minegame159.meteorclient.utils.entity.SortPriority;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PistonAura extends Module {
    public PistonAura(){
        super(Categories.ExtraLint,"PistonAura", "PistonAura");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> targetRange = sgGeneral.add(new DoubleSetting.Builder()
            .name("target-range")
            .description("The radius in which players get targeted.")
            .defaultValue(4)
            .min(0)
            .sliderMax(5)
            .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("place-delay")
            .description("How many ticks between block placements.")
            .defaultValue(1)
            .sliderMin(0)
            .sliderMax(30)
            .build()
    );
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Automatically rotates you towards the city block.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> selfToggle = sgGeneral.add(new BoolSetting.Builder()
            .name("self-toggle")
            .description("Automatically toggles off after activation.")
            .defaultValue(true)
            .build()
    );

    private enum Direction2 {
        SOUTH,
        SOUTH_WEST,
        WEST,
        WEST_NORTH,
        NORTH,
        NORTH_EAST,
        EAST,
        EAST_SOUTH
    }

    private PlayerEntity player = mc.player;
    private PlayerEntity target;
    private BlockPos blockPosTarget;
    private Direction2 direction2;
    BlockPos piston;
    BlockPos redstoneBlock;
    private boolean return_;
    private EndCrystalEntity crystalentity;
    private int timer;

    @Override
    public void onActivate() {
        direction2 = getDirection(mc.player);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (EntityUtils.isBadTarget(target, targetRange.get())) {
            PlayerEntity search = EntityUtils.getPlayerTarget(targetRange.get(), SortPriority.LowestDistance, false);
            target = search;
        }

        if (EntityUtils.isBadTarget(target, targetRange.get())) {
            target = null;
            blockPosTarget = null;
            if (selfToggle.get()) toggle();
            return;
        }
        return_ = false;

        BlockPos targetBlocks = target.getBlockPos().add(0, 1, 0);

        if (direction2 == Direction2.WEST && mc.world.getBlockState(targetBlocks.west(1)).getBlock().is(Blocks.AIR) && mc.world.getBlockState(targetBlocks.west(2)).getBlock().is(Blocks.AIR) && mc.world.getBlockState(targetBlocks.west(3)).getBlock().is(Blocks.AIR)) {
            piston = targetBlocks.west(2);
            redstoneBlock = targetBlocks.west(3);
            BlockUtils.place(piston, Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.PISTON), rotate.get(), 100, true);

            Hand crystalHand = InvUtils.getHand(Items.END_CRYSTAL);
            int crystalSlot = InvUtils.findItemInHotbar(Items.END_CRYSTAL);
            int prevSlot = mc.player.inventory.selectedSlot;

            if (crystalHand != Hand.OFF_HAND && crystalSlot != -1) mc.player.inventory.selectedSlot = crystalSlot;
            CrystalUtils.placeBlock(new Vec3d(targetBlocks.west(1).getX(), targetBlocks.west(1).getY(), targetBlocks.west(1).getZ()), Hand.MAIN_HAND, Direction.WEST);
            crystalentity = new EndCrystalEntity(mc.world, targetBlocks.west(1).getX(), targetBlocks.west(1).getY(), targetBlocks.west(1).getZ());
            mc.interactionManager.attackEntity(mc.player, crystalentity);
            if (crystalHand != Hand.OFF_HAND) mc.player.inventory.selectedSlot = prevSlot;

            if (timer >= delay.get()) {
                BlockUtils.place(redstoneBlock, Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.REDSTONE_BLOCK), rotate.get(), 100, true);
                timer = 0;
            }else {
                timer++;
            }


        }
        if (direction2 == Direction2.EAST && mc.world.getBlockState(targetBlocks.east(1)).getBlock().is(Blocks.AIR) && mc.world.getBlockState(targetBlocks.east(2)).getBlock().is(Blocks.AIR) && mc.world.getBlockState(targetBlocks.east(3)).getBlock().is(Blocks.AIR)) {
            piston = targetBlocks.east(2);
            redstoneBlock = targetBlocks.east(3);
            BlockUtils.place(piston, Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.PISTON), rotate.get(), 100, true);
            Hand crystalHand = InvUtils.getHand(Items.END_CRYSTAL);
            int crystalSlot = InvUtils.findItemInHotbar(Items.END_CRYSTAL);
            int prevSlot = mc.player.inventory.selectedSlot;

            if (crystalHand != Hand.OFF_HAND && crystalSlot != -1) mc.player.inventory.selectedSlot = crystalSlot;
            CrystalUtils.placeBlock(new Vec3d(targetBlocks.east(1).getX(), targetBlocks.east(1).getY(), targetBlocks.east(1).getZ()), Hand.MAIN_HAND, Direction.EAST);
            crystalentity = new EndCrystalEntity(mc.world, targetBlocks.east(1).getX(), targetBlocks.east(1).getY(), targetBlocks.east(1).getZ());
            mc.interactionManager.attackEntity(mc.player, crystalentity);
            if (crystalHand != Hand.OFF_HAND) mc.player.inventory.selectedSlot = prevSlot;

            if (timer >= delay.get()) {
                BlockUtils.place(redstoneBlock, Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.REDSTONE_BLOCK), rotate.get(), 100, true);
                timer = 0;
            }else {
                timer++;
            }

        }
        if (direction2 == Direction2.SOUTH && mc.world.getBlockState(targetBlocks.south(1)).getBlock().is(Blocks.AIR) && mc.world.getBlockState(targetBlocks.south(2)).getBlock().is(Blocks.AIR) && mc.world.getBlockState(targetBlocks.south(3)).getBlock().is(Blocks.AIR)) {
            piston = targetBlocks.south(2);
            redstoneBlock = targetBlocks.south(3);
            BlockUtils.place(piston, Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.PISTON), rotate.get(), 100, true);
            Hand crystalHand = InvUtils.getHand(Items.END_CRYSTAL);
            int crystalSlot = InvUtils.findItemInHotbar(Items.END_CRYSTAL);
            int prevSlot = mc.player.inventory.selectedSlot;

            if (crystalHand != Hand.OFF_HAND && crystalSlot != -1) mc.player.inventory.selectedSlot = crystalSlot;
            CrystalUtils.placeBlock(new Vec3d(targetBlocks.south(1).getX(), targetBlocks.south(1).getY(), targetBlocks.south(1).getZ()), Hand.MAIN_HAND, Direction.SOUTH);
            crystalentity = new EndCrystalEntity(mc.world, targetBlocks.south(1).getX(), targetBlocks.south(1).getY(), targetBlocks.south(1).getZ());
            mc.interactionManager.attackEntity(mc.player, crystalentity);
            if (crystalHand != Hand.OFF_HAND) mc.player.inventory.selectedSlot = prevSlot;

            if (timer >= delay.get()) {
                BlockUtils.place(redstoneBlock, Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.REDSTONE_BLOCK), rotate.get(), 100, true);
                timer = 0;
            }else {
                timer++;
            }

        }
        if (direction2 == Direction2.NORTH && mc.world.getBlockState(targetBlocks.north(1)).getBlock().is(Blocks.AIR) && mc.world.getBlockState(targetBlocks.north(2)).getBlock().is(Blocks.AIR) && mc.world.getBlockState(targetBlocks.north(3)).getBlock().is(Blocks.AIR)) {
            piston = targetBlocks.north(2);
            redstoneBlock = targetBlocks.north(3);
            BlockUtils.place(piston, Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.PISTON), rotate.get(), 100, true);
            Hand crystalHand = InvUtils.getHand(Items.END_CRYSTAL);
            int crystalSlot = InvUtils.findItemInHotbar(Items.END_CRYSTAL);
            int prevSlot = mc.player.inventory.selectedSlot;

            if (crystalHand != Hand.OFF_HAND && crystalSlot != -1) mc.player.inventory.selectedSlot = crystalSlot;
            CrystalUtils.placeBlock(new Vec3d(targetBlocks.north(1).getX(), targetBlocks.north(1).getY(), targetBlocks.north(1).getZ()), Hand.MAIN_HAND, Direction.NORTH);
            crystalentity = new EndCrystalEntity(mc.world, targetBlocks.north(1).getX(), targetBlocks.north(1).getY(), targetBlocks.north(1).getZ());
            mc.interactionManager.attackEntity(mc.player, crystalentity);
            if (crystalHand != Hand.OFF_HAND) mc.player.inventory.selectedSlot = prevSlot;

            if (timer >= delay.get()) {
                BlockUtils.place(redstoneBlock, Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.REDSTONE_BLOCK), rotate.get(), 100, true);
                timer = 0;
            }else {
                timer++;
            }

        }


    }


    private Direction2 getDirection(PlayerEntity player){
        double yaw = player.yaw;
        if(yaw==0) return Direction2.SOUTH;
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

        if(yaw >= 157.5 || yaw < -157.5) return Direction2.NORTH;
        if(yaw >= -157.5 && yaw < -112.5) return Direction2.NORTH_EAST;
        if(yaw >= -112.5 && yaw < -67.5) return Direction2.EAST;
        if(yaw >= -67.5 && yaw < -22.5) return Direction2.EAST_SOUTH;
        if((yaw >= -22.5 && yaw <=0) || (yaw > 0 && yaw < 22.5)) return Direction2.SOUTH;
        if(yaw >= 22.5 && yaw < 67.5) return Direction2.SOUTH_WEST;
        if(yaw >= 67.5 && yaw < 112.5) return Direction2.WEST;
        if(yaw >= 112.5 && yaw < 157.5) return Direction2.WEST_NORTH;
        return Direction2.SOUTH;
    }

}

