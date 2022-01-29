package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.blocks.PedestalBlock;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.Random;

public class PedestalTile extends TileBase {
    private ItemStack stack = ItemStack.EMPTY;
    public int ticksExisted;

    public PedestalTile(BlockPos pos, BlockState state){
        super(TileRegistry.PEDESTAL_TILE.get(), pos, state);
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PedestalTile tile) {
        if (level == null)
            return;

        tile.ticksExisted++;

        if (tile.stack != null && !tile.stack.isEmpty() && tile.ticksExisted % 3 == 0) {
            Random random = level.getRandom();

            CircleTintData particle = new CircleTintData(tile.stack.getRarity().color.getColor() != null ? new Color(tile.stack.getRarity().color.getColor(),
                    false) : new Color(255, 255, 255), random.nextFloat() * 0.025F + 0.04F, 20, 0.94F, true);

            Vec3 vec = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            Direction direction = tile.getBlockState().getValue(PedestalBlock.DIRECTION);

            double motionX = 0.0D, motionY = 0.0D, motionZ = 0.0D;
            double x = vec.x(), y = vec.y(), z = vec.z();

            if (direction == Direction.UP) {
                x = vec.x() + MathUtils.randomFloat(random) * 0.175F;
                y = vec.y() - 0.25D;
                z = vec.z() + MathUtils.randomFloat(random) * 0.175F;

                motionY = random.nextFloat() * 0.05D;
            } else if (direction == Direction.DOWN) {
                x = vec.x() + MathUtils.randomFloat(random) * 0.175F;
                y = vec.y() + 0.25D;
                z = vec.z() + MathUtils.randomFloat(random) * 0.175F;

                motionY = -random.nextFloat() * 0.05D;
            } else if (direction == Direction.NORTH) {
                x = vec.x() + MathUtils.randomFloat(random) * 0.175F;
                y = vec.y() + MathUtils.randomFloat(random) * 0.175F;
                z = vec.z() + 0.25D;

                motionZ = -random.nextFloat() * 0.05D;
            } else if (direction == Direction.SOUTH) {
                x = vec.x() + MathUtils.randomFloat(random) * 0.175F;
                y = vec.y() + MathUtils.randomFloat(random) * 0.175F;
                z = vec.z() - 0.25D;

                motionZ = random.nextFloat() * 0.05D;
            } else if (direction == Direction.EAST) {
                x = vec.x() - 0.25D;
                y = vec.y() + MathUtils.randomFloat(random) * 0.175F;
                z = vec.z() + MathUtils.randomFloat(random) * 0.175F;

                motionX = random.nextFloat() * 0.05D;
            } else if (direction == Direction.WEST) {
                x = vec.x() + 0.25D;
                y = vec.y() + MathUtils.randomFloat(random) * 0.175F;
                z = vec.z() + MathUtils.randomFloat(random) * 0.175F;

                motionX = -random.nextFloat() * 0.05D;
            }

            level.addParticle(particle, x, y, z, motionX, motionY, motionZ);
        }
    }

    @Override
    public void load(CompoundTag compound) {
        stack = ItemStack.of((CompoundTag) compound.get("itemStack"));

        super.load(compound);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        if (stack != null) {
            CompoundTag itemStack = new CompoundTag();

            stack.save(itemStack);

            compound.put("itemStack", itemStack);
        }

        super.saveAdditional(compound);
    }
}