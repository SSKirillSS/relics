package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ShadowGlaiveEntity extends ThrowableEntity {
    private static final DataParameter<Integer> BOUNCES = EntityDataManager.defineId(ShadowGlaiveEntity.class, DataSerializers.INT);
    private static final DataParameter<Float> DAMAGE = EntityDataManager.defineId(ShadowGlaiveEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<String> OWNER = EntityDataManager.defineId(ShadowGlaiveEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> TARGET = EntityDataManager.defineId(ShadowGlaiveEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> BOUNCED_ENTITIES = EntityDataManager.defineId(ShadowGlaiveEntity.class, DataSerializers.STRING);

    private static final String TAG_BOUNCES_AMOUNT = "bounces";
    private static final String TAG_DAMAGE_AMOUNT = "damage";
    private static final String TAG_OWNER_UUID = "owner";
    private static final String TAG_TARGET_UUID = "target";
    private static final String TAG_BOUNCED_ENTITIES = "entities";

    private float damage;
    private PlayerEntity owner;
    private LivingEntity target;

    public ShadowGlaiveEntity(EntityType<? extends ShadowGlaiveEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public ShadowGlaiveEntity(World world, LivingEntity throwerIn) {
        super(EntityRegistry.SHADOW_GLAIVE.get(), throwerIn, world);
    }

    public void setOwner(PlayerEntity playerIn) {
        this.owner = playerIn;
        if (playerIn != null)
            entityData.set(OWNER, playerIn.getUUID().toString());
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
        if (target != null)
            entityData.set(TARGET, target.getUUID().toString());
    }

    public void setDamage(float damage) {
        this.damage = damage;
        entityData.set(DAMAGE, damage);
    }

    private void locateNearestTarget() {
        int bounces = entityData.get(BOUNCES);
        if (level.getRandom().nextFloat() < Math.min(1, bounces * RelicsConfig.ShadowGlaive.ADDITIONAL_BOUNCE_CHANCE_MULTIPLIER.get())) {
            this.remove();
            return;
        }
        String bouncedEntitiesString = entityData.get(BOUNCED_ENTITIES);
        List<String> bouncedEntities = Arrays.asList(bouncedEntitiesString.split(","));
        List<LivingEntity> entitiesAround = level.getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(RelicsConfig.ShadowGlaive.ADDITIONAL_BOUNCE_RADIUS.get()));
        entitiesAround.removeIf(target -> bouncedEntities.contains(target.getUUID().toString()) || target == owner);
        entitiesAround.sort((o1, o2) -> (int) Math.round(o1.position().distanceTo(o2.position())));
        if (entitiesAround.isEmpty()) {
            this.remove();
            return;
        }
        LivingEntity target = null;
        for (LivingEntity entity : entitiesAround) {
            if (entity == null || !entity.isAlive()) continue;
            target = entity;
            break;
        }
        if (target == null || !target.isAlive()) {
            this.remove();
            return;
        }
        this.setTarget(target);
    }

    @Override
    public void tick() {
        super.tick();
        level.addParticle(new CircleTintData(new Color(0.5F, 0.05F, 0.7F), 0.1F, 40, 0.95F, false),
                this.xo, this.yo, this.zo, 0.0D, 0.0D, 0.0D);
        if (level.isClientSide()) return;
        if (this.tickCount > 300) this.remove();
        if (target == null) this.locateNearestTarget();
        if (target.isAlive()) EntityUtils.moveTowardsPosition(this, new Vector3d(target.getX(),
                target.getY() + target.getBbHeight() * 0.5F, target.getZ()), RelicsConfig.ShadowGlaive.MOVEMENT_SPEED.get().floatValue());
        else this.locateNearestTarget();
    }

    @Override
    protected void onHit(@Nonnull RayTraceResult rayTraceResult) {
        if (level.isClientSide()) return;
        switch (rayTraceResult.getType()) {
            case BLOCK: {
                if (level.getBlockState(((BlockRayTraceResult) rayTraceResult).getBlockPos()).canOcclude()) this.remove();
                break;
            }
            case ENTITY: {
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
                if (!(entityRayTraceResult.getEntity() instanceof LivingEntity)) return;
                LivingEntity entity = (LivingEntity) entityRayTraceResult.getEntity();
                if (entity == owner) return;
                int bounces = entityData.get(BOUNCES);
                if (bounces > RelicsConfig.ShadowGlaive.MAX_BOUNCES_AMOUNT.get()) this.remove();
                String bouncedEntitiesString = entityData.get(BOUNCED_ENTITIES);
                List<String> bouncedEntities = Arrays.asList(bouncedEntitiesString.split(","));
                if (!bouncedEntities.contains(entity.getUUID().toString())) {
                    entity.hurt(owner != null ? DamageSource.playerAttack(owner)
                            : DamageSource.GENERIC, Math.max(RelicsConfig.ShadowGlaive.MIN_DAMAGE_PER_BOUNCE.get().floatValue(),
                            damage - (bounces * (damage * RelicsConfig.ShadowGlaive.DAMAGE_MULTIPLIER_PER_BOUNCE.get().floatValue()))));
                    entityData.set(BOUNCED_ENTITIES, bouncedEntitiesString + "," + entity.getUUID());
                }
                entityData.set(BOUNCES, bounces + 1);
                this.locateNearestTarget();
                break;
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(BOUNCES, 0);
        entityData.define(DAMAGE, 0F);
        entityData.define(OWNER, "");
        entityData.define(TARGET, "");
        entityData.define(BOUNCED_ENTITIES, "");
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        tag.putInt(TAG_BOUNCES_AMOUNT, entityData.get(BOUNCES));
        tag.putFloat(TAG_DAMAGE_AMOUNT, entityData.get(DAMAGE));
        tag.putString(TAG_OWNER_UUID, entityData.get(OWNER));
        tag.putString(TAG_TARGET_UUID, entityData.get(TARGET));
        tag.putString(TAG_BOUNCED_ENTITIES, entityData.get(BOUNCED_ENTITIES));

        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        entityData.set(BOUNCES, tag.getInt(TAG_BOUNCES_AMOUNT));
        entityData.set(DAMAGE, tag.getFloat(TAG_DAMAGE_AMOUNT));
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}