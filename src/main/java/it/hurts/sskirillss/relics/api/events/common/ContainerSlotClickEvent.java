package it.hurts.sskirillss.relics.api.events.common;

import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ContainerSlotClickEvent extends PlayerContainerEvent {
    @Getter
    private final Slot slot;
    @Getter
    private final ItemStack heldStack;
    @Getter
    private final ItemStack slotStack;


    @Getter
    private final ClickAction action;

    public ContainerSlotClickEvent(Player player, AbstractContainerMenu container, Slot slot, ClickAction action, ItemStack heldStack, ItemStack slotStack) {
        super(player, container);

        this.slot = slot;
        this.action = action;
        this.heldStack = heldStack;
        this.slotStack = slotStack;
    }
}