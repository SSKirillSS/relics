package it.hurts.sskirillss.relics.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class SingletonInventory implements IItemHandlerModifiable {
    private final ItemStack stack;

    public SingletonInventory(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {

    }

    @Override
    public int getSlots() {
        return 5;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return stack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }
}