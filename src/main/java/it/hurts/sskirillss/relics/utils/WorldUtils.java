package it.hurts.sskirillss.relics.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WorldUtils {
    @Nullable
    public static BlockPos getSolidBlockUnderFeet(Level world, BlockPos blockPos) {
        for (BlockPos pos = blockPos.below(); pos.getY() > world.getMinBuildHeight(); pos = pos.below())
            if (world.getBlockState(pos).getMaterial().blocksMotion())
                return pos;

        return null;
    }

    public static List<BlockPos> getBlockSphere(BlockPos center, double radius) {
        List<BlockPos> sphere = new ArrayList<>((int) Math.pow(radius, 3));

        for (int i = -(int) radius; i <= radius; i++) {
            float r1 = Mth.sqrt((float) (radius * radius - i * i));

            for (int j = -(int) r1; j <= r1; j++) {
                float r2 = Mth.sqrt((float) (radius * radius - i * i - j * j));

                for (int k = -(int) r2; k <= r2; k++)
                    sphere.add(center.offset(i, j, k));

            }
        }

        return sphere;
    }

    public static double getGroundHeight(Level level, Vec3 position, int iterations) {
        HitResult result = level.clip(new ClipContext(position, position.add(0, -iterations, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));

        if (result.getType() == HitResult.Type.BLOCK)
            return result.getLocation().y();

        return -level.getMaxBuildHeight();
    }

    public static double getGroundDistance(Level level, Vec3 position, int iterations) {
        return Math.max(0, position.y() - getGroundHeight(level, position, iterations));
    }
}