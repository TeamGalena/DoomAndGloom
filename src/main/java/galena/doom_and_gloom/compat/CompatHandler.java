package galena.doom_and_gloom.compat;

import net.minecraftforge.fml.ModList;

public class CompatHandler {

    public static final boolean MOONLIGHT  = ModList.get().isLoaded("moonlight");
    public static final boolean SUPPLEMENTARIES  = ModList.get().isLoaded("supplementaries");
    public static final boolean AMENDMENTS  = ModList.get().isLoaded("amendments");
}
