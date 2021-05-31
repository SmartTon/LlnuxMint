/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.player.PlayerUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class LintSurround extends Module {

    public enum eCenter {
        Fast,
        Legit,
        Disable
    }

    public enum surroundMode{
        Simple,
        DoubleHeight,
        AntiSurroundBreaker,
        MoreObsidian,
        DoubleHeightMoreObsidian,
        FullSurround,
        FullSurroundWithAntiCevBreaker,
        SwastikaSurround
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<eCenter> eCenterSetting = sgGeneral.add(new EnumSetting.Builder<eCenter>()
            .name("Center")
            .description(".")
            .defaultValue(eCenter.Legit)
            .build()
    );

    private final Setting<surroundMode> surroundModeSetting = sgGeneral.add(new EnumSetting.Builder<surroundMode>()
            .name("Surround-Mode")
            .description(".")
            .defaultValue(surroundMode.Simple)
            .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("place-delay")
            .description("How many ticks between block placements.")
            .defaultValue(1)
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
            .description(".")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> useNetheriteBlocks = sgGeneral.add(new BoolSetting.Builder()
            .name("use-netherite-blocks")
            .description(".")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> useAncientDebris = sgGeneral.add(new BoolSetting.Builder()
            .name("use-ancient-debris")
            .description(".")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> useRespawnAnchor = sgGeneral.add(new BoolSetting.Builder()
            .name("use-respawn-anchor")
            .description(".")
            .defaultValue(false)
            .build()
    );

    // TODO: Make a render for Surround monkeys.
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private boolean return_;

    public LintSurround() {
        super(Categories.ExtraLint, "LintSurround", "Surrounds you in blocks to prevent you from taking lots of damage.");
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

        boolean p1 = place(0, -1, 0);
        if (return_) return;

        if (surroundModeSetting.get() == surroundMode.Simple){
            // Sides
            boolean p2 = place(1, 0, 0);
            if (return_) return;
            boolean p3 = place(-1, 0, 0);
            if (return_) return;
            boolean p4 = place(0, 0, 1);
            if (return_) return;
            boolean p5 = place(0, 0, -1);
            if (return_) return;
        }
        // Bottom
        else if (surroundModeSetting.get() == surroundMode.DoubleHeight){
            boolean p2 = place(1, 0, 0);
            if (return_) return;
            boolean p3 = place(-1, 0, 0);
            if (return_) return;
            boolean p4 = place(0, 0, 1);
            if (return_) return;
            boolean p5 = place(0, 0, -1);
            if (return_) return;
            boolean p6 = place(1, 1, 0);
            if (return_) return;
            boolean p7 = place(-1, 1, 0);
            if (return_) return;
            boolean p8 = place(0, 1, 1);
            if (return_) return;
            boolean p9 = place(0, 1, -1);
            if (return_) return;

        }

        else if (surroundModeSetting.get() == surroundMode.AntiSurroundBreaker){
            boolean p2 = place(1, 0, 0);
            if (return_) return;
            boolean p3 = place(-1, 0, 0);
            if (return_) return;
            boolean p4 = place(0, 0, 1);
            if (return_) return;
            boolean p5 = place(0, 0, -1);
            if (return_) return;
            boolean p6 = place(2, 0, 0);
            if(return_) return;
            boolean p7 = place(-2, 0, 0);
            if(return_) return;
            boolean p8 = place(0, 0, 2);
            if(return_) return;
            boolean p9 = place(0, 0, -2);
            if(return_) return;

        }

        else if (surroundModeSetting.get() == surroundMode.MoreObsidian){
            boolean p2 = place(1, 0, 0);
            if (return_) return;
            boolean p3 = place(-1, 0, 0);
            if (return_) return;
            boolean p4 = place(0, 0, 1);
            if (return_) return;
            boolean p5 = place(0, 0, -1);
            if (return_) return;
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

        }

        else if (surroundModeSetting.get() == surroundMode.DoubleHeightMoreObsidian){
            boolean p2 = place(1, 0, 0);
            if (return_) return;
            boolean p3 = place(-1, 0, 0);
            if (return_) return;
            boolean p4 = place(0, 0, 1);
            if (return_) return;
            boolean p5 = place(0, 0, -1);
            if (return_) return;
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


        }

        else if (surroundModeSetting.get() == surroundMode.FullSurround){
            boolean p2 = place(1, 0, 0);
            if (return_) return;
            boolean p3 = place(-1, 0, 0);
            if (return_) return;
            boolean p4 = place(0, 0, 1);
            if (return_) return;
            boolean p5 = place(0, 0, -1);
            if (return_) return;
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

        }

        else if (surroundModeSetting.get() == surroundMode.FullSurroundWithAntiCevBreaker){
            boolean p2 = place(1, 0, 0);
            if (return_) return;
            boolean p3 = place(-1, 0, 0);
            if (return_) return;
            boolean p4 = place(0, 0, 1);
            if (return_) return;
            boolean p5 = place(0, 0, -1);
            if (return_) return;
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

        }
        else if (surroundModeSetting.get() == surroundMode.SwastikaSurround){
            boolean p2 = place(1, 0, 0);
            if (return_) return;
            boolean p3 = place(-1, 0, 0);
            if (return_) return;
            boolean p4 = place(0, 0, 1);
            if (return_) return;
            boolean p5 = place(0, 0, -1);
            if (return_) return;
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

        }


        //mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), Direction.DOWN, blockPos, false)));
        // Auto turn off

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

            if (item == Items.OBSIDIAN || (item == Items.ENDER_CHEST && useEnderChests.get()) || (item == Items.RESPAWN_ANCHOR && useRespawnAnchor.get()) || (item == Items.NETHERITE_BLOCK && useNetheriteBlocks.get()) || item == Items.ANCIENT_DEBRIS && useAncientDebris.get()) {
                return i;
            }
        }

        return -1;
    }
}
