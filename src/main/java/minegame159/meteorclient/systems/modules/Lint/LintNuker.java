package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.systems.modules.Modules;
import minegame159.meteorclient.utils.player.ChatUtils;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.world.TickRate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;

public class LintNuker extends Module {
    public LintNuker(){
        super(Categories.ExtraLint,"Lint Nuker","extra nuker");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder()
            .name("only-on-ground")
            .description("Works only when you standing on blocks.")
            .defaultValue(true)
            .build()
    );


    private final Setting<Boolean> itemsaver = sgGeneral.add(new BoolSetting.Builder()
            .name("save-items")
            .description("Prevent destruction of tools.")
            .defaultValue(true)
            .onChanged((bool_1) -> toggleitem())
            .build()
    );

    private final Setting<Boolean> replaceitems = sgGeneral.add(new BoolSetting.Builder()
            .name("replace-items")
            .description("Replace tools before breakage.")
            .defaultValue(true)
            .onChanged((bool_1) -> togglereplace())
            .build()
    );

    private final Setting<Boolean> sword = sgGeneral.add(new BoolSetting.Builder()
            .name("stop-on-sword")
            .description("Pause nuker if sword in main hand.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> spamlimit = sgGeneral.add(new IntSetting.Builder()
            .name("speed")
            .description("Block break speed.")
            .defaultValue(25)
            .min(1)
            .sliderMin(1)
            .sliderMax(100)
            .build()
    );

    private final Setting<Double> lagg = sgGeneral.add(new DoubleSetting.Builder()
            .name("stop-on-lags")
            .description("Pause on server lagging. (Time since last tick)")
            .defaultValue(0.8)
            .min(0.1)
            .max(5)
            .sliderMin(0.1)
            .sliderMax(5)
            .build()
    );

    private final Setting<Double> Distance = sgGeneral.add(new DoubleSetting.Builder()
            .name("distance")
            .description("Maximum distance.")
            .defaultValue(6.6)
            .build()
    );

    private final Setting<Integer> xmin = sgGeneral.add(new IntSetting.Builder()
            .name("x-min")
            .defaultValue(1)
            .min(0)
            .max(6)
            .sliderMin(0)
            .sliderMax(6)
            .build()
    );

    private final Setting<Integer> xmax = sgGeneral.add(new IntSetting.Builder()
            .name("x-max")
            .defaultValue(1)
            .min(0)
            .max(6)
            .sliderMin(0)
            .sliderMax(6)
            .build()
    );

    private final Setting<Integer> zmin = sgGeneral.add(new IntSetting.Builder()
            .name("z-min")
            .defaultValue(1)
            .min(0)
            .max(6)
            .sliderMin(0)
            .sliderMax(6)
            .build()
    );

    private final Setting<Integer> zmax = sgGeneral.add(new IntSetting.Builder()
            .name("z-max")
            .defaultValue(1)
            .min(0)
            .max(6)
            .sliderMin(0)
            .sliderMax(6)
            .build()
    );

    private final Setting<Integer> ymin = sgGeneral.add(new IntSetting.Builder()
            .name("up")
            .defaultValue(1)
            .min(1)
            .max(6)
            .sliderMin(1)
            .sliderMax(6)
            .build()
    );

    private final Setting<Integer> ymax = sgGeneral.add(new IntSetting.Builder()
            .name("down")
            .defaultValue(0)
            .min(0)
            .max(7)
            .sliderMin(0)
            .sliderMax(7)
            .build()
    );


    private final Setting<Boolean> onlySelected = sgGeneral.add(new BoolSetting.Builder()
            .name("only-selected")
            .description("Only mines your selected blocks.")
            .defaultValue(false)
            .build()
    );

    private final Setting<List<Block>> selectedBlocks = sgGeneral.add(new BlockListSetting.Builder()
            .name("selected-blocks")
            .description("The certain type of blocks you want to mine.")
            .defaultValue(new ArrayList<>(0))
            .build()
    );

    int limit = 0;

    @Override
    public void onActivate() {
        limit = 0;
        if(Modules.get().get(LintFastBreak.class).isActive()) Modules.get().get(LintFastBreak.class).toggle();
    }





    @EventHandler
    private void onTick(TickEvent.Pre event) {

        try{

            if(onlyOnGround.get() && !mc.player.isOnGround()) return;

            if(TickRate.INSTANCE.getTimeSinceLastTick() >= lagg.get()) return;

            if(sword.get() && mc.player.getMainHandStack().getItem() instanceof SwordItem) return;

            limit = 0;

            int px = mc.player.getBlockPos().getX();
            int py = mc.player.getBlockPos().getY();
            int pz = mc.player.getBlockPos().getZ();

            for (int x = px - xmin.get(); x <= px + xmax.get(); x++) {
                for (int z = pz - zmin.get(); z <= pz + zmax.get(); z++) {
                    for (int y = py - ymax.get(); y <= py + ymin.get()-1; y++) {

                        BlockPos pos = new BlockPos(x, y, z);

                        if(mc.world.getBlockState(pos).getOutlineShape(mc.world, pos) == VoxelShapes.empty()) continue;
                        if(mc.world.getBlockState(pos).getBlock()== Blocks.BEDROCK) continue;
                        if(distance(pos.getX(), pos.getY(), pos.getZ()) >= Distance.get()) continue;


                        if (onlySelected.get() && !selectedBlocks.get().contains(mc.world.getBlockState(pos).getBlock())) continue;

                        if(limit > spamlimit.get()) return;

                        if(itemsaver.get()
                                && mc.player.getMainHandStack().getDamage()!=0
                                && mc.player.getMainHandStack().getMaxDamage()-mc.player.getMainHandStack().getDamage()<31) {
                            if(replaceitems.get() && swap_item()) return;

                            ChatUtils.moduleWarning(this,"Инструмент почти разрушен!");
                            toggle();
                            return;
                        }

                        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
                        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));

                        limit=limit+2;


                    }
                }
            }
        } catch (Exception ignored) {}
    }



    private boolean swap_item(){
        boolean result = false;
        Item item = mc.player.getMainHandStack().getItem();
        for(int x=0; x < mc.player.inventory.size(); x++){
            if(mc.player.inventory.getStack(x).getItem() != item) continue;
            if(mc.player.inventory.getStack(x).getMaxDamage() - mc.player.inventory.getStack(x).getDamage() < 31) continue;
            InvUtils.clickSlot(InvUtils.invIndexToSlotId(x), mc.player.inventory.selectedSlot, SlotActionType.SWAP);
            result = true;
            break;
        }

        return result;
    }

    private void toggleitem(){
        if(replaceitems.get() && !itemsaver.get()) replaceitems.set(false);
    }

    private void togglereplace(){
        if(replaceitems.get() && !itemsaver.get()) itemsaver.set(true);
    }

    private double distance(double x, double y, double z) {
        double d = mc.player.getPos().getX() - x;
        if(d<0) d--;
        double e = mc.player.getPos().getY() - y;
        double f = mc.player.getPos().getZ() - z;
        if(f<0) f--;
        return MathHelper.sqrt(d * d + e * e + f * f);
    }
}
