package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.WebBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

@Mixin(WebBlock.class)
public class MixinWebBlock {
    @Inject(at = @At(value = "HEAD"), method = "entityInside", cancellable = true)
    protected void ignoreWeb(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info) {
        if (!(entity instanceof LivingEntity))
            return;

        Optional<ImmutableTriple<String, Integer, ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SPIDER_NECKLACE.get(), (LivingEntity) entity);

        if (optional.isPresent() && !RelicItem.isBroken(optional.get().getRight()))
            info.cancel();
    }
}