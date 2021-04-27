package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.particles.CircleTintData;
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
    private static final DataParameter<Integer> BOUNCES = EntityDataManager.createKey(ShadowGlaiveEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(ShadowGlaiveEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<String> OWNER = EntityDataManager.createKey(ShadowGlaiveEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> TARGET = EntityDataManager.createKey(ShadowGlaiveEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> BOUNCED_ENTITIES = EntityDataManager.createKey(ShadowGlaiveEntity.class, DataSerializers.STRING);

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
            dataManager.set(OWNER, playerIn.getUniqueID().toString());
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
        if (target != null)
            dataManager.set(TARGET, target.getUniqueID().toString());
    }

    public void setDamage(float damage) {
        this.damage = damage;
        dataManager.set(DAMAGE, damage);
    }

    private void locateNearestTarget() {
        int bounces = dataManager.get(BOUNCES);
        if (world.getRandom().nextFloat() < Math.min(1, bounces * RelicsConfig.ShadowGlaive.ADDITIONAL_BOUNCE_CHANCE_MULTIPLIER.get())) {
            this.remove();
            return;
        }
        String bouncedEntitiesString = dataManager.get(BOUNCED_ENTITIES);
        List<String> bouncedEntities = Arrays.asList(bouncedEntitiesString.split(","));
        List<LivingEntity> entitiesAround = world.getEntitiesWithinAABB(LivingEntity.class,
                this.getBoundingBox().grow(RelicsConfig.ShadowGlaive.ADDITIONAL_BOUNCE_RADIUS.get()));
        entitiesAround.removeIf(target -> bouncedEntities.contains(target.getUniqueID().toString()) || target == owner);
        entitiesAround.sort((o1, o2) -> (int) Math.round(o1.getPositionVec().distanceTo(o2.getPositionVec())));
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
        world.addParticle(new CircleTintData(new Color(0.5F, 0.05F, 0.7F), 0.1F, 40, 0.95F, false),
                this.prevPosX, this.prevPosY, this.prevPosZ, 0.0D, 0.0D, 0.0D);
        if (world.isRemote()) return;
        if (this.ticksExisted > 300) this.remove();
        if (target == null) this.locateNearestTarget();
        if (target.isAlive()) EntityUtils.moveTowardsPosition(this, new Vector3d(target.getPosX(),
                target.getPosY() + target.getHeight() * 0.5F, target.getPosZ()), RelicsConfig.ShadowGlaive.MOVEMENT_SPEED.get().floatValue());
        else this.locateNearestTarget();
    }

    @Override
    protected void onImpact(@Nonnull RayTraceResult rayTraceResult) {
        if (world.isRemote()) return;
        switch (rayTraceResult.getType()) {
            case BLOCK: {
                if (world.getBlockState(((BlockRayTraceResult) rayTraceResult).getPos()).isSolid()) this.remove();
                break;
            }
            case ENTITY: {
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
                if (!(entityRayTraceResult.getEntity() instanceof LivingEntity)) return;
                LivingEntity entity = (LivingEntity) entityRayTraceResult.getEntity();
                if (entity == owner) return;
                int bounces = dataManager.get(BOUNCES);
                if (bounces > RelicsConfig.ShadowGlaive.MAX_BOUNCES_AMOUNT.get()) this.remove();
                String bouncedEntitiesString = dataManager.get(BOUNCED_ENTITIES);
                List<String> bouncedEntities = Arrays.asList(bouncedEntitiesString.split(","));
                if (!bouncedEntities.contains(entity.getUniqueID().toString())) {
                    entity.attackEntityFrom(owner != null ? DamageSource.causePlayerDamage(owner)
                            : DamageSource.GENERIC, Math.max(RelicsConfig.ShadowGlaive.MIN_DAMAGE_PER_BOUNCE.get().floatValue(),
                            damage - (bounces * (damage * RelicsConfig.ShadowGlaive.DAMAGE_MULTIPLIER_PER_BOUNCE.get().floatValue()))));
                    dataManager.set(BOUNCED_ENTITIES, bouncedEntitiesString + "," + entity.getUniqueID());
                }
                dataManager.set(BOUNCES, bounces + 1);
                this.locateNearestTarget();
                break;
            }
        }
    }

    @Override
    protected void registerData() {
        dataManager.register(BOUNCES, 0);
        dataManager.register(DAMAGE, 0F);
        dataManager.register(OWNER, "");
        dataManager.register(TARGET, "");
        dataManager.register(BOUNCED_ENTITIES, "");
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        tag.putInt(TAG_BOUNCES_AMOUNT, dataManager.get(BOUNCES));
        tag.putFloat(TAG_DAMAGE_AMOUNT, dataManager.get(DAMAGE));
        tag.putString(TAG_OWNER_UUID, dataManager.get(OWNER));
        tag.putString(TAG_TARGET_UUID, dataManager.get(TARGET));
        tag.putString(TAG_BOUNCED_ENTITIES, dataManager.get(BOUNCED_ENTITIES));

        super.writeAdditional(tag);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        dataManager.set(BOUNCES, tag.getInt(TAG_BOUNCES_AMOUNT));
        dataManager.set(DAMAGE, tag.getFloat(TAG_DAMAGE_AMOUNT));
        dataManager.set(OWNER, tag.getString(TAG_OWNER_UUID));
        dataManager.set(TARGET, tag.getString(TAG_TARGET_UUID));
        dataManager.set(BOUNCED_ENTITIES, tag.getString(TAG_BOUNCED_ENTITIES));

        super.readAdditional(tag);
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}