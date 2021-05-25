package it.hurts.sskirillss.relics.crafting;

import com.google.common.collect.AbstractIterator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.function.Supplier;

public class ItemHandlerWrapper implements IInventory, Iterable<ItemStack> {
    protected final IItemHandlerModifiable inner;

    @Nullable
    protected final Supplier<Vector3d> location;
    protected final int distance;

    public ItemHandlerWrapper(IItemHandlerModifiable inner) {
        this(inner, null, 0);
    }

    public ItemHandlerWrapper(IItemHandlerModifiable inner, @Nullable Supplier<Vector3d> location, int distance) {
        this.inner = inner;
        this.distance = distance;
        this.location = location;
    }

    @Override
    public int getContainerSize() {
        return inner.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < inner.getSlots(); i++) if (inner.getStackInSlot(i).getCount() > 0) return false;
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return inner.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int amount) {
        return inner.extractItem(index, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return inner.extractItem(index, 64, false);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        inner.setStackInSlot(index, stack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        if (location == null) return true;
        return player.position().distanceTo(location.get()) <= distance;
    }

    public IItemHandlerModifiable getInner() {
        return inner;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return new AbstractIterator<ItemStack>() {
            int current = 0;

            @Override
            protected ItemStack computeNext() {
                if (current >= getContainerSize()) return endOfData();
                return getItem(current++);
            }
        };
    }

    @Override
    public void clearContent() {

    }
}