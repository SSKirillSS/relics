package it.hurts.sskirillss.relics.items.runes;

import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class FireRuneItem extends RuneItem {
    public FireRuneItem() {
        super(new Color(255, 0, 0));
    }

    @Override
    public void applyAbility(World world, BlockPos pos) {
        Random random = world.getRandom();
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos).inflate(5));
        if (!entities.isEmpty() && random.nextFloat() <= 0.25F) {
            LivingEntity target = entities.get(random.nextInt(entities.size()));
            target.setSecondsOnFire(5);
        }
        for (int i = 0; i < 10; ++i) {
            BlockPos blockPos = pos.offset(random.nextInt(10) - 5, random.nextInt(10) - 5, random.nextInt(10) - 5);
            if (world.isLoaded(blockPos) && world.getBlockState(blockPos).canOcclude() && world.isEmptyBlock(blockPos.above()) && random.nextFloat() <= 0.1F) {
                world.setBlockAndUpdate(blockPos.above(), Blocks.FIRE.defaultBlockState());
                break;
            }
        }
    }
}