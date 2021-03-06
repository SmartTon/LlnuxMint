/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.render.RenderEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.rendering.Renderer;
import minegame159.meteorclient.rendering.ShapeMode;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.entity.EntityUtils;
import minegame159.meteorclient.utils.entity.SortPriority;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.render.color.SettingColor;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.AirBlock;

import java.util.ArrayList;
import java.util.List;

public class tntTRAP extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("target-range")
            .description("The range players can be targeted.")
            .defaultValue(4)
            .build()
    );

    private final Setting<SortPriority> priority = sgGeneral.add(new EnumSetting.Builder<SortPriority>()
            .name("target-priority")
            .description("How to select the player to target.")
            .defaultValue(SortPriority.LowestHealth)
            .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("place-delay")
            .description("How many ticks between block placements.")
            .defaultValue(1)
            .build()
    );


    private final Setting<BottomMode> bottomPlacement = sgGeneral.add(new EnumSetting.Builder<BottomMode>()
            .name("mode")
            .name("mode")
            .defaultValue(BottomMode.Full)
            .build()
    );



    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Rotates towards blocks when placing.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> lever = sgGeneral.add(new BoolSetting.Builder()
            .name("Lever")
            .description("places and activates lever to activate tnt")
            .defaultValue(true)
            .build()
    );

    // Render

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("Renders an overlay where blocks will be placed.")
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
            .description("The side color of the target block rendering.")
            .defaultValue(new SettingColor(197, 137, 232, 10))
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color of the target block rendering.")
            .defaultValue(new SettingColor(197, 137, 232))
            .build()
    );

    private final Setting<SettingColor> nextSideColor = sgRender.add(new ColorSetting.Builder()
            .name("next-side-color")
            .description("The side color of the next block to be placed.")
            .defaultValue(new SettingColor(227, 196, 245, 10))
            .build()
    );

    private final Setting<SettingColor> nextLineColor = sgRender.add(new ColorSetting.Builder()
            .name("next-line-color")
            .description("The line color of the next block to be placed.")
            .defaultValue(new SettingColor(227, 196, 245))
            .build()
    );

    private PlayerEntity target;
    private final List<BlockPos> placePositions = new ArrayList<>();
    private boolean placed;
    private int timer;

    public tntTRAP() {
        super(Categories.ExtraLint, "TNT-trap", "Traps people so tnt aura can work");
    }

    @Override
    public void onActivate() {
        target = null;
        placePositions.clear();
        timer = 0;
        placed = false;
    }

    @Override
    public void onDeactivate() {
        placePositions.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (5 + 5 == 201 && placed && placePositions.isEmpty()) {
            placed = false;
            toggle();
            return;
        }

        if (InvUtils.findItemInHotbar(Items.OBSIDIAN) == -1) {
            placePositions.clear();
            placed = false;
            return;
        }

        if (EntityUtils.isBadTarget(target, range.get()))
            target = EntityUtils.getPlayerTarget(range.get(), priority.get(), false);
        if (EntityUtils.isBadTarget(target, range.get())) return;

        fillPlaceArray(target);

        if (timer >= delay.get() && placePositions.size() > 0) {
            BlockPos blockPos = placePositions.get(placePositions.size() - 1);

            if (BlockUtils.place(blockPos, Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.OBSIDIAN), rotate.get(), 1, true)) {

                placePositions.remove(blockPos);
                placed = true;
            }

            timer = 0;
        } else {
            timer++;
        }
    }

    @EventHandler
    private void onRender(RenderEvent event) {
        if (!render.get() || placePositions.isEmpty()) return;

        for (BlockPos pos : placePositions) {
            if (pos.equals(placePositions.get(placePositions.size() - 1))) {
                Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, pos, nextSideColor.get(), nextLineColor.get(), shapeMode.get(), 0);
            } else {
                Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
            }
        }
    }

    private void fillPlaceArray(PlayerEntity target) {
        placePositions.clear();
        BlockPos targetPos = target.getBlockPos();


        switch (bottomPlacement.get()) {


            case Full:

                add(targetPos.add(1, 2, 0));
                add(targetPos.add(-1, 2, 0));
                add(targetPos.add(0, 2, -1));
                add(targetPos.add(0, 2, 1));
                add(targetPos.add(0, 3, 0));
                add(targetPos.add(0, 1, 1));
                add(targetPos.add(1, 1, 0));
                add(targetPos.add(0, 1, -1));
                add(targetPos.add(-1, 1, 0));

                int slot = getLeverSlot();
                InvUtils.findItemInHotbar(Blocks.LEVER.asItem());
                BlockPos blockPos = target.getBlockPos().up().add(0, 3, 0);
                BlockUtils.place(blockPos, Hand.MAIN_HAND, slot, rotate.get(), 1, false);
                InvUtils.findItemInHotbar(Blocks.AIR.asItem());
                BlockPos blockPos2 = target.getBlockPos().up().add(0, 4, 0);
                BlockUtils.place(blockPos2, Hand.MAIN_HAND, slot, rotate.get(), 1, false);
                slot = getTntSlot();
                InvUtils.findItemInHotbar(Blocks.TNT.asItem());
                BlockPos blockPos3 = target.getBlockPos().up().add(0, 1, 0);
                BlockUtils.place(blockPos3, Hand.MAIN_HAND, slot, rotate.get(), 1, false);



        }
    }





    private int getLeverSlot() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStack(i).getItem();
            Block block = Block.getBlockFromItem(item);

            if (block instanceof LeverBlock) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    private int getTntSlot() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStack(i).getItem();
            Block block = Block.getBlockFromItem(item);

            if (block instanceof TntBlock) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    private int getNothingSlot() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStack(i).getItem();
            Block block = Block.getBlockFromItem(item);

            if (block instanceof AirBlock) {
                slot = i;
                break;
            }
        }
        return slot;
    }


    private void add(BlockPos blockPos) {
        if (!placePositions.contains(blockPos) && BlockUtils.canPlace(blockPos)) placePositions.add(blockPos);
    }


    @Override
    public String getInfoString() {
        if (target != null) return target.getEntityName();
        return null;
    }


    public enum BottomMode {
        Full
    }
}
