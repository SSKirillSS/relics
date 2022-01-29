package it.hurts.sskirillss.relics.blocks;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.tiles.BloodyLecternTile;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.TickerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BloodyLecternBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BloodyLecternBlock() {
        super(Block.Properties.of(Material.STONE)
                .strength(4.0F)
                .noOcclusion());

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn != InteractionHand.MAIN_HAND)
            return InteractionResult.FAIL;

        BloodyLecternTile lectern = (BloodyLecternTile) world.getBlockEntity(pos);
        ItemStack heldStack = player.getItemInHand(handIn);
        Random random = world.getRandom();

        if (lectern.getStack().isEmpty()) {
            if (heldStack.getItem() == ItemRegistry.COAL_PARCHMENT.get()
                    || heldStack.getItem() == ItemRegistry.RELIC_CONTRACT.get())
                lectern.setStack(heldStack.split(1));
        } else {
            ItemStack contract = lectern.getStack();
            if ((contract.getItem() == ItemRegistry.COAL_PARCHMENT.get() || contract.getItem() == ItemRegistry.RELIC_CONTRACT.get())
                    && heldStack.getItem() == ItemRegistry.BLOODY_FEATHER.get()) {
                if (world.isClientSide())
                    return InteractionResult.FAIL;

                int blood = NBTUtils.getInt(contract, RelicContractItem.TAG_BLOOD, -1);
                String owner = RelicUtils.Owner.getOwnerUUID(contract);

                if (player.getCooldowns().isOnCooldown(heldStack.getItem()) || blood >= 4)
                    return InteractionResult.FAIL;

                if (contract.getItem() == ItemRegistry.COAL_PARCHMENT.get()) {
                    ItemStack parchment = new ItemStack(ItemRegistry.RELIC_CONTRACT.get());

                    RelicUtils.Owner.setOwnerUUID(parchment, player.getStringUUID());
                    lectern.setStack(parchment);
                } else if (owner.equals("") || player.getStringUUID().equals(owner)) {
                    blood++;

                    NBTUtils.setInt(contract, RelicContractItem.TAG_BLOOD, blood);
                    RelicUtils.Owner.setOwnerUUID(contract, player.getStringUUID());

                    if (blood == 4)
                        ((ServerLevel) world).sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5F,
                                pos.getY() + 0.75F, pos.getZ() + 0.5F, 1, 0, 0, 0, 0);
                } else
                    return InteractionResult.FAIL;

                player.hurt(DamageSource.GENERIC, player.getMaxHealth() * 0.1F);
                player.getCooldowns().addCooldown(heldStack.getItem(), 10);
                world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1F - random.nextFloat() * 0.5F);
                world.sendBlockUpdated(pos, state, state, 2);
            } else {
                if (heldStack.isEmpty())
                    player.setItemInHand(InteractionHand.MAIN_HAND, contract);
                else {
                    ItemEntity drop = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), lectern.getStack());

                    drop.setPickUpDelay(0);
                    world.addFreshEntity(drop);
                }

                lectern.setStack(ItemStack.EMPTY);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof BloodyLecternTile) {
                ItemStack stack = ((BloodyLecternTile) tile).getStack();
                if (stack != null && !stack.isEmpty())
                    worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        if (direction == Direction.NORTH)
            return LecternBlock.SHAPE_NORTH;
        else if (direction == Direction.SOUTH)
            return LecternBlock.SHAPE_SOUTH;
        else if (direction == Direction.EAST)
            return LecternBlock.SHAPE_EAST;
        else if (direction == Direction.WEST)
            return LecternBlock.SHAPE_WEST;
        else
            return LecternBlock.SHAPE_COMMON;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return TickerUtils.getTicker(type, TileRegistry.BLOODY_LECTERN_TILE.get(), BloodyLecternTile::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloodyLecternTile(pos, state);
    }
}