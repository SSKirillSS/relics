package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WebBlock.class)
public class MixinWebBlock {
    @Inject(at = @At(value = "HEAD"), method = "entityInside", cancellable = true)
    protected void ignoreWeb(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo info) {
        if (!(entity instanceof LivingEntity))
            return;

        if (!EntityUtils.findEquippedCurio((LivingEntity) entity, ItemRegistry.SPIDER_NECKLACE.get()).isEmpty())
            info.cancel();
    }
}