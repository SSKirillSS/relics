package it.hurts.sskirillss.relics.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChairEntity extends Entity {
    public ChairEntity(EntityType<?> pEntityType, Level level) {
        super(pEntityType, level);

        this.noPhysics = true;
    }

    @Override
    public void tick() {
        if (this.getPassengers().isEmpty())
            this.discard();
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity pEntity) {
        return super.getPassengerRidingPosition(pEntity).add(0F, 0.1F, 0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
}