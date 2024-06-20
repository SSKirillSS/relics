package it.hurts.sskirillss.relics.mixin;

import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractChestedHorse.class)
public class AbstractChestedHorseMixin {
//    @Inject(at = @At(value = "HEAD"), method = "mobInteract", cancellable = true)
//    protected void onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
//        AbstractChestedHorse horse = (AbstractChestedHorse) (Object) this;
//        ItemStack stack = player.getItemInHand(hand);
//
//        if (!(stack.getItem() instanceof HorseFluteItem item))
//            return;
//
//        CompoundTag nbt = NBTUtils.getOrCreateTag(stack).getCompound(HorseFluteItem.TAG_ENTITY);
//
//        if (nbt != null) {
//            item.releaseHorse(stack, player);
//            item.catchHorse(horse, player, stack);
//        }
//
//        item.catchHorse(horse, player, stack);
//
//        info.setReturnValue(InteractionResult.SUCCESS);
//    }
}