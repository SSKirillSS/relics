package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.entities.misc.ITargetableEntity;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class DeathEssenceEntity extends ThrowableProjectile implements ITargetableEntity {
    @Setter
    @Getter
    private float damage;

    private LivingEntity target;

    private int directionChoice;

    public DeathEssenceEntity(EntityType<? extends DeathEssenceEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public DeathEssenceEntity(LivingEntity throwerIn, LivingEntity targetIn, float damage) {
        super(EntityRegistry.DEATH_ESSENCE.get(), throwerIn.getCommandSenderWorld());
        this.setOwner(throwerIn);

        this.target = targetIn;

        this.damage = damage;
    }

    @Override
    public void tick() {
        super.tick();

        if (target == null)
            return;

        int segments = 10;

        double dx = (this.getX() - xOld) / segments;
        double dy = (this.getY() - yOld) / segments;
        double dz = (this.getZ() - zOld) / segments;

        for (int i = 0; i < segments; i++) {
            level().addParticle(ParticleUtils.constructSimpleSpark(new Color(random.nextInt(50), random.nextInt(50), 200 + random.nextInt(55)), 0.5F + (damage * 0.01F), 20 + Math.round(damage * 0.025F), 0.9F),
                    this.getX() + dx * i, this.getY() + dy * i, this.getZ() + dz * i, -this.getDeltaMovement().x * 0.1 * Math.random(), -this.getDeltaMovement().y * 0.1 * Math.random(), -this.getDeltaMovement().z * 0.1 * Math.random());
        }

        this.moveTowardsTargetInArc(target);

        if (target.isDeadOrDying())
            this.discard();
    }

    private void moveTowardsTargetInArc(Entity target) {
        Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
        Vec3 direction = targetPos.subtract(this.position()).normalize();

        if (directionChoice == 0)
            directionChoice = 1; // TODO: new Random().nextBoolean() ? 1 : -1;

        Vec3 perpendicular = new Vec3(directionChoice * -direction.z, 0, directionChoice * direction.x).normalize();
        double distance = this.position().distanceTo(targetPos);

        if (distance > 1) {
            Vec3 newPos = this.position().add(direction.add(perpendicular).scale(distance * 0.5));
            Vec3 delta = newPos.subtract(this.position()).normalize();

            this.setDeltaMovement(delta.x, delta.y, delta.z);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (target == null)
            return;

        if (!(result.getEntity() instanceof LivingEntity entity) || entity.getUUID() != target.getUUID())
            return;

        entity.hurt(level().damageSources().generic(), damage);

        this.discard();
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}