package it.hurts.sskirillss.relics.blocks;

import it.hurts.sskirillss.relics.client.screen.description.RelicDescriptionScreen;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.tiles.ResearchingTableTile;
import it.hurts.sskirillss.relics.utils.TickerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResearchingTableBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public ResearchingTableBlock() {
        super(Properties.of(Material.WOOD)
                .lightLevel((s) -> 15)
                .strength(1.5F)
                .sound(SoundType.WOOD)
                .noOcclusion());
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && worldIn.getBlockEntity(pos) instanceof ResearchingTableTile tile) {
            ItemStack stack = tile.getStack();

            if (stack != null && !stack.isEmpty())
                worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @OnlyIn(Dist.CLIENT)
    public static void openGui(BlockPos pos) {
        Minecraft.getInstance().setScreen(new RelicDescriptionScreen(pos));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, 15, 16);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return TickerUtils.getTicker(type, TileRegistry.RESEARCHING_TABLE.get(), ResearchingTableTile::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResearchingTableTile(pos, state);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
            InteractionHand hand = event.getHand();

            if (hand != InteractionHand.MAIN_HAND)
                return;

            Level level = event.getWorld();
            BlockPos pos = event.getPos();

            if (!(level.getBlockEntity(pos) instanceof ResearchingTableTile tile))
                return;

            Player player = event.getPlayer();

            ItemStack handStack = player.getMainHandItem();
            ItemStack tileStack = tile.getStack();

            BlockState oldState = level.getBlockState(pos);

            if (tileStack.isEmpty()) {
                if (!(handStack.getItem() instanceof IRelicItem))
                    return;

                tile.setStack(handStack.split(1));
            } else {
                if (player.isShiftKeyDown()) {
                    if (!(tileStack.getItem() instanceof IRelicItem relic))
                        return;

                    relic.setItemResearched(player, true);

                    if (level.isClientSide())
                        openGui(pos);
                } else {
                    level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), tileStack));

                    tile.setStack(ItemStack.EMPTY);
                }
            }

            tile.setChanged();

            level.sendBlockUpdated(pos, oldState, level.getBlockState(pos), 3);
        }
    }
}