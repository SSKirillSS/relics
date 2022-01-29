package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.init.TileRegistry;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RunicAnvilTile extends TileBase {
    @Getter
    private final ItemStackHandler handler = createHandler();

    public RunicAnvilTile(BlockPos pos, BlockState state) {
        super(TileRegistry.RUNIC_ANVIL_TILE.get(), pos, state);
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

        return true;
    }

    public boolean takeItem(LivingEntity entity) {
        int slot = getLastItemSlot(true);

        if (slot == -1)
            return false;

        Vec3 vec = entity.position();
        Level world = entity.getCommandSenderWorld();

        ItemEntity item = new ItemEntity(world, vec.x(), vec.y(), vec.z(),
                handler.getStackInSlot(slot));
        item.setNoPickUpDelay();

        world.addFreshEntity(item);

        handler.extractItem(slot, 1, false);

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
    public void load(CompoundTag compound) {
        handler.deserializeNBT(compound.getCompound("items"));

        super.load(compound);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        compound.put("items", handler.serializeNBT());

        super.saveAdditional(compound);
    }
}