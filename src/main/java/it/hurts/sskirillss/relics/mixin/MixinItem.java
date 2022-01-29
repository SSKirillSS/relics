package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.items.relics.base.handlers.ContractHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class MixinItem {
    @Inject(at = @At(value = "HEAD"), method = "inventoryTick", cancellable = true)
    protected void handleContract(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected, CallbackInfo info) {
        if (entityIn instanceof Player)
            ContractHandler.handleOwner((Player) entityIn, stack);
    }
}