package galena.oreganized.content.block;

import galena.oreganized.index.OCriteriaTriggers;
import galena.oreganized.index.OEffects;
import galena.oreganized.index.OItems;
import galena.oreganized.index.OParticleTypes;
import galena.oreganized.index.OTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class LeadOreBlock extends DropExperienceBlock {

    public LeadOreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropXp) {
        super.spawnAfterBreak(state, level, pos, stack, dropXp);

        if (shouldSpawnCloud(state, level, pos, stack)) {
            var vec = Vec3.atCenterOf(pos);
            var cloud = new AreaEffectCloud(level, vec.x, vec.y, vec.z);

            cloud.addEffect(new MobEffectInstance(OEffects.STUNNING.get(), 20 * 60));
            cloud.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 2));
            cloud.setParticle(OParticleTypes.LEAD_CLOUD.get());
            cloud.setDuration(60);

            level.addFreshEntity(cloud);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        var stack = player.getItemInHand(player.getUsedItemHand());
        if (shouldSpawnCloud(state, level, pos, stack) && player instanceof ServerPlayer serverPlayer) {
            OCriteriaTriggers.IN_LEAD_CLOUD.trigger(serverPlayer);
        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    protected boolean shouldSpawnCloud(BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
        if (stack.is(OItems.SCRIBE.get()) || EnchantmentHelper.hasSilkTouch(stack)) return false;

        for (var direction : Direction.values()) {
            var adjacentState = level.getBlockState(pos.relative(direction));
            if (adjacentState.is(OTags.Blocks.PREVENTS_LEAD_CLOUD)) return false;
        }

        return true;
    }

}