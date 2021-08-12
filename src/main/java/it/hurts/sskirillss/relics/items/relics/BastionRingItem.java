package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.List;

public class BastionRingItem extends RelicItem<BastionRingItem.Stats> implements ICurioItem {
    public BastionRingItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.bastion_ring.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.bastion_ring.shift_2"));
        return tooltip;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        World world = livingEntity.getCommandSenderWorld();
        if (world.isClientSide() || world.dimension() != World.NETHER) return;
        PiglinEntity piglin = world.getNearestLoadedEntity(PiglinEntity.class, EntityPredicate.DEFAULT, livingEntity,
                livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), livingEntity.getBoundingBox().inflate(config.locateRadius));
        if (piglin == null) return;
        ServerWorld serverWorld = (ServerWorld) world;
        BlockPos bastionPos = serverWorld.getChunkSource().getGenerator().findNearestMapFeature(serverWorld,
                Structure.BASTION_REMNANT, livingEntity.blockPosition(), 100, false);
        if (bastionPos == null) return;
        piglin.setDeltaMovement(0F, 0F, 0F);
        Vector3d currentVec = piglin.position();
        Vector3d finalVec = currentVec.add(new Vector3d(bastionPos.getX(), piglin.getY(),
                bastionPos.getZ()).subtract(currentVec).normalize().multiply(2, 2, 2));
        int distance = (int) Math.round(currentVec.distanceTo(finalVec)) * 20;
        for (int i = 0; i < distance; i++) {
            float x = (float) (((finalVec.x - currentVec.x) * i / distance) + currentVec.x);
            float z = (float) (((finalVec.z - currentVec.z) * i / distance) + currentVec.z);
            serverWorld.sendParticles(new CircleTintData(new Color(255, 240, 150), 0.2F - i * 0.00375F, 1, 0.99F, false),
                    x, piglin.getY() + (piglin.getBbHeight() / 1.75F), z, 1, 0F, 0F, 0F, 0);
        }
        float radius = 0.75F;
        for (int i = 0; i < 2; i++) {
            float angle = (0.02F * (piglin.tickCount * 3 + i * 160));
            double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle))) + piglin.getX();
            double extraZ = (double) (radius * MathHelper.cos(angle)) + piglin.getZ();
            serverWorld.sendParticles(new CircleTintData(new Color(255, 240, 150), 0.2F, 30, 0.95F, false),
                    extraX, piglin.getY() + (piglin.getBbHeight() / 1.75F), extraZ, 1, 0F, 0F, 0F, 0);
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.NETHER;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    public static class Stats extends RelicStats {
        public int locateRadius = 5;
    }
}