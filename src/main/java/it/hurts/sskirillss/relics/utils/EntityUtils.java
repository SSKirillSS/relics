package it.hurts.sskirillss.relics.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class EntityUtils {
    public static void moveTowardsPosition(Entity entity, Vector3d targetPos, float speed) {
        Vector3d motion = targetPos.subtract(entity.getPositionVec());
        motion = motion.normalize().scale(speed);
        entity.setMotion(motion.x, motion.y, motion.z);
    }

    public static void teleportWithMount(Entity entity, ServerWorld targetWorld, BlockPos targetPos) {
        if (entity.getRidingEntity() != null) {
            Entity mount = entity.getRidingEntity();
            entity.stopRiding();
            if (entity.getEntityWorld() != targetWorld) entity.changeDimension(targetWorld);
            else mount.setPositionAndUpdate(targetPos.getX(), targetPos.getY(), targetPos.getZ());
        } else {
            if (entity.getEntityWorld() != targetWorld) entity.changeDimension(targetWorld);
        }
        entity.setPositionAndUpdate(targetPos.getX(), targetPos.getY(), targetPos.getZ());
    }

    public static int getSlotWithItem(PlayerEntity player, Item item) {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            if (player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }
}