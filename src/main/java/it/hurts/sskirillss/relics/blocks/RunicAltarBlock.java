package it.hurts.sskirillss.relics.blocks;

import com.google.common.collect.ImmutableList;
import it.hurts.sskirillss.relics.crafting.RunicAltarContext;
import it.hurts.sskirillss.relics.crafting.RunicAltarRecipe;
import it.hurts.sskirillss.relics.crafting.SingletonInventory;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.items.RuneItem;
import it.hurts.sskirillss.relics.tiles.RunicAltarTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RunicAltarBlock extends Block {
    public RunicAltarBlock() {
        super(Block.Properties.of(Material.STONE).strength(3.0F).harvestTool(ToolType.PICKAXE).lightLevel((state) -> 5).noOcclusion());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return ImmutableList.of(new ItemStack(state.getBlock()));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (handIn != Hand.MAIN_HAND) return ActionResultType.FAIL;
        RunicAltarTile altar = (RunicAltarTile) world.getBlockEntity(pos);
        if (altar == null) return ActionResultType.FAIL;
        Direction direction = hit.getDirection();
        if (direction == Direction.DOWN) return ActionResultType.FAIL;
        ItemStack handStack = player.getItemInHand(handIn);
        if (altar.getCraftingProgress() != 0) return ActionResultType.FAIL;
        ItemStack stack = altar.getStack(direction);
        if (stack == null) return ActionResultType.FAIL;
        if (stack.isEmpty()) {
            if (handStack.isEmpty()) return ActionResultType.FAIL;
            if (direction == Direction.UP && !(handStack.getItem() instanceof RelicItem)) return ActionResultType.FAIL;
            if (direction != Direction.UP && !(handStack.getItem() instanceof RuneItem)) return ActionResultType.FAIL;
            altar.setStack(handStack.split(1), direction);
            Optional<RunicAltarRecipe> optional = world.getRecipeManager().getRecipeFor(RunicAltarRecipe.RECIPE, new RunicAltarContext(
                    new SingletonInventory(altar.getStack(Direction.UP)), player, altar.getRunes(), altar.getStack(Direction.UP)), world);
            if (!optional.isPresent()) return ActionResultType.FAIL;
            altar.addCraftingProgress(1);
        } else {
            if (player.getMainHandItem().isEmpty()) player.setItemInHand(Hand.MAIN_HAND, stack);
            else {
                ItemEntity drop = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), altar.getStack(direction));
                drop.setPickUpDelay(0);
                world.addFreshEntity(drop);
            }
            altar.setStack(ItemStack.EMPTY, direction);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity tile = worldIn.getBlockEntity(pos);
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
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Block.box(0, 0, 0, 16, 13, 16);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RunicAltarTile();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}