package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(at = @At("RETURN"), method = "isInWaterOrRain", cancellable = true)
    protected void setWet(CallbackInfoReturnable<Boolean> info) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DROWNED_BELT.get(),
                (LivingEntity) entity).isPresent()) info.setReturnValue(true);
    }
}