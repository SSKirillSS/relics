package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.client.particles.spark.SparkTintData;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.items.relics.ShadowGlaiveItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShadowGlaiveEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Integer> BOUNCES = SynchedEntityData.defineId(ShadowGlaiveEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> OWNER = SynchedEntityData.defineId(ShadowGlaiveEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TARGET = SynchedEntityData.defineId(ShadowGlaiveEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> BOUNCED_ENTITIES = SynchedEntityData.defineId(ShadowGlaiveEntity.class, EntityDataSerializers.STRING);

    private static final String TAG_BOUNCES_AMOUNT = "bounces";
    private static final String TAG_OWNER_UUID = "owner";
    private static final String TAG_TARGET_UUID = "target";
    private static final String TAG_BOUNCED_ENTITIES = "entities";

    private boolean isBounced = false;
    private Player owner;
    private LivingEntity target;

    public ShadowGlaiveEntity(EntityType<? extends ShadowGlaiveEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public ShadowGlaiveEntity(Level world, LivingEntity throwerIn) {
        super(EntityRegistry.SHADOW_GLAIVE.get(), throwerIn, world);
    }

    public void setOwner(Player playerIn) {
        this.owner = playerIn;

        if (playerIn != null)
            entityData.set(OWNER, playerIn.getUUID().toString());
    }

    public void setTarget(LivingEntity target) {
        this.target = target;

        if (target != null)
            entityData.set(TARGET, target.getUUID().toString());
    }

    private void locateNearestTarget() {
        ShadowGlaiveItem.Stats config = ShadowGlaiveItem.INSTANCE.getStats();
        int bounces = entityData.get(BOUNCES);

        if (level.getRandom().nextFloat() > 1.0F - (bounces * config.bounceChanceMultiplier)) {
            this.remove(Entity.RemovalReason.KILLED);

            return;
        }

        List<String> bouncedEntities = Arrays.asList(entityData.get(BOUNCED_ENTITIES).split(","));
        List<LivingEntity> entitiesAround = level.getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(config.bounceRadius));

        entitiesAround = entitiesAround.stream()
                .filter(entity -> !bouncedEntities.contains(entity.getUUID().toString()))
                .filter(EntitySelector.NO_CREATIVE_OR_SPECTATOR)
                .filter(entity -> entity != owner)
                .sorted(Comparator.comparing(entity -> entity.position().distanceTo(this.position())))
                .collect(Collectors.toList());

        if (entitiesAround.isEmpty()) {
            if (isBounced)
                this.remove(Entity.RemovalReason.KILLED);

            return;
        }

        LivingEntity target = null;

        for (LivingEntity entity : entitiesAround) {
            if (entity == null || !entity.isAlive())
                continue;

            target = entity;

            break;
        }

        if (target == null || !target.isAlive()) {
            this.remove(Entity.RemovalReason.KILLED);

            return;
        }

        this.setTarget(target);
    }

    @Override
    public void tick() {
        super.tick();

        ShadowGlaiveItem.Stats config = ShadowGlaiveItem.INSTANCE.getStats();

        for (int i = 0; i < 3; i++)
            level.addParticle(new SparkTintData(new Color(255, random.nextInt(100), 255), 0.2F, 30),
                    this.xo, this.yo, this.zo, MathUtils.randomFloat(random) * 0.01F, 0, MathUtils.randomFloat(random) * 0.01F);

        if (level.isClientSide())
            return;

        if (!isBounced && target == null && this.tickCount > 30)
            this.remove(Entity.RemovalReason.KILLED);

        if (this.tickCount > 300)
            this.remove(Entity.RemovalReason.KILLED);

        if (target == null && this.tickCount > 10 && this.tickCount % 2 == 0) {
            this.locateNearestTarget();

            return;
        }

        if (target != null && target.isAlive()) {
            EntityUtils.moveTowardsPosition(this, target.position()
                    .add(0D, target.getBbHeight() * 0.5D, 0D), config.projectileSpeed);

            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(0.3D, 3D, 0.3D))) {
                if (entity == owner)
                    return;

                int bounces = entityData.get(BOUNCES);

                if (bounces > config.maxBounces)
                    this.remove(Entity.RemovalReason.KILLED);

                String bouncedEntitiesString = entityData.get(BOUNCED_ENTITIES);
                List<String> bouncedEntities = Arrays.asList(bouncedEntitiesString.split(","));

                entity.hurt(owner != null ? DamageSource.playerAttack(owner) : DamageSource.GENERIC, config.damage);

                if (!bouncedEntities.contains(entity.getUUID().toString())) {
                    entityData.set(BOUNCED_ENTITIES, bouncedEntitiesString + "," + entity.getUUID());

                    entityData.set(BOUNCES, bounces + 1);
                    isBounced = true;
                }

                this.locateNearestTarget();

                break;
            }
        } else
            this.locateNearestTarget();
    }

    @Override
    protected void onHit(@Nonnull HitResult rayTraceResult) {
        if (level.isClientSide())
            return;

        if (rayTraceResult.getType() == HitResult.Type.BLOCK
                && level.getBlockState(((BlockHitResult) rayTraceResult).getBlockPos()).getMaterial().blocksMotion())
            this.remove(Entity.RemovalReason.KILLED);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(BOUNCES, 0);
        entityData.define(OWNER, "");
        entityData.define(TARGET, "");
        entityData.define(BOUNCED_ENTITIES, "");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt(TAG_BOUNCES_AMOUNT, entityData.get(BOUNCES));
        tag.putString(TAG_OWNER_UUID, entityData.get(OWNER));
        tag.putString(TAG_TARGET_UUID, entityData.get(TARGET));
        tag.putString(TAG_BOUNCED_ENTITIES, entityData.get(BOUNCED_ENTITIES));

        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(BOUNCES, tag.getInt(TAG_BOUNCES_AMOUNT));
        entityData.set(OWNER, tag.getString(TAG_OWNER_UUID));
        entityData.set(TARGET, tag.getString(TAG_TARGET_UUID));
        entityData.set(BOUNCED_ENTITIES, tag.getString(TAG_BOUNCED_ENTITIES));

        super.readAdditionalSaveData(tag);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}