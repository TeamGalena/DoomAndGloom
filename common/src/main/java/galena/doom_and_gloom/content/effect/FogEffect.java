package galena.doom_and_gloom.content.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

public class FogEffect extends MobEffect {

    public FogEffect() {
        super(MobEffectCategory.HARMFUL, 2696993);
        this.setFactorDataFactory(() -> new MobEffectInstance.FactorData(22));
    }

}
