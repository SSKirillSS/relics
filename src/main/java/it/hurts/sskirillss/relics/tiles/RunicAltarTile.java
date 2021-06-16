package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.RuneItem;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RunicAltarTile extends TileBase implements ITickableTileEntity {
    private ItemStack relicStack = ItemStack.EMPTY;
    private ItemStack eastStack = ItemStack.EMPTY;
    private ItemStack westStack = ItemStack.EMPTY;
    private ItemStack southStack = ItemStack.EMPTY;
    private ItemStack northStack = ItemStack.EMPTY;
    public int ticksExisted;
    private int progress;

    public static final Direction[] runeDirections = {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

    public RunicAltarTile() {
        super(TileRegistry.RUNIC_ALTAR_TILE.get());
    }

    public void setStack(ItemStack stack, Direction direction) {
        switch (direction) {
            case NORTH:
                this.northStack = stack;
                break;
            case WEST:
                this.westStack = stack;
                break;
            case EAST:
                this.eastStack = stack;
                break;
            case SOUTH:
                this.southStack = stack;
                break;
            case UP:
                this.relicStack = stack;
                break;
        }
    }

    public ItemStack getStack(Direction direction) {
        switch (direction) {
            case NORTH:
                return this.northStack;
            case WEST:
                return this.westStack;
            case EAST:
                return this.eastStack;
            case SOUTH:
                return this.southStack;
            case UP:
                return this.relicStack;
            default:
                return ItemStack.EMPTY;
        }
    }

    public List<ItemStack> getRunes() {
        return Arrays.stream(runeDirections).map(this::getStack).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
    }

    public void addCraftingProgress(int progress) {
        setCraftingProgress(this.progress + progress);
    }

    public void setCraftingProgress(int progress) {
        this.progress = Math.min(100, progress);
    }

    public int getCraftingProgress() {
        return progress;
    }

    @Override
    public void tick() {
        if (level == null) return;
        ticksExisted++;
        Random random = level.getRandom();
        BlockPos pos = this.getBlockPos();
        if (relicStack.isEmpty() || getCraftingProgress() == 0) return;
        level.addParticle(new CircleTintData(relicStack.getRarity().color.getColor() != null ? new Color(relicStack.getRarity().color.getColor(),
                        false) : new Color(255, 255, 255), random.nextFloat() * 0.025F + 0.04F, 20, 0.94F, true),
                pos.getX() + 0.5F + MathUtils.randomFloat(random) * 0.2F, pos.getY() + 0.85F,
                pos.getZ() + 0.5F + MathUtils.randomFloat(random) * 0.2F, 0, random.nextFloat() * 0.05D, 0);
        if (ticksExisted % 20 != 0 || getRunes().isEmpty()) return;
        if (!level.isClientSide() && random.nextInt(3) == 0) RelicUtils.Durability.takeDurability(relicStack, 1);
        for (ItemStack stack : getRunes()) {
            if (!(stack.getItem() instanceof RuneItem)) continue;
            RuneItem rune = (RuneItem) stack.getItem();
            rune.applyAbility(level, pos);
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        relicStack = ItemStack.of((CompoundNBT) compound.get("relicStack"));
        eastStack = ItemStack.of((CompoundNBT) compound.get("eastStack"));
        westStack = ItemStack.of((CompoundNBT) compound.get("westStack"));
        southStack = ItemStack.of((CompoundNBT) compound.get("southStack"));
        northStack = ItemStack.of((CompoundNBT) compound.get("northStack"));
        progress = compound.getInt("progress");
        super.load(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if (relicStack != null) {
            CompoundNBT compoundNBT = new CompoundNBT();
            relicStack.save(compoundNBT);
            compound.put("relicStack", compoundNBT);
        }
        if (eastStack != null) {
            CompoundNBT compoundNBT = new CompoundNBT();
            eastStack.save(compoundNBT);
            compound.put("eastStack", compoundNBT);
        }
        if (westStack != null) {
            CompoundNBT compoundNBT = new CompoundNBT();
            westStack.save(compoundNBT);
            compound.put("westStack", compoundNBT);
        }
        if (southStack != null) {
            CompoundNBT compoundNBT = new CompoundNBT();
            southStack.save(compoundNBT);
            compound.put("southStack", compoundNBT);
        }
        if (northStack != null) {
            CompoundNBT compoundNBT = new CompoundNBT();
            northStack.save(compoundNBT);
            compound.put("northStack", compoundNBT);
        }
        compound.putInt("progress", getCraftingProgress());
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