package minegame159.meteorclient.systems.modules.extra;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.IntSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.player.InvUtils;
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

public class PacketSurround extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

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
    private int timer;

    public PacketSurround() {
        super(Categories.Mint, "packet-surround", "Surrounds you in blocks to prevent you from taking lots of damage.");
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



        //mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), Direction.DOWN, blockPos, false)));

    }

    private boolean place(int x, int y, int z) {
        setBlockPos(x, y, z);
        BlockState blockState = mc.world.getBlockState(blockPos);

        if (!blockState.getMaterial().isReplaceable()) return true;

        int slot = findSlot();

        if (timer >= delay.get()) {
            if (BlockUtils.place(blockPos, Hand.MAIN_HAND, slot, rotate.get(), 50, true)) {
                mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), Direction.DOWN, blockPos, false)));
            }

            timer = 0;
        } else {
            timer++;
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
