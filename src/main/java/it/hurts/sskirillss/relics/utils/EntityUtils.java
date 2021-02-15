package it.hurts.sskirillss.relics.utils;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class EntityUtils {
    public static void moveTowardsPosition(Entity entity, Vector3d targetPos, float speed) {
        Vector3d motion = targetPos.subtract(entity.getPositionVec());
        motion = motion.normalize().scale(speed);
        entity.setMotion(motion.x, motion.y, motion.z);
    }

    public static void teleportWithMount(Entity entity, ServerWorld targetWorld, Vector3d targetPos) {
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

    public static List<Integer> getSlotsWithItem(PlayerEntity player, Item item) {
        List<Integer> list = Lists.newArrayList();
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            if (player.inventory.getStackInSlot(i).getItem() == item) {
                list.add(i);
                if (i == player.inventory.getSizeInventory()) {
                    return list;
                }
            }
        }
        return list;
    }

    public static EntityRayTraceResult rayTraceEntity(Entity shooter, Predicate<Entity> filter, double distance) {
        World world = shooter.world;
        Vector3d startVec = shooter.getEyePosition(1.0F);
        Vector3d endVec = shooter.getEyePosition(1.0F).add(shooter.getLook(1.0F).scale(distance));
        double d0 = distance * distance;
        Entity entity = null;
        Vector3d vector3d = null;
        for(Entity entity1 : world.getEntitiesInAABBexcluding(shooter, shooter.getBoundingBox()
                .expand(shooter.getLook(1.0F).scale(distance * distance)).grow(1.0D), filter)) {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(entity1.getCollisionBorderSize());
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);
            if (axisalignedbb.contains(startVec)) {
                if (d0 >= 0.0D) {
                    entity = entity1;
                    vector3d = optional.orElse(startVec);
                    d0 = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vector3d vector3d1 = optional.get();
                double d1 = startVec.squareDistanceTo(vector3d1);
                if (d1 < d0 || d0 == 0.0D) {
                    if (entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity() && !entity1.canRiderInteract()) {
                        if (d0 == 0.0D) {
                            entity = entity1;
                            vector3d = vector3d1;
                        }
                    } else {
                        entity = entity1;
                        vector3d = vector3d1;
                        d0 = d1;
                    }
                }
            }
        }
        return entity == null ? null : new EntityRayTraceResult(entity, vector3d);
    }
}