package minegame159.meteorclient.systems.modules.extra;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Category;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.player.ChatUtils;
import minegame159.meteorclient.utils.player.PlayerUtils;
import minegame159.meteorclient.utils.player.Rotations;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LintBurrow extends Module {
    public LintBurrow(){
        super(Categories.Mint,"LintBurrow", "BurrowByLint");
    }

    public enum Block{
        EChest,
        Obsidian,
        HeldBlock
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Send a rotation packet when placing.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Block> block = sgGeneral.add(new EnumSetting.Builder<Block>()
            .name("block-to-use")
            .description("The block to use for Burrow.")
            .defaultValue(Block.EChest)
            .build()
    );
    private final Setting<Integer> clipDistance = sgGeneral.add(new IntSetting.Builder()
            .name("clip-distance")
            .description("Vertical clip distance.")
            .defaultValue(6)
            .min(1)
            .sliderMax(16)
            .build()
    );
    private final Setting<Double> timer = sgGeneral.add(new DoubleSetting.Builder()
            .name("timer")
            .description("TPS multiplier for burrowing.")
            .defaultValue(1.00)
            .min(0.01)
            .sliderMax(10)
            .build()
    );
    private final Setting<Boolean> cancelMovement = sgGeneral.add(new BoolSetting.Builder()
            .name("cancel-movement")
            .description("Stops you from moving during the burrow.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
            .name("center")
            .description("Centers you to the middle of the block when burrowing.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Double> yTrigger = sgGeneral.add(new DoubleSetting.Builder()
            .name("clip-trigger-height")
            .description("What Y height to trigger vertical clip.")
            .defaultValue(1.00)
            .min(0.01)
            .sliderMax(1.25)
            .build()
    );
    private final Setting<Double> jumpVelocity = sgGeneral.add(new DoubleSetting.Builder()
            .name("jump-velocity")
            .description("Speed at which you fake-jump.")
            .min(0.01)
            .max(10)
            .sliderMax(10)
            .defaultValue(3)
            .build()
    );

    private int prevSlot;
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();

    @Override
    public void onActivate() {
        if(checkHead()) {
            setBlockPos();
            if(findSlot()) {
                mc.player.setVelocity(mc.player.getVelocity().x, jumpVelocity.get(), mc.player.getVelocity().z);
                ChatUtils.moduleInfo(this, "Burrowing.");
            } else {
                ChatUtils.moduleError(this, "Burrow block not found.");
                toggle();
            }
        } else {
            ChatUtils.moduleError(this, "Not enough room to burrow.");
            toggle();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if(center.get()){
            PlayerUtils.centerPlayer();
        }

        if(cancelMovement.get()){
            mc.player.setVelocity(0, mc.player.getVelocity().getY(), 0);
        }

        if (mc.player.getY() > blockPos.getY() + yTrigger.get()) {
            if (rotate.get()) {
                float[] rotation = PlayerUtils.calculateAngle(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                Rotations.rotate(rotation[0], rotation[1]);
            }

            place();
            mc.player.updatePosition(mc.player.getX(), mc.player.getY() + clipDistance.get(), mc.player.getZ());
            toggle();
        }

    }

    private void place() {
        BlockState blockState = mc.world.getBlockState(blockPos);
        boolean placed = !blockState.getMaterial().isReplaceable();

        if (!placed && findSlot()) {
            PlayerUtils.placeBlock(blockPos, Hand.MAIN_HAND, true);
            resetSlot();
        }
    }
    private void setBlockPos() {
        blockPos.set(mc.player.getBlockPos());
    }

    private boolean checkHead(){
        BlockState blockState1 = mc.world.getBlockState(blockPos.set(mc.player.getX() + .3, mc.player.getY() + 2.3, mc.player.getZ() + .3));
        BlockState blockState2 = mc.world.getBlockState(blockPos.set(mc.player.getX() +.3, mc.player.getY() + 2.3, mc.player.getZ() -.3));
        BlockState blockState3 = mc.world.getBlockState(blockPos.set(mc.player.getX() -.3, mc.player.getY() + 2.3, mc.player.getZ() -.3));
        BlockState blockState4 = mc.world.getBlockState(blockPos.set(mc.player.getX() -.3, mc.player.getY() + 2.3, mc.player.getZ() +.3));
        boolean air1 = blockState1.getMaterial().isReplaceable();
        boolean air2 = blockState2.getMaterial().isReplaceable();
        boolean air3 = blockState3.getMaterial().isReplaceable();
        boolean air4 = blockState4.getMaterial().isReplaceable();
        return air1 & air2 & air3 & air4;

    }

    private boolean findSlot() {
        prevSlot = mc.player.inventory.selectedSlot;

        if(block.get() == Block.HeldBlock) {
            Item item = mc.player.inventory.getStack(mc.player.inventory.selectedSlot).getItem();
            if(item instanceof BlockItem) {
                return true;
            }
        }

        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStack(i).getItem();

            if (!(item instanceof BlockItem)) continue;

            if (item == Items.OBSIDIAN && block.get() == Block.Obsidian) {
                mc.player.inventory.selectedSlot = i;
                return true;
            }
            if (item == Items.ENDER_CHEST && block.get() == Block.EChest) {
                mc.player.inventory.selectedSlot = i;
                return true;
            }
        }

        return false;
    }

    public double getBurrowMultiplier() {
        return isActive() ? timer.get() : 1;
    }

    private void resetSlot() {
        mc.player.inventory.selectedSlot = prevSlot;
    }
}
