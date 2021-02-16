package it.hurts.sskirillss.relics.utils;

import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldUtils {
    public static BlockPos getSolidBlockUnderFeet(World world, BlockPos blockPos) {
        for (BlockPos pos = blockPos.down(); pos.getY() > 0; pos = pos.down()) {
            if(world.getBlockState(pos).getBlock() != Blocks.AIR && !(world.getBlockState(pos).getBlock() instanceof BushBlock)) {
                return pos;
            }
        }
        return null;
    }
}