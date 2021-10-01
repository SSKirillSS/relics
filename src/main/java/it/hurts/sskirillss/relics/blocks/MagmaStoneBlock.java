package it.hurts.sskirillss.relics.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class MagmaStoneBlock extends Block implements IVoidBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public MagmaStoneBlock() {
        super(AbstractBlock.Properties.of(Material.STONE, MaterialColor.NETHER)
                .randomTicks()
                .strength(1.0F)
                .harvestTool(ToolType.PICKAXE)
                .requiresCorrectToolForDrops()
                .lightLevel((state) -> 3));
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        updateState(world, pos, state);
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        updateState(worldIn, pos, state);
    }

    private static void updateState(World worldIn, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        worldIn.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F, 1, 1, 1);
        worldIn.playSound(null, pos, SoundEvents.FUNGUS_BREAK,
                SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (age < 3) worldIn.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), 2);
        else {
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
        }
    }

    @Override
    public void stepOn(World worldIn, BlockPos pos, Entity entityIn) {
        int age = worldIn.getBlockState(pos).getValue(AGE);
        if (!entityIn.fireImmune() && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entityIn)
                && age > 0) entityIn.hurt(DamageSource.HOT_FLOOR, age);
        super.stepOn(worldIn, pos, entityIn);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}