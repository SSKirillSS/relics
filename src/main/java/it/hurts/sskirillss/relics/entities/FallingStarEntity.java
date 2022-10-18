package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Random;

public class FallingStarEntity extends ThrowableProjectile {
    public static final String TAG_DAMAGE = "damage";

    private float damage;
    public Player owner;

    public FallingStarEntity(EntityType<? extends FallingStarEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public FallingStarEntity(LivingEntity throwerIn, LivingEntity target, float damage) {
        super(EntityRegistry.STELLAR_CATALYST_PROJECTILE.get(), throwerIn, target.getCommandSenderWorld());

        this.damage = damage;

        if (throwerIn instanceof Player)
            this.owner = (Player) throwerIn;
    }

    @Override
    public void tick() {
        super.tick();

        Random random = this.getCommandSenderWorld().getRandom();

        for (int i = 0; i < 5; i++)
            level.addParticle(new CircleTintData(new Color(255 - random.nextInt(150), 0, 255 - random.nextInt(150)),
                            0.2F + random.nextFloat() * 0.15F, 20, 0.95F, false), this.xo, this.yo, this.zo,
                    MathUtils.randomFloat(random) * 0.2F, random.nextFloat() * 0.75F, MathUtils.randomFloat(random) * 0.2F);

        if (this.tickCount > 100)
            this.remove(Entity.RemovalReason.KILLED);

        this.setDeltaMovement(0.0F, -0.1 - this.tickCount * 0.01F, 0.0F);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        Random random = this.getCommandSenderWorld().getRandom();

        ParticleUtils.createBall(new CircleTintData(new Color(255 - random.nextInt(100), 0, 255 - random.nextInt(100)),
                0.4F, 40, 0.90F, true), this.position(), this.getCommandSenderWorld(), 2, 0.2F);
        ParticleUtils.createBall(new CircleTintData(new Color(175 - random.nextInt(100), 0, 255 - random.nextInt(50)),
                0.4F, 40, 0.90F, true), this.position(), this.getCommandSenderWorld(), 1, 0.1F);

        if (level.isClientSide())
            return;

        for (LivingEntity entity : this.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(5))) {
            float multiplier = 1;
            Vec3 motion = entity.position().subtract(this.position()).normalize().multiply(multiplier, multiplier, multiplier);

            if (entity instanceof ServerPlayer && entity != owner)
                NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayer) entity);
            else
                entity.setDeltaMovement(motion);

            if (owner != null && entity != owner)
                entity.hurt(new IndirectEntityDamageSource("falling_star", this, owner).setProjectile(), damage);
        }

        this.removeAfterChangingDimensions();
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        compound.putFloat(TAG_DAMAGE, damage);
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        damage = compound.getFloat(TAG_DAMAGE);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}