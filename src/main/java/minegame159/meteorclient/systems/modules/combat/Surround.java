/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.systems.modules.combat;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.player.PlayerUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Surround extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> doubleHeightSurround = sgGeneral.add(new BoolSetting.Builder()
            .name("double-height")
            .description("Places obsidian on top of the original surround blocks to prevent people from face-placing you.")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> longSurround = sgGeneral.add(new BoolSetting.Builder()
            .name("long-surround")
            .description("Places obsidian on top of the original surround blocks to prevent people from face-placing you.")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> fullSurround = sgGeneral.add(new BoolSetting.Builder()
            .name("full-surround")
            .description("Places obsidian on top of the original surround blocks to prevent people from face-placing you.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> moreObby = sgGeneral.add(new BoolSetting.Builder()
            .name("more-obby")
            .description("Places more obsidian near original surround blocks to prevent people from breaking your surround.")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> swastikaSurround = sgGeneral.add(new BoolSetting.Builder()
            .name("swastika-surround")
            .description("Places obsidian on top of the original surround blocks to prevent people from face-placing you.")
            .defaultValue(false)
            .build()
    );
    
    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder()
            .name("only-on-ground")
            .description("Works only when you standing on blocks.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> onlyWhenSneaking = sgGeneral.add(new BoolSetting.Builder()
            .name("only-when-sneaking")
            .description("Places blocks only after sneaking.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> turnOff = sgGeneral.add(new BoolSetting.Builder()
            .name("turn-off")
            .description("Toggles off when all blocks are placed.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
            .name("center")
            .description("Teleports you to the center of the block.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> disableOnJump = sgGeneral.add(new BoolSetting.Builder()
            .name("disable-on-jump")
            .description("Automatically disables when you jump.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> disableOnYChange = sgGeneral.add(new BoolSetting.Builder()
            .name("disable-on-y-change")
            .description("Automatically disables when your y level (step, jumping, atc).")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Automatically faces towards the obsidian being placed.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> useEnderChests = sgGeneral.add(new BoolSetting.Builder()
            .name("use-ender-chests")
            .description("Will surround with ender chests if they are found in your hotbar.")
            .defaultValue(false)
            .build()
    );

    // TODO: Make a render for Surround monkeys.
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private boolean return_;

    public Surround() {
        super(Categories.Combat, "surround", "Surrounds you in blocks to prevent you from taking lots of damage.");
    }

    @Override
    public void onActivate() {
        if (center.get()) PlayerUtils.centerPlayer();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if ((disableOnJump.get() && (mc.options.keyJump.isPressed() || mc.player.input.jumping)) || (disableOnYChange.get() && mc.player.prevY < mc.player.getY())) {
            toggle();
            return;
        }

        if (onlyOnGround.get() && !mc.player.isOnGround()) return;
        if (onlyWhenSneaking.get() && !mc.options.keySneak.isPressed()) return;

        // Place
        return_ = false;

        // Bottom
        boolean p1 = place(0, -1, 0);
        if (return_) return;

        // Sides
        boolean p2 = place(1, 0, 0);
        if (return_) return;
        boolean p3 = place(-1, 0, 0);
        if (return_) return;
        boolean p4 = place(0, 0, 1);
        if (return_) return;
        boolean p5 = place(0, 0, -1);
        if (return_) return;

        // Sides up
        boolean doubleHeightSurroundPlaced = false;
        if (doubleHeightSurround.get()) {
            boolean p6 = place(1, 1, 0);
            if (return_) return;
            boolean p7 = place(-1, 1, 0);
            if (return_) return;
            boolean p8 = place(0, 1, 1);
            if (return_) return;
            boolean p9 = place(0, 1, -1);
            if (return_) return;

            if (p6 && p7 && p8 && p9) doubleHeightSurroundPlaced = true;
        }

        boolean longSurroundPlaced = false;
        if (longSurround.get()){
            boolean p2_2 = place(2, 0, 0);
            if(return_) return;
            boolean p3_2 = place(-2, 0, 0);
            if(return_) return;
            boolean p4_2 = place(0, 0, 2);
            if(return_) return;
            boolean p5_2 = place(0, 0, -2);
            if(return_) return;

            if (p2_2 && p3_2 && p4_2 && p5_2) longSurroundPlaced = true;
        }

        boolean moreObbyPlaced = false;
        if (moreObby.get()){
            boolean p6 = place(1, 0, 0);
            if (return_) return;
            boolean p7 = place(-1, 0, 0);
            if (return_) return;
            boolean p8 = place(0, 0, 1);
            if (return_) return;
            boolean p9 = place(0, 0, -1);
            if (return_) return;
            boolean p10 = place(1,0,1);
            if (return_) return;
            boolean p11 = place(-1,0,-1);
            if (return_) return;
            boolean p12 = place(1,0,-1);
            if (return_) return;
            boolean p13 = place(-1,0,1);
            if (return_) return;
            boolean p14 = place(2, 0, 0);
            if(return_) return;
            boolean p15 = place(-2, 0, 0);
            if(return_) return;
            boolean p16 = place(0, 0, 2);
            if(return_) return;
            boolean p17 = place(0, 0, -2);
            if(return_) return;

            if (p6 && p7 && p8 && p9 && p10 && p11 && p12 && p13 && p14 && p15 && p16 && p17) moreObbyPlaced = true;
        }

        boolean fullSurroundPlaced = false;
        if (fullSurround.get()){
            boolean p6 = place(1, 0, 0);
            if (return_) return;
            boolean p7 = place(-1, 0, 0);
            if (return_) return;
            boolean p8 = place(0, 0, 1);
            if (return_) return;
            boolean p9 = place(0, 0, -1);
            if (return_) return;
            boolean p10 = place(1,0,1);
            if (return_) return;
            boolean p11 = place(-1,0,-1);
            if (return_) return;
            boolean p12 = place(1,0,-1);
            if (return_) return;
            boolean p13 = place(-1,0,1);
            if (return_) return;
            boolean p14 = place(2, 0, 0);
            if(return_) return;
            boolean p15 = place(-2, 0, 0);
            if(return_) return;
            boolean p16 = place(0, 0, 2);
            if(return_) return;
            boolean p17 = place(0, 0, -2);
            if(return_) return;
            boolean p18 = place(1, 1, 0);
            if (return_) return;
            boolean p19 = place(-1, 1, 0);
            if (return_) return;
            boolean p20 = place(0, 1, 1);
            if (return_) return;
            boolean p21 = place(0, 1, -1);
            if (return_) return;
            boolean p22 = place(0,2,0);
            if (return_) return;
            boolean p23 = place(0,3,0);
            if (return_) return;

            if (p6 && p7 && p8 && p9 && p10 && p11 && p12 && p13 && p14 && p15 && p16 && p17 && p18 && p19 && p20 && p21 && p22 && p23) fullSurroundPlaced = true;
        }
        boolean swastikaSurroundPlaced = false;
        if (swastikaSurround.get()){
            boolean p6 = place(2, 0, 0);
            if (return_) return;
            boolean p7 = place(-2, 0, 0);
            if (return_) return;
            boolean p8 = place(0, 0, 2);
            if (return_) return;
            boolean p9 = place(0, 0, -2);
            if (return_) return;
            boolean p10 = place(2, 0, 1);
            if (return_) return;
            boolean p11 = place(-2, 0, -1);
            if (return_) return;
            boolean p12 = place(-1, 0, 2);
            if (return_) return;
            boolean p13 = place(1, 0, -2);
            if (return_) return;
            if (p6 && p7 && p8 && p9 && p10 && p11 && p12 && p13) swastikaSurroundPlaced = true;
        }
        //mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), Direction.DOWN, blockPos, false)));
        // Auto turn off
        if (turnOff.get() && p1 && p2 && p3 && p4 && p5) {
            if (doubleHeightSurroundPlaced || !doubleHeightSurround.get()) toggle();
        }
        if (turnOff.get() && p1 && p2 && p3 && p4 && p5) {
            if (longSurroundPlaced || !longSurround.get()) toggle();
        }
        if (turnOff.get() && p1 && p2 && p3 && p4 && p5) {
            if (moreObbyPlaced || !moreObby.get()) toggle();
        }
        if (turnOff.get() && p1 && p2 && p3 && p4 && p5) {
            if (fullSurroundPlaced || !fullSurround.get()) toggle();
        }
        if (turnOff.get() && p1 && p2 && p3 && p4 && p5) {
            if (swastikaSurroundPlaced || !swastikaSurround.get()) toggle();
        }
    }

    private boolean place(int x, int y, int z) {
        setBlockPos(x, y, z);
        BlockState blockState = mc.world.getBlockState(blockPos);

        if (!blockState.getMaterial().isReplaceable()) return true;

        int slot = findSlot();
        if (BlockUtils.place(blockPos, Hand.MAIN_HAND, slot, rotate.get(), 100, true)) {
            return_ = true;
        }

        return false;
    }

    private void setBlockPos(int x, int y, int z) {
        blockPos.set(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);
    }

    private int findSlot() {
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStack(i).getItem();

            if (!(item instanceof BlockItem)) continue;

            if (item == Items.OBSIDIAN || (item == Items.ENDER_CHEST && useEnderChests.get())) {
                return i;
            }
        }

        return -1;
    }
}
