package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ShockwaveEntity extends ThrowableProjectile {
    @Getter
    @Setter
    private int radius;
    @Getter
    @Setter
    private float damage;

    @Getter
    @Setter
    private int step;

    @Getter
    @Setter
    private List<BlockPos> poses = new ArrayList<>();

    public ShockwaveEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ShockwaveEntity(Level level, int radius, float damage) {
        super(EntityRegistry.SHOCKWAVE.get(), level);

        this.radius = radius;
        this.damage = damage;
    }

    @Override
    public void tick() {
        super.tick();

        BlockPos center = this.blockPosition();
        Level level = this.getLevel();

        if (poses.isEmpty()) {
            for (int i = -(int) radius; i <= radius; i++) {
                float r1 = Mth.sqrt((float) (radius * radius - i * i));

                for (int j = -(int) r1; j <= r1; j++)
                    poses.add(center.offset(i, 0, j));
            }
        }

        if (!poses.isEmpty()) {
            Entity owner = getOwner();

            Vec3 centerVec = new Vec3(center.getX(), center.getY(), center.getZ());

            List<BlockPos> closest = poses.stream().filter(p -> new Vec3(p.getX(), p.getY(), p.getZ())
                    .distanceTo(centerVec) <= step).toList();

            poses.removeAll(closest);

            for (BlockPos p : closest) {
                if (!level.isClientSide()) {
                    float damage = radius * this.damage / step;

                    for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(p, p.above(3)).inflate(0.5F))) {
                        if (owner != null && entity.getStringUUID().equals(owner.getStringUUID()))
                            continue;

                        if (this.getOwner() instanceof Player player)
                            EntityUtils.hurt(entity, DamageSource.playerAttack(player), damage);
                        else
                            entity.hurt(DamageSource.MAGIC, damage);

                        entity.setDeltaMovement(entity.position().add(0, 1, 0).subtract(centerVec).normalize().multiply(2, 1, 2));
                    }
                }

                BlockState state = level.getBlockState(p);

                if (!state.getMaterial().blocksMotion() || level.getBlockState(p.above()).getMaterial().blocksMotion())
                    continue;

                BlockSimulationEntity entity = new BlockSimulationEntity(level, state);

                entity.setPos(p.getX() + 0.5F, p.getY() + 0.5F, p.getZ() + 0.5F);

                entity.setDeltaMovement(0, step * 0.02F, 0);

                level.addFreshEntity(entity);
            }

            step++;

            if (poses.isEmpty() || step >= poses.size())
                this.remove(RemovalReason.KILLED);
        } else
            this.remove(RemovalReason.KILLED);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.radius = compound.getInt("Radius");
        this.damage = compound.getFloat("Damage");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Radius", this.radius);
        compound.putFloat("Damage", this.damage);
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