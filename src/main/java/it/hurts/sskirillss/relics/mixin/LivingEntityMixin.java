package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.api.events.common.LivingSlippingEvent;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.items.relics.feet.AmphibianBootItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.PacketSyncEntityEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyArg(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setAirSupply(I)V", ordinal = 0))
    private int relics$modifyAirConsumption(int decreasedAirSupply) {
        LivingEntity instance = (LivingEntity) (Object) this;

        if (AmphibianBootItem.canBreathe(instance)) {
            return instance.getAirSupply();
        }

        return decreasedAirSupply;
    }

    @ModifyVariable(method = "travel", name = "f2", index = 8, ordinal = 0, at = @At("STORE"))
    protected float setBlockFriction(float original) {
        LivingEntity entity = (LivingEntity) (Object) this;

        LivingSlippingEvent event = new LivingSlippingEvent(entity, entity.level.getBlockState(entity.getBlockPosBelowThatAffectsMyMovement()), original);

        MinecraftForge.EVENT_BUS.post(event);

        return event.getFriction();
    }

    @Inject(method = "isImmobile", at = @At("HEAD"), cancellable = true)
    protected void onAiStep(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.hasEffect(EffectRegistry.STUN.get()))
            cir.setReturnValue(true);

        if (entity.hasEffect(EffectRegistry.PARALYSIS.get()))
            cir.setReturnValue(true);
    }

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    protected void onEffectAdded(MobEffectInstance effect, Entity target, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level level = entity.getCommandSenderWorld();

        if (level.isClientSide())
            return;

        CompoundTag tag = new CompoundTag();

        effect.save(tag);

        NetworkHandler.sendToClients(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                new PacketSyncEntityEffects(entity.getId(), tag, PacketSyncEntityEffects.Action.ADD));
    }

    @Inject(method = "onEffectRemoved", at = @At("TAIL"))
    protected void onEffectRemoved(MobEffectInstance effect, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level level = entity.getCommandSenderWorld();

        if (level.isClientSide())
            return;

        CompoundTag tag = new CompoundTag();

        effect.save(tag);

        NetworkHandler.sendToClients(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                new PacketSyncEntityEffects(entity.getId(), tag, PacketSyncEntityEffects.Action.REMOVE));
    }

    @Inject(method = "canBeSeenByAnyone", at = @At("HEAD"), cancellable = true)
    protected void canBeSeenByAnyone(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.hasEffect(EffectRegistry.VANISHING.get()))
            cir.setReturnValue(false);
    }
}