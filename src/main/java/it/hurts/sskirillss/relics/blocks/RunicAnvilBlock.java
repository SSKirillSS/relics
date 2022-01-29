package it.hurts.sskirillss.relics.blocks;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.tiles.RunicAnvilTile;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RunicAnvilBlock extends FallingBlock implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public RunicAnvilBlock() {
        super(Block.Properties
                .of(Material.HEAVY_METAL)
                .sound(SoundType.ANVIL)
                .strength(4.0F)
                .noOcclusion());

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn != InteractionHand.MAIN_HAND || hit.getDirection() != Direction.UP
                || hit.getLocation().y() - pos.getY() < 0.75F)
            return InteractionResult.FAIL;

        BlockEntity tile = world.getBlockEntity(pos);

        if (!(tile instanceof RunicAnvilTile))
            return InteractionResult.FAIL;

        RunicAnvilTile anvil = (RunicAnvilTile) tile;
        ItemStack heldStack = player.getItemInHand(handIn);

        if (heldStack.isEmpty()) {
            if (!anvil.takeItem(player))
                return InteractionResult.FAIL;
        } else {
            if (heldStack.getItem() == ItemRegistry.RUNIC_HAMMER.get()) {
                List<ItemStack> items = anvil.getItems();

                if (player.getCooldowns().isOnCooldown(heldStack.getItem()) || items.size() != 1)
                    return InteractionResult.FAIL;

                ItemStack stack = items.get(0);

                if (stack.getMaxDamage() == 0)
                    return InteractionResult.FAIL;

                int expCost = 1 + (50 / stack.getMaxDamage());

                if (!(stack.getItem() instanceof RelicItem) || !stack.isDamaged()
                        || player.totalExperience < expCost)
                    return InteractionResult.FAIL;

                player.giveExperiencePoints(-expCost);
                heldStack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                player.getCooldowns().addCooldown(heldStack.getItem(), 10);

                stack.setDamageValue(Math.max(0, stack.getDamageValue() - (stack.getDamageValue() / 10 + 1)));

                world.playSound(null, pos, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.BLOCKS,
                        1F, 1F - world.getRandom().nextFloat() * 0.2F);

                Vec3 animationVec = new Vec3(pos.getX() + 0.5F, pos.getY() + 0.85F, pos.getZ() + 0.5F);
                world.addParticle(ParticleTypes.EXPLOSION, animationVec.x(), animationVec.y(),
                        animationVec.z(), 0F, 0F, 0F);
                ParticleUtils.createBall(ParticleTypes.LAVA, animationVec, world, 1, 0.4F);
            } else {
                if (!anvil.insertItem(heldStack))
                    return InteractionResult.FAIL;
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tile = worldIn.getBlockEntity(pos);

            if (tile instanceof RunicAnvilTile) {
                List<ItemStack> items = ((RunicAnvilTile) tile).getItems();

                items.forEach(stack -> worldIn.addFreshEntity(new ItemEntity(worldIn,
                        pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, stack)));
            }
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        VoxelShape BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

        VoxelShape X_AXIS_AABB = Shapes.or(BASE,
                Block.box(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D),
                Block.box(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D),
                Block.box(0.0D, 10.0D, 3.0D, 16.0D, 15.0D, 13.0D));
        VoxelShape Z_AXIS_AABB = Shapes.or(BASE,
                Block.box(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D),
                Block.box(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D),
                Block.box(3.0D, 10.0D, 0.0D, 13.0D, 15.0D, 16.0D));

        return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected void falling(FallingBlockEntity entity) {
        entity.setHurtsEntities(2.0F, 40);
    }

    @Override
    public void onLand(Level world, BlockPos pos, BlockState oldState, BlockState newState, FallingBlockEntity entity) {
        if (!entity.isSilent())
            world.levelEvent(1031, pos, 0);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RunicAnvilTile(pos, state);
    }
}