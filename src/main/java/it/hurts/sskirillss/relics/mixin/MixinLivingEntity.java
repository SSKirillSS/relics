package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.api.events.utils.EventDispatcher;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.PacketSyncEntityEffects;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    @Inject(at = @At("HEAD"), method = "checkTotemDeathProtection", cancellable = true)
    protected void preventTotemUse(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!EntityUtils.findEquippedCurio(entity, ItemRegistry.DELAY_RING.get()).isEmpty())
            info.setReturnValue(false);
    }

    @ModifyVariable(method = "travel", index = 8, ordinal = 0, at = @At("STORE"))
    protected float setBlockFriction(float original) {
        LivingEntity entity = (LivingEntity) (Object) this;

        return EventDispatcher.onLivingSlipping(entity, entity.level.getBlockState(entity.getBlockPosBelowThatAffectsMyMovement()), original);
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