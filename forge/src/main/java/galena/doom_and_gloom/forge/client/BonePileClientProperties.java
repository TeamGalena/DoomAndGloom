package galena.doom_and_gloom.forge.client;

import galena.doom_and_gloom.content.block.BonePileBlock;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

public record BonePileClientProperties(BonePileBlock block) implements IClientBlockExtensions {

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        block.particles(level, Vec3.atCenterOf(pos), 20);
        return IClientBlockExtensions.super.addDestroyEffects(state, level, pos, manager);
    }

}