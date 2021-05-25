package it.hurts.sskirillss.relics.items.runes;

import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;

public class WaterRuneItem extends RuneItem {
    public WaterRuneItem() {
        super(new Color(0, 255, 215));
    }

    @Override
    public void applyAbility(World world, BlockPos pos) {
        Random random = world.getRandom();
        for (int i = 0; i < 10; ++i) {
            BlockPos blockPos = pos.offset(random.nextInt(10) - 5, random.nextInt(10) - 5, random.nextInt(10) - 5);
            if (world.isLoaded(blockPos) && world.getBlockState(blockPos).canOcclude() && world.isEmptyBlock(blockPos.above()) && random.nextFloat() <= 0.1F) {
                world.setBlockAndUpdate(blockPos.above(), Blocks.WATER.defaultBlockState().setValue(FlowingFluidBlock.LEVEL, 2));
                break;
            }
        }
    }
}