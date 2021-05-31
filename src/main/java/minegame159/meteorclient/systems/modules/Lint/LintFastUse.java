package minegame159.meteorclient.systems.modules.Lint;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.mixin.MinecraftClientAccessor;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ExperienceBottleItem;

public class LintFastUse extends Module {
    public LintFastUse(){
        super(Categories.ExtraLint,"LintFastUse", "Extra FastUse");
    }
    public enum Item {
        All,
        Exp,
        Crystal,
        ExpAndCrystal
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Item> itemChoose = sgGeneral.add(new EnumSetting.Builder<LintFastUse.Item>()
            .name("Which item")
            .description(".")
            .defaultValue(LintFastUse.Item.All)
            .build()
    );
    @EventHandler
    private void onTick(TickEvent.Post event) {
        switch(itemChoose.get()) {
            case All:
                setClickDelay();
                break;
            case Exp:
                assert mc.player != null;
                if(mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem)
                    setClickDelay();
                break;
            case Crystal:
                assert mc.player != null;
                if(mc.player.getMainHandStack().getItem() instanceof EndCrystalItem)
                    setClickDelay();
                break;
            case ExpAndCrystal:
                assert mc.player != null;
                if(mc.player.getMainHandStack().getItem() instanceof EndCrystalItem || mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem)
                    setClickDelay();
                break;
        }
    };

    private void setClickDelay() {
        ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
    }
}
