package it.hurts.sskirillss.relics.entities;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Optional;

public class SporeEntity extends ThrowableProjectile {
    @Getter
    @Setter
    private float size;

    @Getter
    @Setter
    private ItemStack stack = ItemStack.EMPTY;

    public SporeEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);

        this.size = 0.35F + level.getRandom().nextFloat() * 0.5F;
    }

    public SporeEntity(Level level) {
        super(EntityRegistry.SPORE.get(), level);

        this.size = 0.35F + level.getRandom().nextFloat() * 0.5F;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > 200)
            this.discard();

        Level level = this.getLevel();

        if (level.isClientSide())
            return;

        ((ServerLevel) level).sendParticles(new CircleTintData(new Color(level.getRandom().nextInt(200), 255, 0), size * 0.25F, 40, 0.9F, false),
                this.xo, this.yo, this.zo, 1, 0.025D, 0.025D, 0.025D, 0.01F);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockPos pos = result.getBlockPos();
        BlockState state = this.level.getBlockState(pos);

        if (!state.getMaterial().blocksMotion())
            return;

        this.level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER, 0.75F, 1.75F);

        if (this.getOwner() instanceof Player player) {
            Optional<PoisonedPuddleEntity> optional = level.getEntitiesOfClass(PoisonedPuddleEntity.class, this.getBoundingBox().inflate(1)).stream().findFirst();

            if (optional.isEmpty()) {
                PoisonedPuddleEntity puddle = new PoisonedPuddleEntity(player);

                puddle.setPos(this.position());
                puddle.setOwner(player);
                puddle.setStack(stack);
                puddle.setSize(1F);

                level.addFreshEntity(puddle);
            } else {
                PoisonedPuddleEntity puddle = optional.get();

                puddle.addSize((10F - puddle.getSize()) * 0.1F);
            }
        }

        this.discard();
    }

    @Override
    public void onRemovedFromWorld() {
        ParticleUtils.createBall(new CircleTintData(new Color(100 + level.getRandom().nextInt(50), 255, 0),
                0.2F, 40, 0.9F, true), this.position(), level, 1, 0.1F);

        super.onRemovedFromWorld();
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.size = compound.getFloat("size");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("size", this.size);
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