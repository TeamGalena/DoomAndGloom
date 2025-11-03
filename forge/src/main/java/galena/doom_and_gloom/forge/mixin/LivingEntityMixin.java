package galena.doom_and_gloom.forge.mixin;

import galena.doom_and_gloom.content.entity.ISepulcherable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

//TODO:avoid mixin, use services, dep injection instead
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
