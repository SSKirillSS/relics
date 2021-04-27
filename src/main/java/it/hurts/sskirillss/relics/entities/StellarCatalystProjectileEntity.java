package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
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
        super(EntityRegistry.STELLAR_CATALYST_PROJECTILE.get(), throwerIn, target.getEntityWorld());
        this.damage = damage;
        if (throwerIn instanceof PlayerEntity) this.owner = (PlayerEntity) throwerIn;
    }

    @Override
    public void tick() {
        super.tick();
        Random random = this.getEntityWorld().getRandom();
        for (int i = 0; i < 3; i++)
            world.addParticle(new CircleTintData(new Color(255 - random.nextInt(150), 0, 255 - random.nextInt(150)),
                            0.2F + random.nextFloat() * 0.15F, 20, 0.95F, false), this.prevPosX, this.prevPosY, this.prevPosZ,
                    MathUtils.generateReallyRandomFloat(random) * 0.2F, random.nextFloat() * 0.75F, MathUtils.generateReallyRandomFloat(random) * 0.2F);
        if (this.ticksExisted > 100) this.remove();
        this.setMotion(0.0F, -RelicsConfig.StellarCatalyst.FALLING_STAR_SPEED.get(), 0.0F);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        Random random = this.getEntityWorld().getRandom();
        ParticleUtils.createBall(new CircleTintData(new Color(255 - random.nextInt(100), 0, 255 - random.nextInt(100)),
                0.4F, 40, 0.90F, true), this.getPositionVec(), this.getEntityWorld(), 2, 0.2F);
        ParticleUtils.createBall(new CircleTintData(new Color(175 - random.nextInt(100), 0, 255 - random.nextInt(50)),
                0.4F, 40, 0.90F, true), this.getPositionVec(), this.getEntityWorld(), 1, 0.1F);
        if (world.isRemote()) return;
        for (LivingEntity entity : this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class,
                this.getBoundingBox().grow(RelicsConfig.StellarCatalyst.FALLING_STAR_DAMAGE_RADIUS.get()))) {
            float multiplier = RelicsConfig.StellarCatalyst.FALLING_STAR_IMPACT_MOTION_MULTIPLIER.get().floatValue();
            Vector3d motion = entity.getPositionVec().subtract(this.getPositionVec()).normalize().mul(multiplier, multiplier, multiplier);
            if (entity instanceof ServerPlayerEntity && entity != owner)
                NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayerEntity) entity);
            else entity.setMotion(motion);
            if (owner != null) {
                if (entity != owner) entity.attackEntityFrom(DamageSource.causePlayerDamage(owner), damage);
            } else entity.attackEntityFrom(DamageSource.GENERIC, (float) (RelicsConfig.StellarCatalyst.MIN_DAMAGE_AMOUNT.get()
                    * RelicsConfig.StellarCatalyst.FALLING_STAR_DAMAGE_MULTIPLIER.get()));
        }
        this.setDead();
    }

    @Override
    protected void writeAdditional(@Nonnull CompoundNBT compound) {
        compound.putFloat(TAG_DAMAGE, damage);
    }

    @Override
    protected void readAdditional(@Nonnull CompoundNBT compound) {
        damage = compound.getFloat(TAG_DAMAGE);
    }

    @Override
    protected void registerData() {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}