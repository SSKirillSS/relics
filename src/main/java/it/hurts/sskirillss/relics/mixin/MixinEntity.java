package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(at = @At("RETURN"), method = "isInWaterOrRain", cancellable = true)
    protected void setWet(CallbackInfoReturnable<Boolean> info) {
        Entity entity = (Entity) (Object) this;

        if (entity instanceof LivingEntity)
            return;

        Optional<ImmutableTriple<String, Integer, ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DROWNED_BELT.get(),
                (LivingEntity) entity);

        if (optional.isPresent() && !RelicItem.isBroken(optional.get().getRight()))
            info.setReturnValue(true);
    }
}