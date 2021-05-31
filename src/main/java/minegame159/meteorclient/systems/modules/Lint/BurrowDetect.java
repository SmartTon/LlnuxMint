package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.player.PlayerMoveEvent;
import minegame159.meteorclient.events.game.GameLeftEvent;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.entity.EntityUtils;
import minegame159.meteorclient.utils.entity.SortPriority;
import minegame159.meteorclient.utils.player.ChatUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;


public class BurrowDetect extends Module {

    public BurrowDetect() {
        super(Categories.SimpleLint, "BurrowDetect", "detects burrow");
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

    private final Setting<String> BurrowedMessage = sgGeneral.add(new StringSetting.Builder()
            .name("BurrowedMessage")
            .description("The message")
            .defaultValue("player burrowed")
            .build()
    );

    private final Setting<String> noLongerBurrowedMessage = sgGeneral.add(new StringSetting.Builder()
            .name("NoLongerBurrowedMessage")
            .description("The message")
            .defaultValue("player is no longer burrowed")
            .build()
    );
    private final Setting<String> ownBurrowedMessage = sgGeneral.add(new StringSetting.Builder()
            .name("NoLongerBurrowedMessage")
            .description("The message")
            .defaultValue("You burrowed")
            .build()
    );
    private final Setting<String> ownNoLongerBurrowedMessage = sgGeneral.add(new StringSetting.Builder()
            .name("NoLongerBurrowedMessage")
            .description("The message")
            .defaultValue("You is no burrowed")
            .build()
    );

    private PlayerEntity player;
    private PlayerEntity target;
    private BlockPos blockPosTarget;
    private BlockPos playerPosTarget;
    private boolean sentMessage;
    private final List<PlayerEntity> burrowedPlayer = new ArrayList<>();
    private final List<PlayerEntity> ownburrowed= new ArrayList<>();
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
        player = mc.player;
        playerPosTarget = player.getBlockPos();
        blockPosTarget = target.getBlockPos();

        if (mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.BEDROCK) || mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.ENDER_CHEST) || mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.CRYING_OBSIDIAN) || mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.NETHERITE_BLOCK) || mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.RESPAWN_ANCHOR)){
            if (burrowedPlayer.contains(target)){

            }
            else{
                burrowedPlayer.add(target);
                ChatUtils.moduleInfo(this, BurrowedMessage.get().replace("player", (target).getGameProfile().getName()));
            }

        }
        else if (!mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.OBSIDIAN) || !mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.BEDROCK) || !mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.ENDER_CHEST) || !mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.CRYING_OBSIDIAN) || !mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.NETHERITE_BLOCK) || !mc.world.getBlockState(blockPosTarget).getBlock().is(Blocks.RESPAWN_ANCHOR)){
            if (burrowedPlayer.contains(target)) {
                burrowedPlayer.remove(target);
                ChatUtils.moduleInfo(this, noLongerBurrowedMessage.get().replace("player", (target).getGameProfile().getName()));
            }
        }

        if (own.get() && mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.OBSIDIAN) || mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.BEDROCK) || mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.ENDER_CHEST) || mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.CRYING_OBSIDIAN) || mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.NETHERITE_BLOCK) || mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.RESPAWN_ANCHOR)){
            if (ownburrowed.contains(player)){

            }
            else {
                ownburrowed.add(player);
                ChatUtils.moduleInfo(this, ownBurrowedMessage.get());
            }
        }

        else if(own.get() && !mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.OBSIDIAN) || !mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.BEDROCK) || !mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.ENDER_CHEST) || !mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.CRYING_OBSIDIAN) || !mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.NETHERITE_BLOCK) || !mc.world.getBlockState(playerPosTarget).getBlock().is(Blocks.RESPAWN_ANCHOR)){
            if (ownburrowed.contains(player)) {
                ownburrowed.remove(player);
                ChatUtils.moduleInfo(this, ownNoLongerBurrowedMessage.get());
            }
        }

    }


}
