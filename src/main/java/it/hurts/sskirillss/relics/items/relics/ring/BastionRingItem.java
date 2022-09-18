package it.hurts.sskirillss.relics.items.relics.ring;

import com.mojang.datafixers.util.Pair;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class BastionRingItem extends RelicItem<BastionRingItem.Stats> {
    public BastionRingItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#d68600", "#341e00")
                .ability(AbilityTooltip.builder()
                        .build())
                .ability(AbilityTooltip.builder()
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        Level world = livingEntity.getCommandSenderWorld();

        if (world.isClientSide() || world.dimension() != Level.NETHER || DurabilityUtils.isBroken(stack))
            return;

        Piglin piglin = world.getNearestEntity(Piglin.class, TargetingConditions.DEFAULT, livingEntity,
                livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), livingEntity.getBoundingBox().inflate(stats.locateRadius));

        if (piglin == null || piglin.getTarget() == livingEntity)
            return;

        ServerLevel serverLevel = (ServerLevel) world;
        Registry<ConfiguredStructureFeature<?, ?>> registry = serverLevel.getLevel().registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        HolderSet<ConfiguredStructureFeature<?, ?>> holderset =  registry.getHolder(BuiltinStructures.BASTION_REMNANT).map((p_207529_) -> {
                return HolderSet.direct(p_207529_);
            }).get();

        Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> bastionPosPair = serverLevel.getChunkSource().getGenerator().findNearestMapFeature(serverLevel,
                holderset, livingEntity.blockPosition(), 100, false);

        if (bastionPosPair == null)
            return;
        BlockPos bastionPos = bastionPosPair.getFirst();

        piglin.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 255, false, false));

        Vec3 currentVec = piglin.position();
        Vec3 finalVec = currentVec.add(new Vec3(bastionPos.getX(), piglin.getY(),
                bastionPos.getZ()).subtract(currentVec).normalize().multiply(2, 2, 2));
        int distance = (int) Math.round(currentVec.distanceTo(finalVec)) * 20;

        for (int i = 0; i < distance; i++) {
            float x = (float) (((finalVec.x - currentVec.x) * i / distance) + currentVec.x);
            float z = (float) (((finalVec.z - currentVec.z) * i / distance) + currentVec.z);

            serverLevel.sendParticles(new CircleTintData(new Color(255, 240, 150), 0.2F - i * 0.00375F, 1, 0.99F, false),
                    x, piglin.getY() + (piglin.getBbHeight() / 1.75F), z, 1, 0F, 0F, 0F, 0);
        }

        for (int i = 0; i < 2; i++) {
            float angle = (0.02F * (piglin.tickCount * 3 + i * 160));
            double extraX = (double) (0.75F * Mth.sin((float) (Math.PI + angle))) + piglin.getX();
            double extraZ = (double) (0.75F * Mth.cos(angle)) + piglin.getZ();

            serverLevel.sendParticles(new CircleTintData(new Color(255, 240, 150), 0.2F, 30, 0.95F, false),
                    extraX, piglin.getY() + (piglin.getBbHeight() / 1.75F), extraZ, 1, 0F, 0F, 0F, 0);
        }
    }

    public static class Stats extends RelicStats {
        public int locateRadius = 5;
    }
}