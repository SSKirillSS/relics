package it.hurts.sskirillss.relics.blocks;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.feet.PhantomBootItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PhantomBlock extends Block {
    public PhantomBlock() {
        super(BlockBehaviour.Properties.of()
                .friction(0.98F)
                .strength(-1F)
                .dynamicShape()
                .noOcclusion()
        );
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, Mth.nextInt(level.getRandom(), 60, 120));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 0F, level.damageSources().fall());
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        Vec3 motion = entity.getDeltaMovement();

        if (motion.y > -0.5D) {
            super.updateEntityAfterFallOn(level, entity);

            return;
        }

        entity.setDeltaMovement(motion.x, -motion.y, motion.z);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            var entity = entityContext.getEntity();

            var stack = EntityUtils.findEquippedCurio(entity, ItemRegistry.PHANTOM_BOOT.get());

            if (!stack.isEmpty() && stack.getItem() instanceof PhantomBootItem relic && relic.isToggled(stack)) {
                var shape = Shapes.block();

                if (entityContext.isAbove(shape, pos, true))
                    return shape;
            }
        }

        return Shapes.empty();
    }
}