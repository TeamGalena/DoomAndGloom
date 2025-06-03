package galena.doom_and_gloom.index;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.effect.FogEffect;
import galena.doom_and_gloom.content.effect.WardingEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, DoomAndGloom.MOD_ID);

    public static final RegistryObject<MobEffect> FOG = EFFECTS.register("fog", FogEffect::new);
    public static final RegistryObject<MobEffect> WARDING = EFFECTS.register("warding", WardingEffect::new);

}
