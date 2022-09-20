package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.init.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class BlockSimulationEntity extends Entity {
    private static final EntityDataAccessor<Optional<BlockState>> BLOCK_STATE = SynchedEntityData.defineId(BlockSimulationEntity.class, EntityDataSerializers.BLOCK_STATE);

    public BlockSimulationEntity(EntityType<?> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public BlockSimulationEntity(Level level, BlockState state) {
        super(EntityRegistry.BLOCK_SIMULATION.get(), level);

        setBlockState(state);

        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();

        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.075D, 0.0D));

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (this.tickCount % 100 == 0 || (this.tickCount > 10 && this.level.getBlockState(this.blockPosition().above()).getMaterial().blocksMotion()))
            this.remove(RemovalReason.KILLED);
    }

    public void setBlockState(@Nullable BlockState state) {
        this.entityData.set(BLOCK_STATE, Optional.ofNullable(state));
    }

    @Nullable
    public BlockState getBlockState() {
        return this.entityData.get(BLOCK_STATE).orElse(null);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BLOCK_STATE, Optional.of(Blocks.AIR.defaultBlockState()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        setBlockState(NbtUtils.readBlockState(compound.getCompound("BlockState")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        BlockState state = getBlockState();

        if (state != null)
            compound.put("BlockState", NbtUtils.writeBlockState(state));
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