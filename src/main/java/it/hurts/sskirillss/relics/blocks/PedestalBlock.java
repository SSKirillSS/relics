package it.hurts.sskirillss.relics.blocks;

import com.google.common.collect.ImmutableList;
import it.hurts.sskirillss.relics.tiles.PedestalTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class PedestalBlock extends Block {
    public static final DirectionProperty DIRECTION = DirectionProperty.create("direction", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);

    public PedestalBlock() {
        super(Block.Properties.of(Material.STONE).strength(1.5F).harvestTool(ToolType.PICKAXE));
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.UP));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return ImmutableList.of(new ItemStack(state.getBlock()));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (handIn != Hand.MAIN_HAND) return ActionResultType.FAIL;
        PedestalTile pedestal = (PedestalTile) world.getBlockEntity(pos);
        if (pedestal.getStack().isEmpty()) {
            ItemStack stack = player.getMainHandItem();
            if (stack.isEmpty()) return ActionResultType.FAIL;
            pedestal.setStack(player.getMainHandItem().split(1));
        } else {
            ItemStack stack = pedestal.getStack();
            if (player.getMainHandItem().isEmpty()) player.setItemInHand(Hand.MAIN_HAND, stack);
            else {
                ItemEntity drop = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), pedestal.getStack());
                drop.setPickUpDelay(0);
                world.addFreshEntity(drop);
            }
            pedestal.setStack(ItemStack.EMPTY);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof PedestalTile) {
                ItemStack stack = ((PedestalTile) tile).getStack();
                if (stack != null && !stack.isEmpty())
                    worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.getValue(DIRECTION);
        if (direction == Direction.UP) return Block.box(4, 0, 4, 12, 4, 12);
        else if (direction == Direction.DOWN) return Block.box(4, 16, 4, 12, 12, 12);
        else if (direction == Direction.NORTH) return Block.box(12, 4, 16, 4, 12, 12);
        else if (direction == Direction.SOUTH) return Block.box(12, 4, 4, 4, 12, 0);
        else if (direction == Direction.EAST) return Block.box(0, 4, 4, 4, 12, 12);
        else if (direction == Direction.WEST) return Block.box(12, 4, 4, 16, 12, 12);
        else return Block.box(0, 0, 0, 16, 16, 16);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PedestalTile();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state;
        if (context.getPlayer() == null) state = this.defaultBlockState().setValue(DIRECTION, Direction.UP);
        else state = this.defaultBlockState().setValue(DIRECTION, context.getPlayer().isShiftKeyDown()
                ? context.getClickedFace().getOpposite() : context.getClickedFace());
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DIRECTION);
    }
}