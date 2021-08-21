package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    @Inject(at = @At("RETURN"), method = "decreaseAirSupply", cancellable = true)
    protected void calculateAir(int air, CallbackInfoReturnable<Integer> info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.AMPHIBIAN_BOOT.get(), entity).isPresent()
                && entity.getRandom().nextInt(2) == 0) info.setReturnValue(info.getReturnValue() + 1);
    }
}