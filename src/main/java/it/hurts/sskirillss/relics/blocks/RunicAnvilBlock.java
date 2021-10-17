package it.hurts.sskirillss.relics.blocks;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.tiles.RunicAnvilTile;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class RunicAnvilBlock extends FallingBlock {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    public RunicAnvilBlock() {
        super(Block.Properties
                .of(Material.HEAVY_METAL)
                .sound(SoundType.ANVIL)
                .strength(4.0F)
                .harvestTool(ToolType.PICKAXE)
                .noOcclusion());

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (handIn != Hand.MAIN_HAND || hit.getDirection() != Direction.UP
                || hit.getLocation().y() - pos.getY() < 0.75F)
            return ActionResultType.FAIL;

        TileEntity tile = world.getBlockEntity(pos);

        if (!(tile instanceof RunicAnvilTile))
            return ActionResultType.FAIL;

        RunicAnvilTile anvil = (RunicAnvilTile) tile;
        ItemStack heldStack = player.getItemInHand(handIn);

        if (heldStack.isEmpty()) {
            if (!anvil.takeItem(player))
                return ActionResultType.FAIL;
        } else {
            if (heldStack.getItem() == ItemRegistry.RUNIC_HAMMER.get()) {
                List<ItemStack> items = anvil.getItems();

                if (player.getCooldowns().isOnCooldown(heldStack.getItem()) || items.size() != 1)
                    return ActionResultType.FAIL;

                ItemStack stack = items.get(0);

                int expCost = 1 + (50 / stack.getMaxDamage());

                if (!(stack.getItem() instanceof RelicItem) || !stack.isDamaged()
                        || player.totalExperience < expCost)
                    return ActionResultType.FAIL;

                player.giveExperiencePoints(-expCost);
                heldStack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
                player.getCooldowns().addCooldown(heldStack.getItem(), 10);

                stack.setDamageValue(Math.max(0, stack.getDamageValue() - (stack.getDamageValue() / 10 + 1)));

                world.playSound(null, pos, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.BLOCKS,
                        1F, 1F - world.getRandom().nextFloat() * 0.2F);

                Vector3d animationVec = new Vector3d(pos.getX() + 0.5F, pos.getY() + 0.85F, pos.getZ() + 0.5F);
                world.addParticle(ParticleTypes.EXPLOSION, animationVec.x(), animationVec.y(),
                        animationVec.z(), 0F, 0F, 0F);
                ParticleUtils.createBall(ParticleTypes.LAVA, animationVec, world, 1, 0.4F);
            } else {
                if (!anvil.insertItem(heldStack))
                    return ActionResultType.FAIL;
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity tile = worldIn.getBlockEntity(pos);

            if (tile instanceof RunicAnvilTile) {
                List<ItemStack> items = ((RunicAnvilTile) tile).getItems();

                items.forEach(stack -> worldIn.addFreshEntity(new ItemEntity(worldIn,
                        pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, stack)));
            }
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        VoxelShape BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

        VoxelShape X_AXIS_AABB = VoxelShapes.or(BASE,
                Block.box(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D),
                Block.box(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D),
                Block.box(0.0D, 10.0D, 3.0D, 16.0D, 15.0D, 13.0D));
        VoxelShape Z_AXIS_AABB = VoxelShapes.or(BASE,
                Block.box(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D),
                Block.box(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D),
                Block.box(3.0D, 10.0D, 0.0D, 13.0D, 15.0D, 16.0D));

        return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RunicAnvilTile();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected void falling(FallingBlockEntity entity) {
        entity.setHurtsEntities(true);
    }

    @Override
    public void onLand(World world, BlockPos pos, BlockState oldState, BlockState newState, FallingBlockEntity entity) {
        if (!entity.isSilent())
            world.levelEvent(1031, pos, 0);
    }
}