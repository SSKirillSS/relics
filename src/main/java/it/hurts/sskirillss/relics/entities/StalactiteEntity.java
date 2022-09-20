package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;

public class StalactiteEntity extends ThrowableProjectile {
    @Getter
    @Setter
    private float amount;

    public StalactiteEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public StalactiteEntity(Level level, float amount) {
        super(EntityRegistry.STALACTITE.get(), level);

        this.amount = amount;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > 200)
            this.discard();

        Level level = this.getLevel();

        if (level.isClientSide())
            return;

        ((ServerLevel) level).sendParticles(new CircleTintData(new Color(100, 0, 255), 0.1F, 40, 0.9F, false),
                this.xo, this.yo, this.zo, 1, 0.025D, 0.025D, 0.025D, 0.01F);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockPos pos = result.getBlockPos();
        BlockState state = this.level.getBlockState(pos);

        if (!state.getMaterial().blocksMotion())
            return;

        this.level.playSound(null, pos, SoundEvents.BASALT_BREAK, SoundSource.MASTER, 0.75F, 1.75F);

        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level.isClientSide() || !(result.getEntity() instanceof LivingEntity entity)
                || (this.getOwner() != null && entity.getStringUUID().equals(this.getOwner().getStringUUID())))
            return;

        entity.hurt(DamageSource.thrown(this, this.getOwner()), amount * 0.1F);

        entity.addEffect(new MobEffectInstance(EffectRegistry.STUN.get(), Math.round(amount * 0.5F), 0, true, false));

        this.discard();
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.amount = compound.getFloat("Amount");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("Amount", this.amount);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}