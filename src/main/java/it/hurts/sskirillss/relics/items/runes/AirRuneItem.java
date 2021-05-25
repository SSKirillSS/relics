package it.hurts.sskirillss.relics.items.runes;

import it.hurts.sskirillss.relics.items.RuneItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class AirRuneItem extends RuneItem {
    public AirRuneItem() {
        super(new Color(255, 255, 255));
    }

    @Override
    public void applyAbility(World world, BlockPos pos) {
        Random random = world.getRandom();
        if (random.nextFloat() > 0.1F) return;
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos).inflate(5));
        if (entities.isEmpty()) return;
        LivingEntity target = entities.get(random.nextInt(entities.size()));
        target.setDeltaMovement(target.position().add(0, 2, 0).subtract(new Vector3d(pos.getX() + 0.5F,
                pos.getY() + 0.5F, pos.getZ() + 0.5F)).normalize().multiply(3, 2, 3));
    }
}