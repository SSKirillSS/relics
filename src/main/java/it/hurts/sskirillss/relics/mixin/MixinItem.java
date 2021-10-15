package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.items.relics.base.handlers.ContractHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class MixinItem {
    @Inject(at = @At(value = "HEAD"), method = "inventoryTick", cancellable = true)
    protected void handleContract(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CallbackInfo info) {
        if (entityIn instanceof PlayerEntity)
            ContractHandler.handleOwner((PlayerEntity) entityIn, stack);
    }
}