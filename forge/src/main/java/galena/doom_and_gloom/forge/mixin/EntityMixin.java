package galena.doom_and_gloom.forge.mixin;

import galena.doom_and_gloom.content.entity.WithExtraData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements WithExtraData {

    @Override
    public CompoundTag getExtraData() {
        var self = (Entity) (Object) this;
        return self.getPersistentData();
    }

}
