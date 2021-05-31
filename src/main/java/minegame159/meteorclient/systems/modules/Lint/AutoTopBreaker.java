/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.player.StartBreakingBlockEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.DoubleSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.entity.EntityUtils;
import minegame159.meteorclient.utils.entity.SortPriority;
import minegame159.meteorclient.utils.player.ChatUtils;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.player.PlayerUtils;
import minegame159.meteorclient.utils.player.Rotations;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoTopBreaker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> targetRange = sgGeneral.add(new DoubleSetting.Builder()
            .name("target-range")
            .description("The radius in which players get targeted.")
            .defaultValue(4)
            .min(0)
            .sliderMax(5)
            .build()
    );

    private final Setting<Boolean> crystal = sgGeneral.add(new BoolSetting.Builder()
            .name("crystal")
            .description("If there is no block below a city block it will place one before mining.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> support = sgGeneral.add(new BoolSetting.Builder()
            .name("support")
            .description("If there is no block below a city block it will place one before mining.")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> autoCrystal = sgGeneral.add(new BoolSetting.Builder()
            .name("autoCrystal")
            .description("autoCrystal.")
            .defaultValue(false)
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

    private PlayerEntity target;
    private BlockPos blockPosTarget;
    private boolean sentMessage;
    private Direction direction;
    private EndCrystalEntity crystalentity;


    public AutoTopBreaker() {
        super(Categories.ExtraLint, "auto-top-breaker", "Automatically breaks top block");
    }

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        direction = event.direction;
    }
    @EventHandler
    private void onTick(TickEvent.Pre event) {

        if (EntityUtils.isBadTarget(target, targetRange.get())) {
            PlayerEntity search = EntityUtils.getPlayerTarget(targetRange.get(), SortPriority.LowestDistance, false);
            if (search != target) sentMessage = false;
            target = search;
        }

        if (EntityUtils.isBadTarget(target, targetRange.get())) {
            target = null;
            blockPosTarget = null;
            if (selfToggle.get()) toggle();
            return;
        }

        blockPosTarget = target.getBlockPos();

        if (blockPosTarget == null) {
            if (selfToggle.get()) {
                ChatUtils.moduleError(this,"No target block found... disabling.");
                toggle();
            }
            target = null;
            return;
        }

        if (PlayerUtils.distanceTo(blockPosTarget) > mc.interactionManager.getReachDistance() && selfToggle.get()) {
            ChatUtils.moduleError(this,"Target block out of reach... disabling.");
            toggle();
            return;
        }

        if (!sentMessage) {
            ChatUtils.moduleInfo(this,"Attempting to city %s.", target.getEntityName());
            sentMessage = true;
        }

        int slot = InvUtils.findItemInHotbar(itemStack -> itemStack.getItem() == Items.DIAMOND_PICKAXE || itemStack.getItem() == Items.NETHERITE_PICKAXE);
        if (mc.player.abilities.creativeMode) slot = mc.player.inventory.selectedSlot;

        if (slot == -1) {
            if (selfToggle.get()) {
                ChatUtils.moduleError(this,"No pickaxe found... disabling.");
                toggle();
            }
            return;
        }
        if (crystal.get() && (mc.world.getBlockState(blockPosTarget.up(2)).getBlock().is(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPosTarget.up(2)).getBlock().is(Blocks.BEDROCK))) {
            Hand crystalHand = InvUtils.getHand(Items.END_CRYSTAL);
            int crystalSlot = InvUtils.findItemInHotbar(Items.END_CRYSTAL);
            int prevSlot = mc.player.inventory.selectedSlot;

            if (crystalHand != Hand.OFF_HAND && crystalSlot != -1) mc.player.inventory.selectedSlot = crystalSlot;
            mc.interactionManager.interactBlock(mc.player, mc.world, crystalHand, new BlockHitResult(mc.player.getPos(), direction, blockPosTarget.up(2), false));
            crystalentity = new EndCrystalEntity(mc.world, target.getX(), target.getY() + 3, target.getZ());
            if (crystalHand != Hand.OFF_HAND) mc.player.inventory.selectedSlot = prevSlot;


        }

        if (support.get()) {
            BlockUtils.place(blockPosTarget.up(2), Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.OBSIDIAN), rotate.get(), 0, true);
        }

        if (autoCrystal.get() && mc.world.getBlockState(blockPosTarget.up(2)).isAir()){

        }



        mc.player.inventory.selectedSlot = slot;

        if (rotate.get()) Rotations.rotate(Rotations.getYaw(blockPosTarget.up(2)), Rotations.getPitch(blockPosTarget.up(2)), () -> mine(blockPosTarget.up(2)));
        else mine(blockPosTarget.up(2));

        if (selfToggle.get()) toggle();
    }

    private void mine(BlockPos blockPos) {
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.UP));
        mc.player.swingHand(Hand.MAIN_HAND);
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
    }

    @Override
    public String getInfoString() {
        if (target != null) return target.getEntityName();
        return null;
    }
}
