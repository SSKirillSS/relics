package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.items.relics.StellarCatalystItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Random;

public class StellarCatalystProjectileEntity extends ThrowableEntity {
    public static final String TAG_DAMAGE = "damage";

    private float damage;
    public PlayerEntity owner;

    public StellarCatalystProjectileEntity(EntityType<? extends StellarCatalystProjectileEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public StellarCatalystProjectileEntity(LivingEntity throwerIn, LivingEntity target, float damage) {
        super(EntityRegistry.STELLAR_CATALYST_PROJECTILE.get(), throwerIn, target.getCommandSenderWorld());

        this.damage = damage;

        if (throwerIn instanceof PlayerEntity)
            this.owner = (PlayerEntity) throwerIn;
    }

    @Override
    public void tick() {
        super.tick();

        StellarCatalystItem.Stats config = StellarCatalystItem.INSTANCE.getConfig();

        Random random = this.getCommandSenderWorld().getRandom();

        for (int i = 0; i < 3; i++)
            level.addParticle(new CircleTintData(new Color(255 - random.nextInt(150), 0, 255 - random.nextInt(150)),
                            0.2F + random.nextFloat() * 0.15F, 20, 0.95F, false), this.xo, this.yo, this.zo,
                    MathUtils.randomFloat(random) * 0.2F, random.nextFloat() * 0.75F, MathUtils.randomFloat(random) * 0.2F);

        if (this.tickCount > 100)
            this.remove();

        this.setDeltaMovement(0.0F, -config.projectileSpeed, 0.0F);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        StellarCatalystItem.Stats config = StellarCatalystItem.INSTANCE.getConfig();

        Random random = this.getCommandSenderWorld().getRandom();

        ParticleUtils.createBall(new CircleTintData(new Color(255 - random.nextInt(100), 0, 255 - random.nextInt(100)),
                0.4F, 40, 0.90F, true), this.position(), this.getCommandSenderWorld(), 2, 0.2F);
        ParticleUtils.createBall(new CircleTintData(new Color(175 - random.nextInt(100), 0, 255 - random.nextInt(50)),
                0.4F, 40, 0.90F, true), this.position(), this.getCommandSenderWorld(), 1, 0.1F);

        if (level.isClientSide())
            return;

        for (LivingEntity entity : this.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(config.explosionRadius))) {
            float multiplier = config.knockbackPower;
            Vector3d motion = entity.position().subtract(this.position()).normalize().multiply(multiplier, multiplier, multiplier);

            if (entity instanceof ServerPlayerEntity && entity != owner)
                NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayerEntity) entity);
            else
                entity.setDeltaMovement(motion);

            if (owner != null) {
                if (entity != owner)
                    entity.hurt(DamageSource.playerAttack(owner), damage);
            } else
                entity.hurt(DamageSource.GENERIC, (float) (config.minDamage * config.damageMultiplier));
        }

        this.removeAfterChangingDimensions();
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundNBT compound) {
        compound.putFloat(TAG_DAMAGE, damage);
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
        damage = compound.getFloat(TAG_DAMAGE);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}