package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(EndermanEntity.class)
public class MixinEndermanEntity {
    @Inject(at = @At("HEAD"), method = "isLookingAtMe", cancellable = true)
    protected void calmEndermans(PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
        if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ENDERS_HAND.get(), player).isPresent()) info.setReturnValue(false);
    }
}