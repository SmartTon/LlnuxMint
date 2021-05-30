package minegame159.meteorclient.systems.modules.extra;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.player.PlayerMoveEvent;
import minegame159.meteorclient.events.game.GameJoinedEvent;
import minegame159.meteorclient.events.game.GameLeftEvent;
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
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.graalvm.compiler.nodes.cfg.Block;

import java.util.ArrayList;
import java.util.List;


public class BurrowDetect extends Module {

    public BurrowDetect() {
        super(Categories.Mint, "BurrowDetect", "detects burrow");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> own = sgGeneral.add(new BoolSetting.Builder()
            .name("own")
            .description("own")
            .defaultValue(false)
            .build()
    );

    private final Setting<Double> targetRange = sgGeneral.add(new DoubleSetting.Builder()
            .name("target-range")
            .description("The radius in which players get targeted.")
            .defaultValue(4)
            .min(0)
            .sliderMax(20)
            .build()
    );

    private PlayerEntity target;
    private BlockPos blockPosTarget;
    private boolean sentMessage;
    private final List<PlayerEntity> burrowedPlayer = new ArrayList<>();

    /*/@Override
    public void OnTick() {
        mc.world.getPlayers().stream().filter(PlayerEntity -> PlayerEntity != mc.player).forEach((PlayerEntity -> {
            if (!burrowedPlayer.contains(PlayerEntity) && is)
        }));
    }

    private boolean isInBurrow(PlayerEntity playerEntity) {
        BlockPos playerPos = new BlockPos(getMiddlePosition(playerEntity.getX()), playerEntity.getY(), getMiddlePosition(playerEntity.getZ()));

        return mc.world.getBlockState(playerPos).getBlock() == Blocks.OBSIDIAN
                || mc.world.getBlockState(playerPos).getBlock() == Blocks.ENDER_CHEST
                || mc.world.getBlockState(playerPos).getBlock() == Blocks.ANVIL
                || mc.world.getBlockState(playerPos).getBlock() == Blocks.ANVIL
    }

    //This converts a double position such as 12.9 or 12.13 to a "middle" value of 12.5
    private double getMiddlePosition(double positionIn) {
        double positionFinal = Math.round(positionIn);

        if(Math.round(positionIn) > positionIn){
            positionFinal -= 0.5;
        }
        else if(Math.round(positionIn) <= positionIn){
            positionFinal += 0.5;
        }

        return positionFinal;
    }

     */

    @EventHandler
    private void onGameLeave(GameLeftEvent event) {
        toggle();
    }

    @EventHandler
    private void BurrowCheck(PlayerMoveEvent event){
        if (EntityUtils.isBadTarget(target, targetRange.get())) {
            PlayerEntity search = EntityUtils.getPlayerTarget(targetRange.get(), SortPriority.LowestDistance, false);
            if (search != target) sentMessage = false;
            target = search;
        }

        if (EntityUtils.isBadTarget(target, targetRange.get())) {
            target = null;
            blockPosTarget = null;
            return;
        }

        blockPosTarget = target.getBlockPos();

        if (mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.BEDROCK) || mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.ENDER_CHEST) || mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.CRYING_OBSIDIAN) || mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.NETHERITE_BLOCK) || mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.RESPAWN_ANCHOR)){
            if (burrowedPlayer.contains(target)){

            }
            else{
                burrowedPlayer.add(target);
                ChatUtils.moduleInfo(this, "Gay burrowed");
            }

        }
        else if (!mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.OBSIDIAN) || !mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.BEDROCK) || !mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.ENDER_CHEST) || !mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.CRYING_OBSIDIAN) || !mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.NETHERITE_BLOCK) || !mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.RESPAWN_ANCHOR)){
            burrowedPlayer.remove(target);
        }

    }


}
