package minegame159.meteorclient.systems.modules.extra;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.player.StartBreakingBlockEvent;
import minegame159.meteorclient.events.render.RenderEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.rendering.Renderer;
import minegame159.meteorclient.rendering.ShapeMode;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Category;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.systems.modules.Modules;
import minegame159.meteorclient.systems.modules.combat.AutoTrap;
import minegame159.meteorclient.utils.entity.EntityUtils;
import minegame159.meteorclient.utils.entity.SortPriority;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.player.Rotations;
import minegame159.meteorclient.utils.render.color.SettingColor;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author seasnail8169
 */
public class CevBreaker extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<SortPriority> priority = sgGeneral.add(new EnumSetting.Builder<SortPriority>()
            .name("target-priority")
            .description("How to select the player to target.")
            .defaultValue(SortPriority.LowestHealth)
            .build()
    );

    private final Setting<Integer> tickDelay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay between breaks.")
            .defaultValue(0)
            .min(0)
            .sliderMax(20)
            .build()
    );
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("target-range")
            .description("The range players can be targeted.")
            .defaultValue(4)
            .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("place-delay")
            .description("How many ticks between block placements.")
            .defaultValue(1)
            .build()
    );

    private final Setting<Boolean> autotrap = sgGeneral.add(new BoolSetting.Builder()
            .name("autotrap")
            .description("trap top.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> selfToggle = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-trap-self-toggle")
            .description("Turns off after placing all blocks.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> pick = sgGeneral.add(new BoolSetting.Builder()
            .name("only-pick")
            .description("Only tries to mine the block if you are holding a pickaxe.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> insta = sgGeneral.add(new BoolSetting.Builder()
            .name("insta-mine")
            .description("Places an end crystal above the block getting mined.")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> packet = sgGeneral.add(new BoolSetting.Builder()
            .name("packet-mine")
            .description("Places an end crystal above the block getting mined.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Faces the blocks being mined server side.")
            .defaultValue(true)
            .build()
    );

    // Render

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

    private int ticks;
    private boolean shouldMine;
    private PlayerEntity target;
    private final List<BlockPos> placePositions = new ArrayList<>();
    private boolean placed;
    private int timer;

    private final BlockPos.Mutable blockPos = new BlockPos.Mutable(0, -1, 0);
    private Direction direction;

    public CevBreaker() {
        super(Categories.Mint, "Cev-Breaker", "Attempts to instantly mine blocks.");
    }


    @Override
    public void onActivate() {
        ticks = 0;
        blockPos.set(0, -1, 0);
        shouldMine = false;
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
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        direction = event.direction;
        blockPos.set(event.blockPos);
        shouldMine = mc.world.getBlockState(event.blockPos).getHardness(mc.world, event.blockPos) >= 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (ticks >= tickDelay.get()) {
            ticks = 0;



            if (shouldMine() && shouldMine && !mc.world.getBlockState(blockPos).isAir()) {
                if ((mc.world.getBlockState(blockPos).getBlock().is(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).getBlock().is(Blocks.BEDROCK))) {
                    Hand crystalHand = InvUtils.getHand(Items.END_CRYSTAL);
                    int crystalSlot = InvUtils.findItemInHotbar(Items.END_CRYSTAL);
                    int prevSlot = mc.player.inventory.selectedSlot;

                    if (crystalHand != Hand.OFF_HAND && crystalSlot != -1)
                        mc.player.inventory.selectedSlot = crystalSlot;
                    mc.interactionManager.interactBlock(mc.player, mc.world, crystalHand, new BlockHitResult(mc.player.getPos(), direction, blockPos, false));
                    if (crystalHand != Hand.OFF_HAND) mc.player.inventory.selectedSlot = prevSlot;


                }

                if (rotate.get()) {
                    Rotations.rotate(Rotations.getYaw(blockPos), Rotations.getPitch(blockPos), () -> mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction)));
                } else {
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
                }
                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));


            }
        } else ticks++;
    }

    private void fillPlaceArray(PlayerEntity target) {
        placePositions.clear();
        BlockPos targetPos = target.getBlockPos();

        add(targetPos.add(0,2,0));
    }

    private void add(BlockPos blockPos) {
        if (!placePositions.contains(blockPos) && BlockUtils.canPlace(blockPos)) placePositions.add(blockPos);
    }

    @EventHandler
    private void onRender(RenderEvent event) {
        if (!render.get() || !shouldMine() || !shouldMine || blockPos.getY() == -1) return;
        Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, blockPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    private boolean shouldMine() {
        if (!pick.get()) return true;
        else
            return pick.get() && (mc.player.inventory.getMainHandStack().getItem() instanceof PickaxeItem && (mc.player.getMainHandStack().getItem() == Items.DIAMOND_PICKAXE || mc.player.getMainHandStack().getItem() == Items.NETHERITE_PICKAXE));
    }
}
