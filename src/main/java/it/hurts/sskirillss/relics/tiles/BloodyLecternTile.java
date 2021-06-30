package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.blocks.BloodyLecternBlock;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Random;

public class BloodyLecternTile extends TileBase implements ITickableTileEntity {
    private ItemStack stack = ItemStack.EMPTY;
    public int ticksExisted;

    public BloodyLecternTile() {
        super(TileRegistry.BLOODY_LECTERN_TILE.get());
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
        ItemStack stack = getStack();
        if (stack.isEmpty() || stack.getItem() != ItemRegistry.RELIC_CONTRACT.get()) return;
        int blood = NBTUtils.getInt(stack, RelicContractItem.TAG_BLOOD, 0) + 1;
        if (blood == 0) return;
        Random random = level.getRandom();
        BlockPos pos = this.getBlockPos();
        for (int i = 0; i < blood; i++)
            if (level.getRandom().nextInt(3) == 0) level.addParticle(new CircleTintData(new Color(255, 0, 0),
                            random.nextFloat() * 0.025F + 0.04F, 20, 0.94F, true),
                    pos.getX() + 0.5F + MathUtils.randomFloat(random) * 0.3F, pos.getY() + 0.95F,
                    pos.getZ() + 0.5F + MathUtils.randomFloat(random) * 0.3F, 0, random.nextFloat() * 0.05D, 0);
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