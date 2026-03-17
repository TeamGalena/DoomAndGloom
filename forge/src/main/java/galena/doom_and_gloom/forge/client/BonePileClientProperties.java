package galena.doom_and_gloom.forge.client;

import galena.doom_and_gloom.DoomAndGloom;
import galena.doom_and_gloom.content.block.BonePileBlock;
import galena.doom_and_gloom.index.DGBlocks;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = DoomAndGloom.MOD_ID, value = Dist.CLIENT)
public record BonePileClientProperties(BonePileBlock block) implements IClientBlockExtensions {

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        block.particles(level, Vec3.atCenterOf(pos), 20);
        return IClientBlockExtensions.super.addDestroyEffects(state, level, pos, manager);
    }

    @SubscribeEvent
    public static void register(RegisterClientExtensionsEvent event) {
        var block = DGBlocks.BONE_PILE.get();
        event.registerBlock(new BonePileClientProperties(block), block);
    }

}