package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.api.events.common.EntityBlockSpeedFactorEvent;
import it.hurts.sskirillss.relics.api.events.common.FluidCollisionEvent;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Entity.class)
public class MixinEntity {
    @ModifyVariable(method = "move", ordinal = 1, index = 3, name = "vec32", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 fluidCollision(Vec3 original) {
        if (!((Object) this instanceof LivingEntity entity))
            return original;

        if (original.y > 0 || isTouchingFluid(entity, entity.getBoundingBox().deflate(0.001D)))
            return original;

        Map<Vec3, Double> points = new HashMap<>();
        Double highestDistance = null;

        AABB box = entity.getBoundingBox().move(original);

        points.put(new Vec3(box.minX, box.minY, box.minZ), null);
        points.put(new Vec3(box.minX, box.minY, box.maxZ), null);
        points.put(new Vec3(box.maxX, box.minY, box.minZ), null);
        points.put(new Vec3(box.maxX, box.minY, box.maxZ), null);

        boolean cancelled = false;
        double fluidStepHeight = entity.onGround() ? Math.max(1F, entity.maxUpStep) : 0F;

        for (Map.Entry<Vec3, Double> entry : points.entrySet()) {
            for (int i = 0; ; i++) {
                Vec3 vec = entry.getKey();

                BlockPos landingPos = new BlockPos((int) vec.x, (int) vec.y, (int) vec.z).offset(0, (int) (fluidStepHeight - i), 0);
                FluidState landingState = entity.getCommandSenderWorld().getFluidState(landingPos);

                double distanceToFluidSurface = landingPos.getY() + landingState.getOwnHeight() - entity.getY();

                if (distanceToFluidSurface < original.y || distanceToFluidSurface > fluidStepHeight)
                    break;

                if (!landingState.isEmpty()) {
                    FluidCollisionEvent event = new FluidCollisionEvent(entity, landingState);

                    MinecraftForge.EVENT_BUS.post(event);

                    if (cancelled || event.isCanceled()) {
                        entry.setValue(distanceToFluidSurface);

                        cancelled = true;

                        break;
                    }
                }
            }
        }

        for (Map.Entry<Vec3, Double> point : points.entrySet())
            if (highestDistance == null || (point.getValue() != null && point.getValue() > highestDistance))
                highestDistance = point.getValue();

        if (highestDistance == null)
            return original;

        Vec3 finalDisplacement = new Vec3(original.x, highestDistance, original.z);

        if (!isTouchingFluid(entity, entity.getBoundingBox().move(finalDisplacement).deflate(0.001D))) {
            entity.fallDistance = 0.0F;
            entity.setOnGround(true);

            return finalDisplacement;
        }

        return original;
    }

    @Unique
    private static boolean isTouchingFluid(LivingEntity entity, AABB box) {
        Level world = entity.getCommandSenderWorld();

        int minX = Mth.floor(box.minX);
        int maxX = Mth.ceil(box.maxX);
        int minY = Mth.floor(box.minY);
        int maxY = Mth.ceil(box.maxY);
        int minZ = Mth.floor(box.minZ);
        int maxZ = Mth.ceil(box.maxZ);

        if (!world.hasChunksAt(minX, minY, minZ, maxX, maxY, maxZ))
            return false;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int i = minX; i < maxX; ++i) {
            for (int j = minY; j < maxY; ++j) {
                for (int k = minZ; k < maxZ; ++k) {
                    mutable.set(i, j, k);

                    FluidState fluidState = world.getFluidState(mutable);

                    if (!fluidState.isEmpty() && fluidState.getHeight(world, mutable) + j >= box.minY)
                        return true;
                }
            }
        }

        return false;
    }

    @Inject(at = @At(value = "RETURN"), method = "isInWaterOrRain", cancellable = true)
    public void setWet(CallbackInfoReturnable<Boolean> info) {
        Entity entity = (Entity) (Object) this;

        if (!(entity instanceof LivingEntity))
            return;

        if (!EntityUtils.findEquippedCurio(entity, ItemRegistry.DROWNED_BELT.get()).isEmpty())
            info.setReturnValue(true);
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("RETURN"), cancellable = true)
    public void getBlockSpeedFactor(CallbackInfoReturnable<Float> cir) {
        Entity entity = (Entity) (Object) this;

        EntityBlockSpeedFactorEvent event = new EntityBlockSpeedFactorEvent(entity, entity.level().getBlockState(entity.getOnPos()), cir.getReturnValue());

        MinecraftForge.EVENT_BUS.post(event);

        cir.setReturnValue(event.getSpeedFactor());
    }
}