package it.hurts.sskirillss.relics.blocks;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.tiles.BloodyLecternTile;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BloodyLecternBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    public BloodyLecternBlock() {
        super(Block.Properties.of(Material.STONE).strength(4.0F).harvestTool(ToolType.PICKAXE).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (handIn != Hand.MAIN_HAND)
            return ActionResultType.FAIL;

        BloodyLecternTile lectern = (BloodyLecternTile) world.getBlockEntity(pos);
        ItemStack heldStack = player.getItemInHand(handIn);
        Random random = world.getRandom();

        if (!lectern.getStack().isEmpty()) {
            ItemStack contract = lectern.getStack();
            if ((contract.getItem() == ItemRegistry.COAL_PARCHMENT.get() || contract.getItem() == ItemRegistry.RELIC_CONTRACT.get()) && heldStack.getItem() == ItemRegistry.BLOODY_FEATHER.get()) {
                if (world.isClientSide())
                    return ActionResultType.FAIL;

                int blood = NBTUtils.getInt(contract, RelicContractItem.TAG_BLOOD, -1);
                String owner = RelicUtils.Owner.getOwnerUUID(contract);

                if (player.getCooldowns().isOnCooldown(heldStack.getItem()) || blood >= 4)
                    return ActionResultType.FAIL;

                if (contract.getItem() == ItemRegistry.COAL_PARCHMENT.get()) {
                    if (world.random.nextInt(3) == 0) {
                        ItemStack parchment = new ItemStack(ItemRegistry.RELIC_CONTRACT.get());
                        RelicUtils.Owner.setOwnerUUID(parchment, player.getStringUUID());
                        lectern.setStack(parchment);
                    }
                } else if (owner.isEmpty() || player.getStringUUID().equals(owner)) {
                    if (world.random.nextInt(3) == 0) {
                        blood++;
                        NBTUtils.setInt(contract, RelicContractItem.TAG_BLOOD, blood);
                        RelicUtils.Owner.setOwnerUUID(contract, player.getStringUUID());

                        if (blood == 4)
                            ((ServerWorld) world).sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, 1, 0, 0, 0, 0);
                    }
                } else {
                    return ActionResultType.FAIL;
                }

                player.hurt(DamageSource.GENERIC, player.getMaxHealth() * 0.1F);
                player.getCooldowns().addCooldown(heldStack.getItem(), 20);
                world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 1F - random.nextFloat() * 0.5F);
                world.sendBlockUpdated(pos, state, state, 2);
            } else {
                if (!heldStack.isEmpty()) {
                    ItemEntity drop = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), lectern.getStack());
                    drop.setPickUpDelay(0);
                    world.addFreshEntity(drop);
                } else {
                    player.setItemInHand(Hand.MAIN_HAND, contract);
                }

                lectern.setStack(ItemStack.EMPTY);
            }
        } else if (heldStack.getItem() == ItemRegistry.COAL_PARCHMENT.get() || heldStack.getItem() == ItemRegistry.RELIC_CONTRACT.get()) {
            lectern.setStack(heldStack.split(1));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof BloodyLecternTile) {
                ItemStack stack = ((BloodyLecternTile) tile).getStack();
                if (stack != null && !stack.isEmpty())
                    worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
            }
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        Direction direction = state.getValue(FACING);

        switch (direction) {
            case NORTH:
                return LecternBlock.SHAPE_NORTH;
            case SOUTH:
                return LecternBlock.SHAPE_SOUTH;
            case EAST:
                return LecternBlock.SHAPE_EAST;
            case WEST:
                return LecternBlock.SHAPE_WEST;
            default:
                return LecternBlock.SHAPE_COMMON;
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BloodyLecternTile();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}