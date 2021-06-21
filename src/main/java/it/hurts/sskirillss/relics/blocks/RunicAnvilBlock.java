package it.hurts.sskirillss.relics.blocks;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.items.RelicScrapItem;
import it.hurts.sskirillss.relics.particles.spark.SparkTintData;
import it.hurts.sskirillss.relics.tiles.RunicAnvilTile;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Random;

public class RunicAnvilBlock extends FallingBlock {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    public RunicAnvilBlock() {
        super(Block.Properties.of(Material.STONE).strength(4.0F).harvestTool(ToolType.PICKAXE).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (handIn != Hand.MAIN_HAND || hit.getDirection() != Direction.UP || hit.getLocation().y() - pos.getY() < 0.75F) return ActionResultType.FAIL;
        RunicAnvilTile anvil = (RunicAnvilTile) world.getBlockEntity(pos);
        ItemStack heldStack = player.getMainHandItem();
        Random random = world.getRandom();
        if (anvil.getStack().isEmpty()) {
            ItemStack stack = player.getMainHandItem();
            if (stack.isEmpty() || !(stack.getItem() instanceof RelicItem)) return ActionResultType.FAIL;
            anvil.setStack(heldStack.split(1));
        } else {
            ItemStack relic = anvil.getStack();
            if (relic.getItem() instanceof RelicItem && heldStack.getItem() == ItemRegistry.RUNIC_HAMMER.get()) {
                ItemStack offhandStack = player.getOffhandItem();
                if (player.getCooldowns().isOnCooldown(heldStack.getItem())) return ActionResultType.FAIL;
                int durability = RelicUtils.Durability.getDurability(relic);
                if (offhandStack.getItem() instanceof RelicScrapItem) {
                    if (durability >= RelicUtils.Durability.getMaxDurability(relic.getItem())) return ActionResultType.FAIL;
                    RelicUtils.Durability.addDurability(relic, ((RelicScrapItem) offhandStack.getItem()).getReplenishedVolume());
                    offhandStack.shrink(1);
                } else RelicUtils.Durability.takeDurability(relic, 5);
                heldStack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
                player.getCooldowns().addCooldown(heldStack.getItem(), 20);
                world.playSound(null, pos, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.BLOCKS, 1F, 1F - random.nextFloat() * 0.5F);
                for (int i = 0; i < random.nextInt(20) + 20; i++)
                    world.addParticle(new SparkTintData(new Color(255, 255 - random.nextInt(100), 0), 0.025F + random.nextFloat() * 0.05F,
                                    10 - random.nextInt(10)), hit.getLocation().x() + MathUtils.randomFloat(random) * 0.075F,
                            hit.getLocation().y() + 0.05F, hit.getLocation().z() + MathUtils.randomFloat(random) * 0.075F,
                            MathUtils.randomFloat(random) * 0.02F, random.nextFloat() * 0.05F, MathUtils.randomFloat(random) * 0.02F);
            } else {
                if (heldStack.isEmpty()) player.setItemInHand(Hand.MAIN_HAND, relic);
                else {
                    ItemEntity drop = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), anvil.getStack());
                    drop.setPickUpDelay(0);
                    world.addFreshEntity(drop);
                }
                anvil.setStack(ItemStack.EMPTY);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof RunicAnvilTile) {
                ItemStack stack = ((RunicAnvilTile) tile).getStack();
                if (stack != null && !stack.isEmpty())
                    worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
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
}