package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Random;

public class RunicAnvilTile extends TileBase implements ITickableTileEntity {
    private ItemStack stack = ItemStack.EMPTY;
    public int ticksExisted;

    public RunicAnvilTile() {
        super(TileRegistry.RUNIC_ANVIL_TILE.get());
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void tick() {
        if (level == null || !(stack.getItem() instanceof RelicItem)) return;
        ticksExisted++;
        Random random = level.getRandom();
        BlockPos pos = this.getBlockPos();
        if (RelicUtils.Durability.getDurability(stack) <= 0) {
            setStack(RelicUtils.Durability.getScrap(stack));
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 2);
            level.addParticle(ParticleTypes.EXPLOSION, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0, 0, 0);
            level.playSound(null, pos, SoundEvents.WITHER_BREAK_BLOCK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        level.addParticle(new CircleTintData(stack.getRarity().color.getColor() != null ? new Color(stack.getRarity().color.getColor(),
                        false) : new Color(255, 255, 255), random.nextFloat() * 0.025F + 0.04F, 20, 0.94F, true),
                pos.getX() + 0.5F + MathUtils.randomFloat(random) * 0.2F, pos.getY() + 0.85F,
                pos.getZ() + 0.5F + MathUtils.randomFloat(random) * 0.2F, 0, random.nextFloat() * 0.05D, 0);
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