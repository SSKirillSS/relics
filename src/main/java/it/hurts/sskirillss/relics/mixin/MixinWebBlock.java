package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.WebBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(WebBlock.class)
public class MixinWebBlock {
    @Inject(at = @At("HEAD"), method = "entityInside", cancellable = true)
    protected void ignoreWeb(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info) {
        if (entity instanceof LivingEntity && CuriosApi.getCuriosHelper().findEquippedCurio(
                ItemRegistry.SPIDER_NECKLACE.get(), (LivingEntity) entity).isPresent())
            info.cancel();
    }
}