package galena.doom_and_gloom.compat.moonlight;

import com.google.common.collect.ImmutableSet;
import galena.doom_and_gloom.index.OTags;
import galena.doom_and_gloom.index.OVillagerTypes;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

public class CandleSensor extends Sensor<Villager> {

    public CandleSensor() {
        super(200);
    }

    @Override
    protected void doTick(ServerLevel pLevel, Villager pEntity) {
        if (!pEntity.isBaby() && pEntity.getVillagerData().getProfession() == OVillagerTypes.GRAVETENDER.get()) {
            ResourceKey<Level> resourcekey = pLevel.dimension();
            BlockPos blockpos = pEntity.blockPosition();
            GlobalPos found = null;
            int rad = 4;

            for (int j = -rad; j <= rad; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    for (int l = -rad; l <= rad; ++l) {
                        BlockPos blockpos1 = blockpos.offset(j, k, l);
                        if (pLevel.getBlockState(blockpos1).is(OTags.Blocks.GRAVETENDER_LIGHTABLE)) {
                            found = (GlobalPos.of(resourcekey, blockpos1));
                            break;
                        }
                    }
                }
            }


            Brain<?> brain = pEntity.getBrain();

            if (found != null) {
                brain.setMemory(MoonlightCompat.NEAREST_UNLIT_CANDLE.get(), found);
            } else {
                brain.eraseMemory(MoonlightCompat.NEAREST_UNLIT_CANDLE.get());
            }
        }

    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MoonlightCompat.NEAREST_UNLIT_CANDLE.get());
    }
}