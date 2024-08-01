package it.hurts.sskirillss.relics.client.screen.base;

import net.minecraft.world.item.ItemStack;

public interface IRelicScreenProvider {
    ItemStack getStack();

    int getContainer();

    int getSlot();
}