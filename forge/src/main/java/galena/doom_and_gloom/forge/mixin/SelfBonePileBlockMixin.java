package galena.doom_and_gloom.forge.mixin;

import galena.doom_and_gloom.content.block.BonePileBlock;
import galena.doom_and_gloom.forge.client.BonePileClientProperties;
import galena.doom_and_gloom.index.DGParticleTypes;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BonePileBlock.class, remap = false)
public abstract class SelfBonePileBlockMixin extends FallingBlock {

    public SelfBonePileBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, BlockState other, LivingEntity entity, int numberOfParticles) {
        var self = (BonePileBlock) (Object) this;
        self.particles(level, entity.position().add(0, 0.2, 0.0), numberOfParticles / 2);
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        var vec = entity.position().add(0, 0.2, 0.0);
        var speed = entity.isSprinting() ? 0.5F : 0.2F;
        var halfSpeed = speed / 2;
        level.addParticle(DGParticleTypes.BONE_FRAGMENT.get(), vec.x, vec.y, vec.z, level.random.nextDouble() * speed - halfSpeed, level.random.nextDouble() * speed - halfSpeed, level.random.nextDouble() * speed - halfSpeed);
        return true;
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        var self = (BonePileBlock) (Object) this;
        consumer.accept(new BonePileClientProperties(self));
    }

}
