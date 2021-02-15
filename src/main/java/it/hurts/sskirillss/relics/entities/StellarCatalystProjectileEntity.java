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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.UUID;

public class StellarCatalystProjectileEntity extends ThrowableEntity {
    public static final String TAG_TARGET_POSITION = "target";
    public static final String TAG_LIVE_TIME = "time";
    public static final String TAG_DAMAGE_AMOUNT = "damage";
    public static final String TAG_THROWER = "thrower";

    private int liveTime;
    private String position;
    private float damage;
    private UUID thrower;

    public StellarCatalystProjectileEntity(EntityType<? extends StellarCatalystProjectileEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public StellarCatalystProjectileEntity(LivingEntity throwerIn, LivingEntity target, float damage) {
        super(EntityRegistry.STELLAR_CATALYST_PROJECTILE.get(), throwerIn, target.getEntityWorld());
        this.position = NBTUtils.writePosition(new Vector3d(target.getPosX(), 0.0D, target.getPosZ()));
        this.damage = damage;
        this.thrower = throwerIn.getUniqueID();
        this.setNoGravity(true);
        this.setPosition(target.getPosX() + MathUtils.generateReallyRandomFloat() * 10,
                Math.min(target.getPosY() + world.getRandom().nextInt(10) + 20, target.getEntityWorld().getHeight()),
                target.getPosZ() + MathUtils.generateReallyRandomFloat() * 10);
    }

    @Override
    public void tick() {
        super.tick();
        liveTime++;
        if (liveTime > 10 * 20) this.setDead();

        if (NBTUtils.parsePosition(position) != null) {
            Vector3d pos = NBTUtils.parsePosition(position);
            EntityUtils.moveTowardsPosition(this, new Vector3d(pos.getX(), pos.getY(), pos.getZ()), 0.5F);
        }

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

        for (LivingEntity entity : this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class,
                this.getBoundingBox().grow(RelicsConfig.StellarCatalyst.FALLING_STAR_DAMAGE_RADIUS.get()))) {
            double distance = entity.getPositionVec().distanceTo(this.getPositionVec());
            if (thrower != null && world.getPlayerByUuid(thrower) != null && entity != world.getPlayerByUuid(thrower)) {
                entity.attackEntityFrom(DamageSource.causePlayerDamage(world.getPlayerByUuid(thrower)), (float) (damage / (distance * 0.25D)));
                Vector3d motion = entity.getPositionVec().subtract(this.getPositionVec()).normalize()
                        .mul(distance * 0.25D, distance * 0.25D, distance * 0.25D);
                if (entity instanceof PlayerEntity) {
                    NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayerEntity) entity);
                } else {
                    entity.setMotion(motion);
                }
            }
        }

        this.setDead();
    }

    @Override
    protected void writeAdditional(@Nonnull CompoundNBT compound) {
        compound.putInt(TAG_LIVE_TIME, liveTime);
        compound.putFloat(TAG_DAMAGE_AMOUNT, damage);
        compound.putString(TAG_TARGET_POSITION, position);
        compound.putUniqueId(TAG_THROWER, thrower);
    }

    @Override
    protected void readAdditional(@Nonnull CompoundNBT compound) {
        liveTime = compound.getInt(TAG_LIVE_TIME);
        damage = compound.getFloat(TAG_DAMAGE_AMOUNT);
        position = compound.getString(TAG_TARGET_POSITION);
        thrower = compound.getUniqueId(TAG_THROWER);
    }

    @Override
    protected void registerData() {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}