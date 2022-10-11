package it.hurts.sskirillss.relics.blocks;

import it.hurts.sskirillss.relics.client.screen.description.RelicDescriptionScreen;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.tiles.ResearchingTableTile;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import it.hurts.sskirillss.relics.utils.TickerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ResearchingTableBlock extends Block implements EntityBlock {
    public ResearchingTableBlock() {
        super(Properties.of(Material.WOOD)
                .strength(4.0F)
                .noOcclusion());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (handIn != InteractionHand.MAIN_HAND)
            return InteractionResult.FAIL;

        if (!(world.getBlockEntity(pos) instanceof ResearchingTableTile tile))
            return InteractionResult.FAIL;

        ItemStack handStack = player.getMainHandItem();
        Item handItem = handStack.getItem();

        ItemStack tileStack = tile.getStack();
        Item tileItem = tileStack.getItem();

        if (tileStack.isEmpty()) {
            if (handStack.isEmpty())
                return InteractionResult.FAIL;

            tile.setStack(handStack.split(1));
        } else {
            if (handItem == ItemRegistry.RUNIC_HAMMER.get()) {
                if (tileStack.getMaxDamage() == 0 || player.getAttackStrengthScale(1F) < 1F)
                    return InteractionResult.FAIL;

                int expCost = 1 + (50 / tileStack.getMaxDamage());

                if (!(tileStack.getItem() instanceof RelicItem) || !tileStack.isDamaged()
                        || player.totalExperience < expCost)
                    return InteractionResult.FAIL;

                player.giveExperiencePoints(-expCost);
                player.resetAttackStrengthTicker();

                handStack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));

                tileStack.setDamageValue(Math.max(0, tileStack.getDamageValue() - (tileStack.getDamageValue() / 10 + 1)));

                world.playSound(null, pos, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.BLOCKS,
                        1F, 1F - world.getRandom().nextFloat() * 0.2F);

                Vec3 animationVec = new Vec3(pos.getX() + 0.5F, pos.getY() + 0.85F, pos.getZ() + 0.5F);

                world.addParticle(ParticleTypes.EXPLOSION, animationVec.x(), animationVec.y(),
                        animationVec.z(), 0F, 0F, 0F);

                ParticleUtils.createBall(ParticleTypes.LAVA, animationVec, world, 1, 0.4F);
            } else {
                if (player.isShiftKeyDown()) {
                    try {
                        if (world.isClientSide())
                            Minecraft.getInstance().setScreen(new RelicDescriptionScreen(pos, tileStack));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tileStack));

                    tile.setStack(ItemStack.EMPTY);
                }
            }
        }

        return InteractionResult.SUCCESS;
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
}