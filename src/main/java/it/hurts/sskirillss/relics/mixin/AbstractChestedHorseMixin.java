package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.items.relics.HorseFluteItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractChestedHorse.class)
public class AbstractChestedHorseMixin {
    @Inject(at = @At(value = "HEAD"), method = "mobInteract", cancellable = true)
    protected void onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
        AbstractChestedHorse horse = (AbstractChestedHorse) (Object) this;
        ItemStack stack = player.getItemInHand(hand);

        if (!(stack.getItem() instanceof HorseFluteItem item))
            return;

        CompoundTag nbt = stack.getTagElement(HorseFluteItem.TAG_ENTITY);

        if (nbt != null) {
            item.releaseHorse(stack, player);
            item.catchHorse(horse, player, stack);
        }

        item.catchHorse(horse, player, stack);

        info.setReturnValue(InteractionResult.SUCCESS);
    }
}