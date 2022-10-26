package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.init.TileRegistry;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RunicAnvilTile extends TileBase implements ITickableTileEntity {
    @Getter
    private final ItemStackHandler handler = createHandler();

    private int ticksExisted;

    public RunicAnvilTile() {
        super(TileRegistry.RUNIC_ANVIL_TILE.get());
    }

    @Override
    public void tick() {
        if (level == null)
            return;

        ticksExisted++;
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return super.isItemValid(slot, stack);
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public int getEmptySlot() {
        for (int i = 0; i < handler.getSlots(); i++)
            if (handler.getStackInSlot(i).isEmpty())
                return i;

        return -1;
    }

    public int getLastItemSlot(boolean inverted) {
        if (inverted) {
            for (int i = handler.getSlots() - 1; i > -1; i--)
                if (!handler.getStackInSlot(i).isEmpty())
                    return i;
        } else {
            for (int i = 0; i < handler.getSlots(); i++)
                if (!handler.getStackInSlot(i).isEmpty())
                    return i;
        }

        return -1;
    }

    public boolean insertItem(ItemStack stack) {
        int slot = getEmptySlot();

        if (slot == -1)
            return false;
        
        handler.insertItem(slot, stack.copy().split(1), false);

        stack.shrink(1);

        BlockPos pos = this.getBlockPos();

        if (level != null)
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 2);

        return true;
    }

    public boolean takeItem(LivingEntity entity) {
        int slot = getLastItemSlot(true);

        if (slot == -1)
            return false;

        Vector3d vec = entity.position();
        World world = entity.getCommandSenderWorld();

        ItemEntity item = new ItemEntity(world, vec.x(), vec.y(), vec.z(),
                handler.getStackInSlot(slot));
        item.setNoPickUpDelay();

        world.addFreshEntity(item);

        handler.extractItem(slot, 1, false);

        BlockPos pos = this.getBlockPos();

        if (level != null)
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 2);

        return true;
    }

    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);

            if (!stack.isEmpty())
                items.add(stack);
        }

        return items;
    }

    @Override
    public void load(@NotNull BlockState state, CompoundNBT compound) {
        handler.deserializeNBT(compound.getCompound("items"));

        super.load(state, compound);
    }

    @Override
    public @NotNull CompoundNBT save(CompoundNBT compound) {
        compound.put("items", handler.serializeNBT());

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