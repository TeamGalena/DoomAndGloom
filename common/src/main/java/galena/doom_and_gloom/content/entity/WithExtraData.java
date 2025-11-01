package galena.doom_and_gloom.content.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

// TODO add mixin implementation on fabric side
public interface WithExtraData {

    CompoundTag getExtraData();

    static CompoundTag getOrEmpty(Entity entity) {
        if (entity instanceof WithExtraData accessor) return accessor.getExtraData();
        return new CompoundTag();
    }

}
