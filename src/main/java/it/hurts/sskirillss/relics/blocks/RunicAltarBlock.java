package it.hurts.sskirillss.relics.blocks;

import com.google.common.collect.ImmutableList;
import it.hurts.sskirillss.relics.crafting.RunicAltarContext;
import it.hurts.sskirillss.relics.crafting.RunicAltarRecipe;
import it.hurts.sskirillss.relics.crafting.SingletonInventory;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.RuneItem;
import it.hurts.sskirillss.relics.tiles.RunicAltarTile;
import it.hurts.sskirillss.relics.utils.TickerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RunicAltarBlock extends Block implements EntityBlock {
    public RunicAltarBlock() {
        super(Block.Properties.of(Material.STONE)
                .strength(3.0F)
                .lightLevel((state) -> 5)
                .noOcclusion());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return ImmutableList.of(new ItemStack(state.getBlock()));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn != InteractionHand.MAIN_HAND)
            return InteractionResult.FAIL;

        RunicAltarTile altar = (RunicAltarTile) world.getBlockEntity(pos);

        if (altar == null)
            return InteractionResult.FAIL;

        Direction direction = hit.getDirection();

        if (direction == Direction.DOWN)
            return InteractionResult.FAIL;

        ItemStack handStack = player.getItemInHand(handIn);

        if (altar.getCraftingProgress() != 0)
            return InteractionResult.FAIL;

        ItemStack stack = altar.getStack(direction);

        if (stack == null)
            return InteractionResult.FAIL;

        if (stack.isEmpty()) {
            if (handStack.isEmpty())
                return InteractionResult.FAIL;

            if (direction != Direction.UP && !(handStack.getItem() instanceof RuneItem))
                return InteractionResult.FAIL;

            altar.setStack(handStack.split(1), direction);

            Optional<RunicAltarRecipe> optional = world.getRecipeManager().getRecipeFor(RunicAltarRecipe.RECIPE, new RunicAltarContext(
                    new SingletonInventory(altar.getStack(Direction.UP)), player, altar.getRunes(), altar.getStack(Direction.UP)), world);

            if (!optional.isPresent())
                return InteractionResult.FAIL;

            altar.addCraftingProgress(1);
        } else {
            if (player.getMainHandItem().isEmpty())
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            else {
                ItemEntity drop = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), altar.getStack(direction));

                drop.setPickUpDelay(0);

                world.addFreshEntity(drop);
            }

            altar.setStack(ItemStack.EMPTY, direction);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof RunicAltarTile) {
                RunicAltarTile altar = (RunicAltarTile) tile;
                for (Direction direction : Direction.values()) {
                    ItemStack stack = altar.getStack(direction);
                    if (stack == null || stack.isEmpty()) continue;
                    worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
                }
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, 13, 16);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return TickerUtils.getTicker(type, TileRegistry.RUNIC_ALTAR_TILE.get(), RunicAltarTile::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RunicAltarTile(pos, state);
    }
}