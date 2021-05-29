package minegame159.meteorclient.systems.modules.extra;

import java.math.BigDecimal;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.IntSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FullSurround extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    

    private final Setting<Integer> cooldown = sgGeneral.add(new IntSetting.Builder()
            .name("cooldown")
            .description("Block place cooldown.")
            .defaultValue(0)
            .min(0)
            .sliderMax(20)
            .build()
    );


    public enum ecenter {
        fast,
        legit,
        disable
    }
  

    private final Setting<ecenter> center = sgGeneral.add(new EnumSetting.Builder<ecenter>()
            .name("center")
            .description("Teleport to center block.")
            .defaultValue(ecenter.legit)
            .build()
    );
    
    private final Setting<Boolean> doubleHeight = sgGeneral.add(new BoolSetting.Builder()
            .name("double-height")
            .description("Places obsidian on top of the original surround blocks to prevent people from face-placing you.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> wideDown = sgGeneral.add(new BoolSetting.Builder()
            .name("wide-down")
            .description("Big Down")
            .defaultValue(false)
            .build()
    );
    
    private final Setting<Boolean> upDown = sgGeneral.add(new BoolSetting.Builder()
            .name("up-down")
            .description("Up Down")
            .defaultValue(false)
            .build()
    );
    
    private final Setting<Boolean> helpUP = sgGeneral.add(new BoolSetting.Builder()
            .name("help-up")
            .description("Help head block place. Only working if Up Down is enabled")
            .defaultValue(false)
            .build()
    );

    
    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder()
            .name("only-on-ground")
            .description("Works only when you standing on blocks.")
            .defaultValue(true)
            .build()
    );


    private final Setting<Boolean> disableOnJump = sgGeneral.add(new BoolSetting.Builder()
            .name("disable-on-jump")
            .description("Automatically disables when you jump.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Automatically faces towards the obsidian being placed.")
            .defaultValue(true)
            .build()
    );

    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private boolean return_;

    public FullSurround() {
        super(Categories.Mint, "full-surround", "Surrounds you in blocks to prevent you from taking lots of damage.");
    }

    
    @Override
    public void onActivate() {
    	
    	if(center.get().name()=="legit")
    		tocenter = true;

        tickskip = cooldown.get();
    	
        if(center.get().name()=="fast"){
	    	double tx=0,tz=0;
	
	    	Vec3d p = mc.player.getPos(); 
	    	
		   	 if (p.x>0 && gp(p.x)<3) tx=0.3;
			 if (p.x>0 && gp(p.x)>6) tx=-0.3;
			 if (p.x<0 && gp(p.x)<3) tx=-0.3;
			 if (p.x<0 && gp(p.x)>6) tx=0.3;
		
			 if (p.z>0 && gp(p.z)<3) tz=0.3;
			 if (p.z>0 && gp(p.z)>6) tz=-0.3;
			 if (p.z<0 && gp(p.z)<3) tz=-0.3;
			 if (p.z<0 && gp(p.z)>6) tz=0.3;
	
			 if(tx!=0 || tz!=0){
		    	 double posx = mc.player.getX() + tx;
		         double posz = mc.player.getZ() + tz;
		         mc.player.updatePosition(posx, mc.player.getY(), posz);
		         mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
		    }
        }
    	 
    }


    private long gp(double v) {
    	   BigDecimal v1 = BigDecimal.valueOf(v);
	       BigDecimal v2 = v1.remainder(BigDecimal.ONE);
	       return Byte.valueOf(String.valueOf(String.valueOf(v2).replace("0.", "").replace("-", "").charAt(0)));
    }
    
    
    
    
    int tickskip = cooldown.get();
    boolean tocenter = false;
    
    @EventHandler
    private void onTick(TickEvent.Pre event) {
    	
    	
    	
    	

        if(center.get().name()=="legit"){
        	
	    	double tx=0,tz=0;
	    	Vec3d p = mc.player.getPos(); 
		   	 if (p.x>0 && gp(p.x)<3)	tx=0.185;
			 if (p.x>0 && gp(p.x)>6)	tx=-0.185;
			 if (p.x<0 && gp(p.x)<3)	tx=-0.185;
			 if (p.x<0 && gp(p.x)>6)	tx=0.185;
		
			 if (p.z>0 && gp(p.z)<3)	tz=0.185;
			 if (p.z>0 && gp(p.z)>6)	tz=-0.185;
			 if (p.z<0 && gp(p.z)<3)	tz=-0.185;
			 if (p.z<0 && gp(p.z)>6)	tz=0.185;	

		
			 if(tx!=0 || tz!=0){
		    	 double posx = mc.player.getX() + tx;
		         double posz = mc.player.getZ() + tz;
		         mc.player.updatePosition(posx, mc.player.getY(), posz);
		         mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
		         return;
		    }
        }
    	
    	
    	
    	
    	
    	if(tickskip==0) {tickskip = cooldown.get();} else {tickskip--;return;}
    		
    	
    	
        if (disableOnJump.get() && mc.options.keyJump.isPressed()) {
            toggle();
            return;
        }

        if (onlyOnGround.get() && !mc.player.isOnGround()) return;

        return_ = false;

        place(0, -1, 0);	if (return_) return;

        place(1, 0, 0);		if (return_) return;
        place(-1, 0, 0);	if (return_) return;
        place(0, 0, 1)	;	if (return_) return;
        place(0, 0, -1);	if (return_) return;

        place(1, -1, 0);	if (return_) return;
        place(-1, -1, 0);	if (return_) return;
        place(0, -1, 1);	if (return_) return;
        place(0, -1, -1);	if (return_) return;
        
        
        
        
		// Big Down
        if (wideDown.get()) {
            place(1, 0, 1);		if (return_) return;
            place(-1, 0, -1);	if (return_) return;
            place(-1, 0, 1);	if (return_) return;
            place(1, 0, -1);	if (return_) return;
            place(2, 0, 0);		if (return_) return;
            place(-2, 0, 0);	if (return_) return;
            place(0, 0, 2);		if (return_) return;
            place(0, 0, -2);	if (return_) return;

        }
        

        // Sides up
        boolean doubleHeightPlaced = false;
        if (doubleHeight.get()) {
            place(1, 1, 0); 	if (return_) return;
            place(-1, 1, 0); 	if (return_) return;
            place(0, 1, 1);		if (return_) return;
            place(0, 1, -1);	if (return_) return;

        }
        
     
        
        //fix up
        if(gp(mc.player.getPos().y)>1){
            place(1, 2, 0);		if (return_) return;
            place(0, 2, 1);		if (return_) return;
            place(-1, 2, 0);	if (return_) return;
            place(0, 2, -1);	if (return_) return;
            place(0, 3, 0);		if (return_) return;
        }
        
        
        //help up air place
        boolean helpUPPlaced = false;
        if (helpUP.get() && doubleHeightPlaced) {
            
        	boolean p18 = place(1, 2, 0);	if (return_) return;
        	
            if(p18) helpUPPlaced = true;
        }
        
        // Up Down
        if (upDown.get()) {
        	if( (helpUP.get() && helpUPPlaced) || !helpUP.get() ){
        		
        		place(0, 2, 0);	if (return_) return;
        	}
            
            place(0, -2, 0); 	if (return_) return;      
        }


    };

    
    
    
    private boolean place(int x, int y, int z) {
        setBlockPos(x, y, z);
        
        if( (mc.player.getY()+y)<0 || (mc.player.getY()+y)>254)
        	return true;
        
        if(!mc.world.canPlace(Blocks.OBSIDIAN.getDefaultState(), blockPos, ShapeContext.absent()))
        	return true;
        
        BlockState blockState = mc.world.getBlockState(blockPos);
        boolean placed = !blockState.getMaterial().isReplaceable();

        int slot = InvUtils.findItemInHotbar(Items.OBSIDIAN);
        if (BlockUtils.place(blockPos, Hand.MAIN_HAND, slot, rotate.get(), 100,true)) return_ = true;

        return placed;
    }

    private void setBlockPos(int x, int y, int z) {
    	
        blockPos.set(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);
    }


}
