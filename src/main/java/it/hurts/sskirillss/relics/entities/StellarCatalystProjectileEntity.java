package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import java.util.UUID;

public class StellarCatalystProjectileEntity extends ThrowableEntity {
    public static final String TAG_LIVE_TIME = "time";
    public static final String TAG_DAMAGE_AMOUNT = "damage";
    public static final String TAG_THROWER = "thrower";

    private int liveTime;
    private float damage;
    private UUID thrower;

    public StellarCatalystProjectileEntity(EntityType<? extends StellarCatalystProjectileEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public StellarCatalystProjectileEntity(LivingEntity throwerIn, LivingEntity target, float damage) {
        super(EntityRegistry.STELLAR_CATALYST_PROJECTILE.get(), throwerIn, target.getEntityWorld());
        this.damage = damage;
        this.thrower = throwerIn.getUniqueID();
    }

    @Override
    public void tick() {
        super.tick();

        this.setMotion(0.0F, -0.5F, 0.0F);
        liveTime++;
        if (liveTime > 10 * 20) this.setDead();

        if (world.isRemote() && liveTime > 5) {
            CircleTintData circleTintData = new CircleTintData(
                    new Color(0.4F, 0.05F, 0.7F), 0.5F, 40, 0.95F, false);
            for (int i = 0; i < 2; i++) {
                world.addParticle(circleTintData,
                        getPosX(), getPosY(), getPosZ(),
                        0F, 0F, 0F);
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        ParticleUtils.createBall(new CircleTintData(new Color(0.4F, 0.05F, 0.7F), 0.5F, 40, 0.94F, true),
                this.getPositionVec(), this.getEntityWorld(), 3, 0.2F);
        if (!world.isRemote()) {
            for (LivingEntity entity : this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class,
                    this.getBoundingBox().grow(RelicsConfig.StellarCatalyst.FALLING_STAR_DAMAGE_RADIUS.get()))) {
                Vector3d motion = entity.getPositionVec().subtract(this.getPositionVec()).normalize();
                if (entity instanceof ServerPlayerEntity) {
                    NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayerEntity) entity);
                } else {
                    entity.setMotion(motion);
                }
                if (thrower != null) {
                    if (world.getPlayerByUuid(thrower) != null && entity != world.getPlayerByUuid(thrower)) {
                        entity.attackEntityFrom(DamageSource.causePlayerDamage(world.getPlayerByUuid(thrower)), damage);
                    }
                } else {
                    entity.attackEntityFrom(DamageSource.GENERIC, (float) (RelicsConfig.StellarCatalyst.MIN_DAMAGE_AMOUNT.get()
                            * RelicsConfig.StellarCatalyst.FALLING_STAR_DAMAGE_MULTIPLIER.get()));
                }
            }
        }
        this.setDead();
    }

    @Override
    protected void writeAdditional(@Nonnull CompoundNBT compound) {
        compound.putInt(TAG_LIVE_TIME, liveTime);
        compound.putFloat(TAG_DAMAGE_AMOUNT, damage);
        if (thrower != null) compound.putUniqueId(TAG_THROWER, thrower);
    }

    @Override
    protected void readAdditional(@Nonnull CompoundNBT compound) {
        liveTime = compound.getInt(TAG_LIVE_TIME);
        damage = compound.getFloat(TAG_DAMAGE_AMOUNT);
        if (thrower != null) thrower = compound.getUniqueId(TAG_THROWER);
    }

    @Override
    protected void registerData() {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}