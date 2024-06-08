package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

public class LifeEssenceEntity extends ThrowableProjectile {
    @Setter
    @Getter
    private float heal;

    private int directionChoice;

    public LifeEssenceEntity(EntityType<? extends LifeEssenceEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public LifeEssenceEntity(LivingEntity throwerIn, float heal) {
        super(EntityRegistry.LIFE_ESSENCE.get(), throwerIn.getCommandSenderWorld());

        this.setOwner(throwerIn);

        this.heal = heal;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide())
            return;

        double size = 0.02D + heal * 0.001D;

        ((ServerLevel) level()).sendParticles(ParticleUtils.constructSimpleSpark(new Color(200, 150 + random.nextInt(50), random.nextInt(50)), 0.5F + (heal * 0.01F), 20 + Math.round(heal * 0.025F), 0.9F),
                this.getX(), this.getY(), this.getZ(), 1, size, size, size, 0.01F + heal * 0.0001F);
        ((ServerLevel) level()).sendParticles(ParticleUtils.constructSimpleSpark(new Color(200, 150 + random.nextInt(50), random.nextInt(50)), 0.5F + (heal * 0.01F), 20 + Math.round(heal * 0.025F), 0.9F),
                this.xo, this.yo, this.zo, 1, size, size, size, 0.01F + heal * 0.0001F);

        if (!(getOwner() instanceof Player player) || player.isDeadOrDying()) {
            this.remove(RemovalReason.KILLED);

            return;
        }

        for (LifeEssenceEntity essence : level().getEntitiesOfClass(LifeEssenceEntity.class, this.getBoundingBox().inflate(heal * 0.05F))) {
            if (essence.getStringUUID().equals(this.getStringUUID()) || (essence.getOwner() instanceof Player p1
                    && this.getOwner() instanceof Player p2 && !p1.getStringUUID().equals(p2.getStringUUID())))
                continue;

            setHeal(getHeal() + essence.getHeal());

            essence.remove(RemovalReason.KILLED);
        }

        double distance = this.position().distanceTo(player.position().add(0, player.getBbHeight() / 2, 0));

        if (distance > 1) {
            if (distance > 32) {
                this.remove(RemovalReason.KILLED);

                return;
            }
            moveTowardsTargetInArc(player);
        } else {
            player.heal(heal);

            this.remove(RemovalReason.KILLED);
        }
    }
    private void moveTowardsTargetInArc(Entity target) {
        Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
        Vec3 direction = targetPos.subtract(this.position()).normalize();

        if (directionChoice == 0)
            directionChoice = new Random().nextBoolean() ? 1 : -1;

        Vec3 perpendicular = new Vec3(directionChoice * -direction.z, 0, directionChoice * direction.x).normalize();
        double distance = this.position().distanceTo(targetPos);

        if (distance > 0) {
            Vec3 newPos = this.position().add(direction.add(perpendicular).scale(distance * 0.5));
            Vec3 delta = newPos.subtract(this.position()).normalize().scale(0.35);

           this.setDeltaMovement(delta.x, delta.y, delta.z);
        }
    }
    @Override
    protected void defineSynchedData() {

    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}