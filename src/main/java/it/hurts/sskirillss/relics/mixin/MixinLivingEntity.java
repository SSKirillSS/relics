package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.boots.AmphibianBootItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    @Inject(at = @At(value = "RETURN"), method = "decreaseAirSupply", cancellable = true)
    protected void calculateAir(int air, CallbackInfoReturnable<Integer> info) {
        AmphibianBootItem.Stats config = AmphibianBootItem.INSTANCE.getConfig();
        LivingEntity entity = (LivingEntity) (Object) this;

        if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.AMPHIBIAN_BOOT.get(), entity).isPresent()
                && entity.getRandom().nextInt(config.airSupplyModifier) == 0)
            info.setReturnValue(info.getReturnValue() + 1);
    }

    @Inject(at = @At("HEAD"), method = "checkTotemDeathProtection", cancellable = true)
    protected void preventTotemUse(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Optional<ImmutableTriple<String, Integer, ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DELAY_RING.get(), entity);

        if (entity instanceof PlayerEntity && optional.isPresent() && !RelicItem.isBroken(optional.get().getRight())
                && !((PlayerEntity) entity).getCooldowns().isOnCooldown(optional.get().getRight().getItem()))
            info.setReturnValue(false);
    }
}