package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.blocks.PedestalBlock;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.awt.*;
import java.util.Random;

public class PedestalTile extends TileBase implements ITickableTileEntity {
    private ItemStack stack = ItemStack.EMPTY;
    public int ticksExisted;

    public PedestalTile() {
        super(TileRegistry.PEDESTAL_TILE.get());
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void tick() {
        if (level == null) return;
        ticksExisted++;
        if (RelicsConfig.Pedestal.SPAWN_PARTICLES.get() && stack != null && !stack.isEmpty() && this.ticksExisted % 3 == 0) {
            Random random = level.getRandom();
            CircleTintData particle = new CircleTintData(stack.getRarity().color.getColor() != null ? new Color(stack.getRarity().color.getColor(),
                    false) : new Color(255, 255, 255), random.nextFloat() * 0.025F + 0.04F, 20, 0.94F, true);
            BlockPos blockPos = this.getBlockPos();
            Vector3d pos = new Vector3d(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D);
            Direction direction = this.getBlockState().getValue(PedestalBlock.DIRECTION);
            double motionX = 0.0D, motionY = 0.0D, motionZ = 0.0D;
            double x = pos.x(), y = pos.y(), z = pos.z();
            if (direction == Direction.UP) {
                x = pos.x() + MathUtils.randomFloat(random) * 0.175F;
                y = pos.y() - 0.25D;
                z = pos.z() + MathUtils.randomFloat(random) * 0.175F;
                motionY = random.nextFloat() * 0.05D;
            } else if (direction == Direction.DOWN) {
                x = pos.x() + MathUtils.randomFloat(random) * 0.175F;
                y = pos.y() + 0.25D;
                z = pos.z() + MathUtils.randomFloat(random) * 0.175F;
                motionY = -random.nextFloat() * 0.05D;
            } else if (direction == Direction.NORTH) {
                x = pos.x() + MathUtils.randomFloat(random) * 0.175F;
                y = pos.y() + MathUtils.randomFloat(random) * 0.175F;
                z = pos.z() + 0.25D;
                motionZ = -random.nextFloat() * 0.05D;
            } else if (direction == Direction.SOUTH) {
                x = pos.x() + MathUtils.randomFloat(random) * 0.175F;
                y = pos.y() + MathUtils.randomFloat(random) * 0.175F;
                z = pos.z() - 0.25D;
                motionZ = random.nextFloat() * 0.05D;
            } else if (direction == Direction.EAST) {
                x = pos.x() - 0.25D;
                y = pos.y() + MathUtils.randomFloat(random) * 0.175F;
                z = pos.z() + MathUtils.randomFloat(random) * 0.175F;
                motionX = random.nextFloat() * 0.05D;
            } else if (direction == Direction.WEST) {
                x = pos.x() + 0.25D;
                y = pos.y() + MathUtils.randomFloat(random) * 0.175F;
                z = pos.z() + MathUtils.randomFloat(random) * 0.175F;
                motionX = -random.nextFloat() * 0.05D;
            }
            level.addParticle(particle, x, y, z, motionX, motionY, motionZ);
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        stack = ItemStack.of((CompoundNBT) compound.get("itemStack"));
        super.load(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if (stack != null) {
            CompoundNBT itemStack = new CompoundNBT();
            stack.save(itemStack);
            compound.put("itemStack", itemStack);
        }
        return super.save(compound);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getBlockPos(), -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.load(getBlockState(), packet.getTag());
    }
}