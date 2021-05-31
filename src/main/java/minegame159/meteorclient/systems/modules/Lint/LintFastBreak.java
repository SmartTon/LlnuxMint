package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.player.StartBreakingBlockEvent;
import minegame159.meteorclient.events.packets.PacketEvent;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.IntSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.systems.modules.Modules;
import minegame159.meteorclient.utils.misc.ThreadUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public class LintFastBreak extends Module {
    public LintFastBreak(){
        super(Categories.ExtraLint,"LintFastBreak", "Extra FastBreak");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .defaultValue(50)
            .min(1)
            .max(500)
            .sliderMin(1)
            .sliderMax(500)
            .build()
    );



    private final Setting<Boolean> autocity = sgGeneral.add(new BoolSetting.Builder()
            .name("Auto City Break")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> smash = sgGeneral.add(new BoolSetting.Builder()
            .name("Smash")
            .description("Destroy the block instantly.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> obbysmash = sgGeneral.add(new BoolSetting.Builder()
            .name("Anti Obsidian")
            .description("Anti obsidian/Ender chest.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> allsmash = sgGeneral.add(new BoolSetting.Builder()
            .name("All Smash")
            .description("Destroy the block instantly. (All block)")
            .defaultValue(false)
            .build()
    );

    BlockPos pos = null;
    boolean insta = false;

    @Override
    public void onActivate() {
        pos = null;
        insta = false;
        ThreadUtils.runInThread(() -> BREAK());
    }




    @EventHandler
    private void AUTO_CITY(PacketEvent.Send e) {
        if (e.packet instanceof PlayerActionC2SPacket && autocity.get()) {
            PlayerActionC2SPacket p = (PlayerActionC2SPacket) e.packet;
            if(p.getAction().toString()=="START_DESTROY_BLOCK") pos = p.getPos();
        }
    }


    @EventHandler
    public void FAST_BREAK_START(StartBreakingBlockEvent e) {
        try{
            pos = e.blockPos;

            Block b = mc.world.getBlockState(pos).getBlock();
            if(
                    b==Blocks.BEDROCK
                            || b==Blocks.NETHER_PORTAL
                            || b==Blocks.END_GATEWAY
                            || b==Blocks.END_PORTAL
                            || b==Blocks.END_PORTAL_FRAME
                            || b==Blocks.BARRIER
            ) {
                pos = null;
                return;
            };


            Block[] block_pickaxe = {
                    Blocks.STONE,
                    Blocks.COBBLESTONE,
                    Blocks.NETHERRACK,
                    Blocks.TERRACOTTA,
                    Blocks.BASALT,
                    Blocks.FURNACE,
                    Blocks.IRON_BLOCK,
                    Blocks.GOLD_BLOCK,
                    Blocks.BONE_BLOCK
            };

            String[] block_axe = {
                    "acacia_", "oak_", "crimson_", "birch_", "warped_", "jungle_", "spruce_", "crafting_table"
            };

            String[] block_pickaxe2 = {
                    "stone_",
                    "andesite",
                    "diorite",
                    "granite",
                    "cobblestone_",
                    "mossy_",
                    "_terracotta",
                    "basalt",
                    "blackstone",
                    "end_",
                    "purpur_",
                    "shulker_box"
            };

            insta = false;

            if (smash.get() && mc.player.isOnGround()){

                if(mc.player.getMainHandStack().getItem() == Items.NETHERITE_PICKAXE){

                    if(Arrays.asList(block_pickaxe).contains(b)) insta = true;

                    for(int x=0; x < block_pickaxe2.length; x++){
                        if(b.asItem().toString().contains(block_pickaxe2[x])){
                            insta = true;
                            break;
                        }
                    }
                }

                if(mc.player.getMainHandStack().getItem() == Items.NETHERITE_AXE){
                    for(int x=0; x < block_axe.length; x++){
                        if(b.asItem().toString().contains(block_axe[x])){
                            insta = true;
                            break;
                        }
                    }
                }

                if(b.asItem().toString().contains("_leaves")) insta = false;
                if(b.asItem().toString().contains("_wart")) insta = false;
                if(allsmash.get()) insta = true;

                if(insta) mc.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        } catch (Exception ign) {}
    }



    @EventHandler
    private void AntiObby(PacketEvent.Receive e) {
        try{
            if (e.packet instanceof BlockUpdateS2CPacket) {
                BlockUpdateS2CPacket p = (BlockUpdateS2CPacket) e.packet;
                if(
                        obbysmash.get()
                                && pos.equals(p.getPos())
                                && (p.getState().getBlock() == Blocks.OBSIDIAN || p.getState().getBlock() == Blocks.ENDER_CHEST)
                ) e.cancel();
            }
        } catch (Exception ignor) {}
    }



    private void BREAK() {
        while(true){
            try{
                if(!Modules.get().isActive(LintFastBreak.class)) break;

                if(pos!=null) mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));

                ThreadUtils.sleep(delay.get());
            } catch (Exception ign) {}
        }
    }
}
