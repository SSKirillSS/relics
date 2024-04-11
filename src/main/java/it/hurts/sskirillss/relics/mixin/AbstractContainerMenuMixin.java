package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.api.events.common.ContainerSlotClickEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Inject(at = @At(value = "HEAD"), method = "doClick", cancellable = true)
    protected void onClick(int index, int action, ClickType clickType, Player player, CallbackInfo ci) {
        if (clickType != ClickType.PICKUP || index < 0)
            return;

        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        Slot slot = menu.slots.get(index);

        if (canInteract(slot, player, menu)) {
            ContainerSlotClickEvent event = new ContainerSlotClickEvent(player, menu, slot, action == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY, menu.getCarried(), slot.getItem());

            MinecraftForge.EVENT_BUS.post(event);

            if (event.isCanceled())
                ci.cancel();
        }
    }

    private boolean canInteract(Slot slot, Player player, AbstractContainerMenu menu) {
        ItemStack heldStack = menu.getCarried();

        return !(slot instanceof ResultSlot) && slot.allowModification(player)
                && menu.canTakeItemForPickAll(heldStack, slot) && slot.isActive();
    }
}