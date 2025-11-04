package galena.doom_and_gloom.content.entity;

import galena.doom_and_gloom.DoomAndGloom;
import net.minecraft.world.entity.LivingEntity;

public interface ISepulcherable {
    String DG_TAG_KEY = DoomAndGloom.MOD_ID + ":sepulched";

    static ISepulcherable cast(LivingEntity entity) {
        return (ISepulcherable) entity;
    }

    void DG$setSepulchered(boolean sepulchered);

    boolean DG$wasSepulchered();

}
