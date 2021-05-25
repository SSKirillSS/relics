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

public class EarthRuneItem extends RuneItem {
    public EarthRuneItem() {
        super(new Color(80, 255, 0));
    }

    @Override
    public void applyAbility(World world, BlockPos pos) {
        Random random = world.getRandom();
        if (random.nextFloat() > 0.1F) return;
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos).inflate(5));
        if (entities.isEmpty()) return;
        LivingEntity target = entities.get(random.nextInt(entities.size()));
        Vector3d targetPos = target.position();
        if (!target.isFallFlying() && !target.isInWall()) target.teleportTo(targetPos.x(), targetPos.y() - 1.0F, targetPos.z());
    }
}