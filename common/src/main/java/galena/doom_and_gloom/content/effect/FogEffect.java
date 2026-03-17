package galena.doom_and_gloom.content.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class FogEffect extends MobEffect {

    public FogEffect() {
        super(MobEffectCategory.HARMFUL, 0x514AA1);
        setBlendDuration(22);
    }

}
