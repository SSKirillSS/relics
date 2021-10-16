package it.hurts.sskirillss.relics.blocks;

import it.hurts.sskirillss.relics.init.BlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class MagmaStoneBlock extends Block implements IVoidBlock {
    public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 8);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public MagmaStoneBlock() {
        super(AbstractBlock.Properties.of(Material.STONE, MaterialColor.NETHER)
                .randomTicks()
                .strength(1.0F)
                .harvestTool(ToolType.PICKAXE)
                .requiresCorrectToolForDrops()
                .lightLevel((state) -> 3));

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(FALLING, false)
                .setValue(LEVEL, 0));
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        updateState(world, pos, true);

        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        updateState(worldIn, pos, true);
    }

    private static void updateState(World worldIn, BlockPos pos, boolean isCascade) {
        if (worldIn.isClientSide())
            return;

        BlockState state = worldIn.getBlockState(pos);

        int level = state.getValue(LEVEL);
        int age = state.getValue(AGE);

        if (age < 3)
            worldIn.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), 2);
        else {
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 1.0F);

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
    public void stepOn(World worldIn, BlockPos pos, Entity entityIn) {
        int age = worldIn.getBlockState(pos).getValue(AGE);

        if (!entityIn.fireImmune() && entityIn instanceof LivingEntity && age > 0
                && !EnchantmentHelper.hasFrostWalker((LivingEntity) entityIn))
            entityIn.hurt(DamageSource.HOT_FLOOR, age);

        super.stepOn(worldIn, pos, entityIn);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE).add(FALLING).add(LEVEL);
    }
}