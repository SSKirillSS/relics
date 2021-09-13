package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.items.relics.ShadowGlaiveItem;
import it.hurts.sskirillss.relics.particles.spark.SparkTintData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    private boolean isBounced = false;
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

    private void locateNearestTarget() {
        ShadowGlaiveItem.Stats config = ShadowGlaiveItem.INSTANCE.getConfig();
        int bounces = entityData.get(BOUNCES);

        if (level.getRandom().nextFloat() > 1.0F - (bounces * config.bounceChanceMultiplier)) {
            this.remove();

            return;
        }

        List<String> bouncedEntities = Arrays.asList(entityData.get(BOUNCED_ENTITIES).split(","));
        List<LivingEntity> entitiesAround = level.getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(config.bounceRadius));

        entitiesAround = entitiesAround.stream()
                .filter(entity -> !bouncedEntities.contains(entity.getUUID().toString()))
                .filter(entity -> entity != owner)
                .sorted(Comparator.comparing(entity -> entity.position().distanceTo(this.position())))
                .collect(Collectors.toList());

        if (entitiesAround.isEmpty()) {
            if (isBounced)
                this.remove();

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
            this.remove();

            return;
        }

        this.setTarget(target);
    }

    @Override
    public void tick() {
        super.tick();

        ShadowGlaiveItem.Stats config = ShadowGlaiveItem.INSTANCE.getConfig();

        for (int i = 0; i < 3; i++)
            level.addParticle(new SparkTintData(new Color(255, random.nextInt(100), 255), 0.2F, 30),
                    this.xo, this.yo, this.zo, MathUtils.randomFloat(random) * 0.01F, 0, MathUtils.randomFloat(random) * 0.01F);

        if (level.isClientSide())
            return;

        if (!isBounced && target == null && this.tickCount > 30)
            this.remove();

        if (this.tickCount > 300)
            this.remove();

        if (target == null) {
            this.locateNearestTarget();

            return;
        }

        if (target.isAlive())
            EntityUtils.moveTowardsPosition(this, new Vector3d(target.getX() + target.getBbWidth() * 0.5F,
                    target.getY() + target.getBbHeight() * 0.5F, target.getZ() + target.getBbWidth() * 0.5F), config.projectileSpeed);
        else
            this.locateNearestTarget();
    }

    @Override
    protected void onHit(@Nonnull RayTraceResult rayTraceResult) {
        if (level.isClientSide())
            return;

        ShadowGlaiveItem.Stats config = ShadowGlaiveItem.INSTANCE.getConfig();

        switch (rayTraceResult.getType()) {
            case BLOCK: {
                if (level.getBlockState(((BlockRayTraceResult) rayTraceResult).getBlockPos()).getMaterial().blocksMotion())
                    this.remove();

                break;
            }
            case ENTITY: {
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;

                if (!(entityRayTraceResult.getEntity() instanceof LivingEntity))
                    return;

                LivingEntity entity = (LivingEntity) entityRayTraceResult.getEntity();

                if (entity == owner)
                    return;

                int bounces = entityData.get(BOUNCES);

                if (bounces > config.maxBounces)
                    this.remove();

                String bouncedEntitiesString = entityData.get(BOUNCED_ENTITIES);
                List<String> bouncedEntities = Arrays.asList(bouncedEntitiesString.split(","));

                if (!bouncedEntities.contains(entity.getUUID().toString())) {
                    entity.hurt(owner != null ? DamageSource.playerAttack(owner) : DamageSource.GENERIC, config.damage);

                    entityData.set(BOUNCED_ENTITIES, bouncedEntitiesString + "," + entity.getUUID());
                }

                entityData.set(BOUNCES, bounces + 1);
                isBounced = true;

                this.locateNearestTarget();

                break;
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(BOUNCES, 0);
        entityData.define(OWNER, "");
        entityData.define(TARGET, "");
        entityData.define(BOUNCED_ENTITIES, "");
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        tag.putInt(TAG_BOUNCES_AMOUNT, entityData.get(BOUNCES));
        tag.putString(TAG_OWNER_UUID, entityData.get(OWNER));
        tag.putString(TAG_TARGET_UUID, entityData.get(TARGET));
        tag.putString(TAG_BOUNCED_ENTITIES, entityData.get(BOUNCED_ENTITIES));

        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}