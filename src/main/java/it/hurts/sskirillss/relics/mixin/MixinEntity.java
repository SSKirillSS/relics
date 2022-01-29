package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(at = @At(value = "RETURN"), method = "isInWaterOrRain", cancellable = true)
    protected void setWet(CallbackInfoReturnable<Boolean> info) {
        Entity entity = (Entity) (Object) this;

        if (!(entity instanceof LivingEntity))
            return;

        if (!EntityUtils.findEquippedCurio((LivingEntity) entity, ItemRegistry.DROWNED_BELT.get()).isEmpty())
            info.setReturnValue(true);
    }
}