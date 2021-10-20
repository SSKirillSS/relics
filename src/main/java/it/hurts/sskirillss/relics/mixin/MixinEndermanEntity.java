package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public class MixinEndermanEntity {
    @Inject(at = @At(value = "HEAD"), method = "isLookingAtMe", cancellable = true)
    protected void calmEndermans(PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
        if (!EntityUtils.findEquippedCurio(player, ItemRegistry.ENDERS_HAND.get()).isEmpty()) info.setReturnValue(false);
    }
}