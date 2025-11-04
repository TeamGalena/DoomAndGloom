package galena.doom_and_gloom.forge.mixin;

import galena.doom_and_gloom.content.entity.ISepulcherable;
import galena.doom_and_gloom.forge.ForgeEntrypoint;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

//TODO:avoid mixin, use services or dep injection instead. even through a generic helper method like isSepulchered, or setSepulchered. only advantage to this is that on 1.21 porting will be trivial
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ISepulcherable {

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void DG$setSepulchered(boolean sepulchered) {
        this.getPersistentData().putBoolean(ISepulcherable.DG_TAG_KEY, sepulchered);
    }

    @Override
    public boolean DG$wasSepulchered() {
        return this.getPersistentData().getBoolean(ISepulcherable.DG_TAG_KEY);
    }
}
