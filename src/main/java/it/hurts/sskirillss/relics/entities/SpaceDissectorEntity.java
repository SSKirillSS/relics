package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.SpaceDissectorItem;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;

public class SpaceDissectorEntity extends ThrowableEntity {
    private static final DataParameter<Integer> UPDATE_TIME = EntityDataManager.createKey(SpaceDissectorEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Boolean> IS_RETURNING = EntityDataManager.createKey(SpaceDissectorEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BOUNCES = EntityDataManager.createKey(SpaceDissectorEntity.class, DataSerializers.VARINT);
    private static final DataParameter<String> OWNER = EntityDataManager.createKey(SpaceDissectorEntity.class, DataSerializers.STRING);

    private static final String TAG_UPDATE_TIME = "time";
    private static final String TAG_IS_RETURNING = "returning";
    private static final String TAG_BOUNCES_AMOUNT = "bounces";
    private static final String TAG_OWNER_UUID = "owner";

    private static boolean bounced = false;
    public ItemStack stack = ItemStack.EMPTY;
    private PlayerEntity owner;
    private static int time;

    public SpaceDissectorEntity(EntityType<? extends SpaceDissectorEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public SpaceDissectorEntity(World world, LivingEntity throwerIn) {
        super(EntityRegistry.SPACE_DISSECTOR.get(), throwerIn, world);
    }

    public void setOwner(PlayerEntity playerIn) {
        this.owner = playerIn;
        if (playerIn != null)
            dataManager.set(OWNER, playerIn.getUniqueID().toString());
    }

    @Override
    public void tick() {
        Vector3d defaultMotion = getMotion();

        super.tick();

        CircleTintData circleTintData = new CircleTintData(
                new Color(0.5F, 0.05F, 0.7F), 0.1F, 40, 0.95F, false);
        world.addParticle(circleTintData, this.prevPosX, this.prevPosY, this.prevPosZ, 0.0D, 0.0D, 0.0D);

        if (!world.isRemote()) {
            if (this.ticksExisted % 20 == 0) {
                if (dataManager.get(UPDATE_TIME) > RelicsConfig.SpaceDissector.MAX_THROWN_TIME.get()) {
                    if (owner != null && stack != null && stack != ItemStack.EMPTY) {
                        owner.getCooldownTracker().setCooldown(stack.getItem(), RelicsConfig.SpaceDissector.COOLDOWN_AFTER_RETURN.get() * 20);
                        NBTUtils.setBoolean(stack, SpaceDissectorItem.TAG_IS_THROWN, false);
                    }
                    this.remove();
                }

                if (!dataManager.get(IS_RETURNING)) {
                    if (dataManager.get(UPDATE_TIME) < RelicsConfig.SpaceDissector.TIME_BEFORE_RETURN.get()) {
                        dataManager.set(UPDATE_TIME, dataManager.get(UPDATE_TIME) + 1);
                    } else {
                        dataManager.set(IS_RETURNING, true);
                    }
                }
            }

            if (!dataManager.get(IS_RETURNING)) {
                if (!bounced) setMotion(defaultMotion);
            } else {
                if (owner != null) {
                    EntityUtils.moveTowardsPosition(this, new Vector3d(owner.getPosX(),
                            owner.getPosY() + 1.0F, owner.getPosZ()), RelicsConfig.SpaceDissector.MOVEMENT_SPEED.get().floatValue());
                    for (PlayerEntity player : world.getEntitiesWithinAABB(PlayerEntity.class, this.getBoundingBox().grow(2.0F))) {
                        if (owner.getUniqueID().equals(player.getUniqueID()) && stack != null && stack != ItemStack.EMPTY) {
                            this.remove();
                            NBTUtils.setBoolean(stack, SpaceDissectorItem.TAG_IS_THROWN, false);
                            owner.getCooldownTracker().setCooldown(stack.getItem(), RelicsConfig.SpaceDissector.COOLDOWN_AFTER_RETURN.get() * 20);
                        }
                    }
                } else {
                    if (stack != null && stack != ItemStack.EMPTY) NBTUtils.setBoolean(stack, SpaceDissectorItem.TAG_IS_THROWN, false);
                    this.remove();
                }
            }

            if (this.isBurning()) this.extinguish();

            time++;
            bounced = false;
        }
    }

    @Override
    protected void onImpact(@Nonnull RayTraceResult rayTraceResult) {
        switch (rayTraceResult.getType()) {
            case BLOCK: {
                BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTraceResult;
                if (world.getBlockState(blockRayTraceResult.getPos()).isSolid()) {
                    if (dataManager.get(BOUNCES) < RelicsConfig.SpaceDissector.MAX_BOUNCES_AMOUNT.get()) {
                        if (!dataManager.get(IS_RETURNING)) {
                            Direction dir = blockRayTraceResult.getFace();
                            Vector3d normalVector = new Vector3d(-2 * dir.getXOffset(), -2 * dir.getYOffset(), -2 * dir.getZOffset()).normalize();
                            setMotion(normalVector.mul(
                                    -2 * this.getMotion().dotProduct(normalVector),
                                    -2 * this.getMotion().dotProduct(normalVector),
                                    -2 * this.getMotion().dotProduct(normalVector))
                                    .add(this.getMotion()));
                            if (time > 5) {
                                world.playSound(null, getPosX(), getPosY(), getPosZ(), SoundRegistry.RICOCHET, SoundCategory.MASTER,
                                        0.5F, 0.75F + (rand.nextFloat() * 0.5F));
                                time = 0;
                            }
                            bounced = true;
                            dataManager.set(BOUNCES, dataManager.get(BOUNCES) + 1);
                            dataManager.set(UPDATE_TIME, Math.max(dataManager.get(UPDATE_TIME)
                                    - RelicsConfig.SpaceDissector.ADDITIONAL_TIME_PER_BOUNCE.get(), 0));
                        }
                    } else {
                        dataManager.set(IS_RETURNING, true);
                    }
                }
                break;
            }
            case ENTITY: {
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
                if (entityRayTraceResult.getEntity() instanceof LivingEntity) {
                    LivingEntity entity = (LivingEntity) entityRayTraceResult.getEntity();
                    if (owner != null && owner.getUniqueID().equals(entity.getUniqueID())) {
                        if (stack != null && stack != ItemStack.EMPTY) {
                            NBTUtils.setBoolean(stack, SpaceDissectorItem.TAG_IS_THROWN, false);
                            owner.getCooldownTracker().setCooldown(stack.getItem(), RelicsConfig.SpaceDissector.COOLDOWN_AFTER_RETURN.get() * 20);
                        }
                        this.remove();
                        break;
                    } else {
                        entity.attackEntityFrom(owner != null ? DamageSource.causePlayerDamage(owner) : DamageSource.GENERIC,
                                RelicsConfig.SpaceDissector.BASE_DAMAGE_AMOUNT.get().floatValue() + (dataManager.get(BOUNCES)
                                        * RelicsConfig.SpaceDissector.DAMAGE_MULTIPLIER_PER_BOUNCE.get().floatValue()));
                    }
                }
                break;
            }
        }
    }


    @Override
    protected void registerData() {
        dataManager.register(UPDATE_TIME, 0);
        dataManager.register(IS_RETURNING, false);
        dataManager.register(BOUNCES, 0);
        dataManager.register(OWNER, "");
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        tag.putInt(TAG_UPDATE_TIME, dataManager.get(UPDATE_TIME));
        tag.putString(TAG_OWNER_UUID, dataManager.get(OWNER));
        tag.putBoolean(TAG_IS_RETURNING, dataManager.get(IS_RETURNING));
        tag.putInt(TAG_BOUNCES_AMOUNT, dataManager.get(BOUNCES));
        stack.write(tag);
        super.writeAdditional(tag);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        dataManager.set(UPDATE_TIME, tag.getInt(TAG_UPDATE_TIME));
        dataManager.set(OWNER, tag.getString(TAG_OWNER_UUID));
        dataManager.set(IS_RETURNING, tag.getBoolean(TAG_IS_RETURNING));
        dataManager.set(BOUNCES, tag.getInt(TAG_BOUNCES_AMOUNT));
        stack = ItemStack.read(tag);
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