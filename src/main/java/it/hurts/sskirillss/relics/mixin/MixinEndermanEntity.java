package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

@Mixin(EndermanEntity.class)
public class MixinEndermanEntity {
    @Inject(at = @At(value = "HEAD"), method = "isLookingAtMe", cancellable = true)
    protected void calmEndermans(PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
        Optional<ImmutableTriple<String, Integer, ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ENDERS_HAND.get(), player);

        if (optional.isPresent() && !RelicItem.isBroken(optional.get().getRight())) info.setReturnValue(false);
    }
}