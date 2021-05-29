/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.systems.modules.extra;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.render.RenderEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.rendering.Renderer;
import minegame159.meteorclient.rendering.ShapeMode;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.systems.modules.combat.AutoTrap;
import minegame159.meteorclient.utils.entity.EntityUtils;
import minegame159.meteorclient.utils.entity.SortPriority;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.render.color.SettingColor;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

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

    private final Setting<tntPlacement> tntPToplacement = sgGeneral.add(new EnumSetting.Builder<tntPlacement>()
            .name("top-blocks")
            .description("Which blocks to place on the top half of the target.")
            .defaultValue(tntPlacement.tnt)
            .build()
    );


    private final Setting<tntActivate> tnt_activateType = sgGeneral.add(new EnumSetting.Builder<tntActivate>()
            .name("tnt activate")
            .description("How to activate tnt.")
            .defaultValue(tntActivate.FlintAdnSteel)
            .build()
    );

    private final Setting<Boolean> antiBreak = sgGeneral.add(new BoolSetting.Builder()
            .name("anti-break")
            .description("Prevents flint and steel from being broken.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Rotates towards blocks when placing.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> selfToggle = sgGeneral.add(new BoolSetting.Builder()
            .name("self-toggle")
            .description("Turns off after placing all blocks.")
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
    private int preSlot;

    public tntTRAP() {
        super(Categories.Mint, "TNT-trap", "Traps people so tnt aura can work");
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
        if (selfToggle.get() && placed && placePositions.isEmpty()) {
            placed = false;
            toggle();
            return;
        }

        if (InvUtils.findItemInHotbar(Items.OBSIDIAN) == -1) {
            placePositions.clear();
            placed = false;
            return;
        }

        if (EntityUtils.isBadTarget(target, range.get())) target = EntityUtils.getPlayerTarget(range.get(), priority.get(), false);
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

        int slot3 = getTntSlot();
        InvUtils.findItemInHotbar(Blocks.TNT.asItem());
        BlockPos blockPos3 = target.getBlockPos().up().add(0, 2, 0);
        BlockUtils.place(blockPos3, Hand.MAIN_HAND, slot3, rotate.get(), -1, false);



        switch (tnt_activateType.get()){
            case LEVER:
                int slot = getLeverSlot();
                InvUtils.findItemInHotbar(Blocks.LEVER.asItem());
                BlockPos blockPos = target.getBlockPos().up().add(0, 3, 0);
                BlockUtils.place(blockPos, Hand.MAIN_HAND, slot, rotate.get(), -1, false);
                InvUtils.findItemInHotbar(Blocks.AIR.asItem());
                BlockPos blockPos2 = target.getBlockPos().up().add(0, 4, 0);
                BlockUtils.place(blockPos2, Hand.MAIN_HAND, slot, rotate.get(), -2, false);

                break;
            case FlintAdnSteel:
                int fire = InvUtils.findItemInHotbar(Items.FLINT_AND_STEEL);
                BlockPos pos = target.getBlockPos().add(0,2,2);
                int preSlot = mc.player.inventory.selectedSlot;
                mc.player.inventory.selectedSlot = fire;
                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), Direction.UP, pos, true));
                mc.player.inventory.selectedSlot = preSlot;

                break;
        }

        switch (tntPToplacement.get()) {


            case tnt:

                add(targetPos.add(1, 2, 0));
                add(targetPos.add(-1, 2, 0));
                add(targetPos.add(0, 2, -1));
                add(targetPos.add(0, 2, 1));
                add(targetPos.add(0, 3, 0));
                add(targetPos.add(0, 1, 1));
                add(targetPos.add(1, 1, 0));
                add(targetPos.add(0, 1, -1));
                add(targetPos.add(-1, 1, 0));

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

    private int getTntSlot(){
        int slot3 = -1;
        for (int i = 0; i < 9; i++) {
            Item item3 = mc.player.inventory.getStack(i).getItem();
            Block block3 = Block.getBlockFromItem(item3);

            if (block3 instanceof TntBlock) {
                slot3 = i;
                break;
            }
        }
        return slot3;
    }

    /*/private int getSteelSlot(){
        //int slot2 = -1;
        //for (int i = 0; i < 9; i++) {
           // Item item2 = mc.player.inventory.getStack(i).getItem();
            /Item item3 = Item.BLOCK_ITEMS.get();

            if (block3 instanceof FlintAndSteelItem) {
                slot2 = i;
                break;
            }
        }
        return slot2;
    }

    /*/

    private void add(BlockPos blockPos) {
        if (!placePositions.contains(blockPos) && BlockUtils.canPlace(blockPos)) placePositions.add(blockPos);
    }

    @Override
    public String getInfoString() {
        if (target != null) return target.getEntityName();
        return null;
    }

    public enum tntPlacement{
        tnt
    }

    public enum tntActivate{
        LEVER,
        FlintAdnSteel
    }
}
