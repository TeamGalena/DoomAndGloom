package galena.doom_and_gloom.content.block;

import galena.doom_and_gloom.index.OParticleTypes;
import galena.doom_and_gloom.index.OSoundEvents;
import java.util.function.Consumer;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

public class BonePileBlock extends FallingBlock {

    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    private static final StatePredicate ALWAYS = (s, l, p) -> true;

    public BonePileBlock(Properties properties) {
        super(properties.isRedstoneConductor(ALWAYS).isSuffocating(ALWAYS).isViewBlocking(ALWAYS).noParticlesOnBreak());
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // For particles
        if(context instanceof EntityCollisionContext ec && (ec.getEntity() == null || ec.getEntity() instanceof FallingBlockEntity)) return Shapes.block();
        return SHAPE;
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState other, FallingBlockEntity entity) {
        super.onLand(level, pos, state, other, entity);
        if (!entity.isSilent())
            level.playSound(null, pos, OSoundEvents.BONE_PILE_FALL.get(), SoundSource.BLOCKS, 1F, 1F);
        particles(level, Vec3.atCenterOf(pos), 20);
    }

    @Override
    public boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, BlockState other, LivingEntity entity, int numberOfParticles) {
        particles(level, entity.position().add(0, 0.2, 0.0), numberOfParticles / 2);
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        var vec = entity.position().add(0, 0.2, 0.0);
        var speed = entity.isSprinting() ? 0.5F : 0.2F;
        var halfSpeed = speed / 2;
        level.addParticle(OParticleTypes.BONE_FRAGMENT.get(), vec.x, vec.y, vec.z, level.random.nextDouble() * speed - halfSpeed, level.random.nextDouble() * speed - halfSpeed, level.random.nextDouble() * speed - halfSpeed);
        return true;
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new ClientProperties());
    }

    private void particles(Level level, Vec3 vec, int numberOfParticles) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(OParticleTypes.BONE_FRAGMENT.get(), vec.x, vec.y, vec.z, numberOfParticles, 0.35, 0.35, 0.35, 0.1);
        } else for (int i = 0; i < numberOfParticles; i++) {
            level.addParticle(OParticleTypes.BONE_FRAGMENT.get(),
                    vec.x + level.random.nextDouble() - 0.5, vec.y + level.random.nextDouble() - 0.5, vec.z + level.random.nextDouble() - 0.5,
                    level.random.nextDouble() * 0.3 - 0.15, level.random.nextDouble() * 0.3 - 0.15, level.random.nextDouble() * 0.3 - 0.15
            );
        }
    }

    public class ClientProperties implements IClientBlockExtensions {

        @Override
        public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
            particles(level, Vec3.atCenterOf(pos), 20);
            return IClientBlockExtensions.super.addDestroyEffects(state, level, pos, manager);
        }

    }

}
