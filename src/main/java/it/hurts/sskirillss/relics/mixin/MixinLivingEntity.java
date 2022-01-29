package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.feet.AmphibianBootItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    @Inject(at = @At(value = "RETURN"), method = "decreaseAirSupply", cancellable = true)
    protected void calculateAir(int air, CallbackInfoReturnable<Integer> info) {
        AmphibianBootItem.Stats config = AmphibianBootItem.INSTANCE.getStats();
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!EntityUtils.findEquippedCurio(entity, ItemRegistry.AMPHIBIAN_BOOT.get()).isEmpty()
                && entity.getRandom().nextInt(config.airSupplyModifier) == 0)
            info.setReturnValue(info.getReturnValue() + 1);
    }

    @Inject(at = @At("HEAD"), method = "checkTotemDeathProtection", cancellable = true)
    protected void preventTotemUse(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!EntityUtils.findEquippedCurio(entity, ItemRegistry.DELAY_RING.get()).isEmpty())
            info.setReturnValue(false);
    }
}