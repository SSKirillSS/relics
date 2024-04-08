package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.back.ArrowQuiverItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ArrowRainEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(ArrowRainEntity.class, EntityDataSerializers.FLOAT);

    @Getter
    @Setter
    private int delay;

    @Getter
    @Setter
    private int duration;

    @Getter
    @Setter
    private ItemStack quiver;

    public float getRadius() {
        return this.getEntityData().get(RADIUS);
    }

    public void setRadius(float radius) {
        this.getEntityData().set(RADIUS, radius);
    }

    public ArrowRainEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        Random random = level.getRandom();

        ParticleUtils.createCyl(ParticleUtils.constructSimpleSpark(new Color(255, 255, 255), 0.2F, 1, 1F),
                this.position(), getLevel(), getRadius(), 0.2F);

        if (!level.isClientSide()) {
            if (getDelay() == 0 || getRadius() == 0 || quiver.isEmpty() || getDuration() < this.tickCount) {
                this.discard();

                return;
            }
        }

        AABB area = this.getBoundingBox().inflate(getRadius(), getRadius() * 2, getRadius());

        for (AbstractArrow arrow : level.getEntitiesOfClass(AbstractArrow.class, area)) {
            if (arrow.isOnGround())
                continue;

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area).stream()
                    .filter(entry -> entry.hasLineOfSight(arrow))
                    .filter(entry -> entry.position().distanceTo(this.position()) <= getRadius())
                    .filter(entry -> !EntityUtils.isAlliedTo(this.getOwner(), entry))
                    .sorted(Comparator.comparing(entry -> entry.position().distanceTo(arrow.position())))
                    .toList();

            if (entities.isEmpty())
                continue;

            LivingEntity target = entities.get(0);

            Vec3 motion = target.position().subtract(arrow.position()).normalize().scale(0.1F);

            arrow.setDeltaMovement(arrow.getDeltaMovement().add(motion.x(), 0, motion.z()));
        }

        if (!(getOwner() instanceof Player player))
            return;

        if (this.tickCount % getDelay() == 0) {
            List<ItemStack> arrows = ArrowQuiverItem.getArrows(quiver);

            if (arrows.isEmpty()) {
                this.discard();

                return;
            }

            for (int i = 0; i < Math.ceil(getRadius() / 5F); i++) {
                ItemStack arrow = arrows.get(random.nextInt(arrows.size()));

                if (arrow.getItem() instanceof ArrowItem item) {
                    AbstractArrow entity = item.createArrow(level, arrow, player);

                    double theta = MathUtils.randomFloat(random) * 2 * Math.PI;
                    double r = random.nextFloat() * getRadius();

                    double xOff = r * Math.cos(theta);
                    double zOff = r * Math.sin(theta);

                    entity.setPos(this.getX() + xOff, this.getY() + 15 + getRadius(), this.getZ() + zOff);
                    entity.getPersistentData().putBoolean("relics_arrow_rain", true);
                    entity.setDeltaMovement(0, -0.5F, 0);
                    entity.setOwner(player);
                    entity.life = 1100;

                    entity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;

                    level.addFreshEntity(entity);

                    if (!level.isClientSide())
                        ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, entity.getX(), entity.getY(), entity.getZ(), 5, 0, 0, 0, 0.1F);

                    level.playSound(null, entity.blockPosition(), SoundRegistry.ARROW_RAIN.get(), SoundSource.MASTER, 2F, 1F + random.nextFloat() * 0.25F);
                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RADIUS, 5F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        setRadius(compound.getFloat("radius"));
        setDelay(compound.getInt("delay"));
        setDuration(compound.getInt("duration"));

        quiver = ItemStack.of(compound.getCompound("quiver"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("radius", getRadius());
        compound.putFloat("delay", getDelay());
        compound.putFloat("duration", getDuration());

        quiver.save(compound.getCompound("quiver"));
    }

    @Override
    protected float getGravity() {
        return 0F;
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

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (!(event.getEntity() instanceof AbstractArrow arrow)
                    || !arrow.getPersistentData().getBoolean("relics_arrow_rain")
                    || !(arrow.getOwner() instanceof Player player)
                    || !(event.getRayTraceResult() instanceof EntityHitResult result)
                    || !(result.getEntity() instanceof LivingEntity entity)
                    || !EntityUtils.isAlliedTo(player, entity))
                return;

            event.setCanceled(true);
        }
    }
}