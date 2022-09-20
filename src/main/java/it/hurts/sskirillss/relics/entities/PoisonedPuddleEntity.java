package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class PoisonedPuddleEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(PoisonedPuddleEntity.class, EntityDataSerializers.FLOAT);

    public void setSize(float amount) {
        this.getEntityData().set(SIZE, Math.min(10, amount));
    }

    public float getSize() {
        return this.getEntityData().get(SIZE);
    }

    public void addSize(float amount) {
        this.setSize(this.getSize() + amount);
    }

    @Getter
    @Setter
    private ItemStack stack = ItemStack.EMPTY;

    public PoisonedPuddleEntity(EntityType<? extends PoisonedPuddleEntity> type, Level worldIn) {
        super(type, worldIn);

        this.setSize(1F);
    }

    public PoisonedPuddleEntity(Entity throwerIn) {
        super(EntityRegistry.POISONED_PUDDLE.get(), throwerIn.getCommandSenderWorld());

        this.setOwner(throwerIn);

        this.setSize(1F);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getSize() > 0.5F)
            this.addSize((float) -Math.max(RelicItem.getAbilityValue(stack, "puddle", "resize"), 0.01D));
        else
            this.discard();

        float size = getSize();

        for (int i = 0; i < (10 - size / 2) * 3; i++) {
            float angle = random.nextInt(360);

            double extraX = Math.min(1D, random.nextDouble() * 1.75D) * (size * Math.sin(angle + this.tickCount)) + this.getX();
            double extraZ = Math.min(1D, random.nextDouble() * 1.75D) * (size * Math.cos(angle + this.tickCount)) + this.getZ();

            level.addParticle(new CircleTintData(
                    new Color(25 + random.nextInt(50), 175 + random.nextInt(75), 0), 0.15F + (size * 0.035F),
                    Math.round(size * 10), Math.min(0.99F, 0.95F + size * 0.005F), false), extraX, this.getY(), extraZ, 0, random.nextFloat() * 0.015F, 0);
        }

        if (level.isClientSide())
            return;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (this.position().distanceTo(entity.position()) > size + 0.5F || (this.getOwner() instanceof Player player
                    && entity.getStringUUID().equals(player.getStringUUID())))
                continue;

            MobEffectInstance effect = entity.getEffect(MobEffects.POISON);

            entity.addEffect(new MobEffectInstance(MobEffects.POISON, effect == null ? 20 : effect.getDuration() + 2, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, (int) (Math.round(RelicItem.getAbilityValue(stack, "puddle", "slowness")) - 1)));

            if (this.getOwner() instanceof Player player)
                entity.setLastHurtByPlayer(player);
        }

        for (PoisonedPuddleEntity puddle : level.getEntitiesOfClass(PoisonedPuddleEntity.class, this.getBoundingBox())) {
            if (puddle.getStringUUID().equals(this.getStringUUID()) || puddle.getSize() > size)
                continue;

            this.addSize(puddle.getSize() * 0.1F);

            puddle.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SIZE, 0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        setSize(compound.getFloat("size"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("size", getSize());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (SIZE.equals(pKey))
            this.refreshDimensions();

        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected float getGravity() {
        return 0F;
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(this.getSize() * 2F, 0.5F);
    }
}