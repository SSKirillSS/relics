package it.hurts.sskirillss.relics.utils;

import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class WorldUtils {
    public static BlockPos getSolidBlockUnderFeet(World world, BlockPos blockPos) {
        for (BlockPos pos = blockPos.below(); pos.getY() > 0; pos = pos.below()) {
            if (world.getBlockState(pos).getBlock() != Blocks.AIR && !(world.getBlockState(pos).getBlock() instanceof BushBlock)) {
                return pos;
            }
        }
        return null;
    }

    public static List<BlockPos> getBlockSphere(BlockPos center, double radius) {
        List<BlockPos> sphere = new ArrayList<>((int) Math.pow(radius, 3));
        for (int i = -(int) radius; i <= radius; i++) {
            float r1 = MathHelper.sqrt(radius * radius - i * i);
            for (int j = -(int) r1; j <= r1; j++) {
                float r2 = MathHelper.sqrt(radius * radius - i * i - j * j);
                for (int k = -(int) r2; k <= r2; k++) {
                    sphere.add(center.offset(i, j, k));
                }
            }
        }
        return sphere;
    }
}