package it.hurts.sskirillss.relics.blocks;

import it.hurts.sskirillss.relics.init.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Random;

public class MagmaStoneBlock extends Block implements IVoidBlock {
    public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 8);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public MagmaStoneBlock() {
        super(Block.Properties.of(Material.STONE, MaterialColor.NETHER)
                .randomTicks()
                .strength(1.0F)
                .lightLevel((state) -> 3));

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(FALLING, false)
                .setValue(LEVEL, 0));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        updateState(world, pos, true);

        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
        updateState(worldIn, pos, true);
    }

    private static void updateState(Level worldIn, BlockPos pos, boolean isCascade) {
        if (worldIn.isClientSide())
            return;

        BlockState state = worldIn.getBlockState(pos);

        int level = state.getValue(LEVEL);
        int age = state.getValue(AGE);

        if (age < 3)
            worldIn.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), 2);
        else {
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);

            FluidState fluid = level > 0 ? Fluids.FLOWING_LAVA.getFlowing(level, state.getValue(FALLING)) : Fluids.LAVA.defaultFluidState();

            worldIn.setBlockAndUpdate(pos, fluid.createLegacyBlock());
        }

        if (!isCascade)
            return;

        for (Direction direction : Direction.values()) {
            BlockPos offset = pos.offset(direction.getStepX(), direction.getStepY(), direction.getStepZ());

            if (worldIn.getBlockState(offset).is(BlockRegistry.MAGMA_STONE_BLOCK.get()))
                updateState(worldIn, offset, worldIn.random.nextInt(10) == 0);
        }
    }

    @Override
    public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
        int age = state.getValue(AGE);

        if (!entityIn.fireImmune() && entityIn instanceof LivingEntity && age > 0
                && !EnchantmentHelper.hasFrostWalker((LivingEntity) entityIn))
            entityIn.hurt(DamageSource.HOT_FLOOR, age);

        super.stepOn(worldIn, pos, state, entityIn);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE).add(FALLING).add(LEVEL);
    }
}