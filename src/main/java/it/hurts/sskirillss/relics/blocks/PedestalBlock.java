package it.hurts.sskirillss.relics.blocks;

import com.google.common.collect.ImmutableList;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.tiles.PedestalTile;
import it.hurts.sskirillss.relics.utils.TickerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class PedestalBlock extends Block implements EntityBlock {
    public static final DirectionProperty DIRECTION = DirectionProperty.create("direction", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);

    public PedestalBlock() {
        super(Block.Properties.of(Material.STONE)
                .strength(1.5F));

        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.UP));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return ImmutableList.of(new ItemStack(state.getBlock()));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn != InteractionHand.MAIN_HAND)
            return InteractionResult.FAIL;

        PedestalTile pedestal = (PedestalTile) world.getBlockEntity(pos);

        if (pedestal.getStack().isEmpty()) {
            ItemStack stack = player.getMainHandItem();

            if (stack.isEmpty())
                return InteractionResult.FAIL;

            pedestal.setStack(player.getMainHandItem().split(1));
        } else {
            ItemStack stack = pedestal.getStack();

            if (player.getMainHandItem().isEmpty())
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            else {
                ItemEntity drop = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), pedestal.getStack());

                drop.setPickUpDelay(0);

                world.addFreshEntity(drop);
            }

            pedestal.setStack(ItemStack.EMPTY);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tile = worldIn.getBlockEntity(pos);

            if (tile instanceof PedestalTile) {
                ItemStack stack = ((PedestalTile) tile).getStack();

                if (stack != null && !stack.isEmpty())
                    worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
            }
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(DIRECTION);

        if (direction == Direction.UP)
            return Block.box(4, 0, 4, 12, 4, 12);
        else if (direction == Direction.DOWN)
            return Block.box(4, 12, 4, 12, 16, 12);
        else if (direction == Direction.NORTH)
            return Block.box(4, 4, 12, 12, 12, 16);
        else if (direction == Direction.SOUTH)
            return Block.box(4, 4, 0, 12, 12, 4);
        else if (direction == Direction.EAST)
            return Block.box(0, 4, 4, 4, 12, 12);
        else if (direction == Direction.WEST)
            return Block.box(12, 4, 4, 16, 12, 12);
        else
            return Block.box(0, 0, 0, 16, 16, 16);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state;

        if (context.getPlayer() == null)
            state = this.defaultBlockState().setValue(DIRECTION, Direction.UP);
        else
            state = this.defaultBlockState().setValue(DIRECTION, context.getPlayer().isShiftKeyDown()
                    ? context.getClickedFace().getOpposite() : context.getClickedFace());

        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DIRECTION);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return TickerUtils.getTicker(type, TileRegistry.PEDESTAL_TILE.get(), PedestalTile::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalTile(pos, state);
    }
}