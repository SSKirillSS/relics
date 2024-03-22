package it.hurts.sskirillss.relics.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccess {
    @Invoker("getOnPos")
    BlockPos invokeGetOnPos(float yOffset);
}
