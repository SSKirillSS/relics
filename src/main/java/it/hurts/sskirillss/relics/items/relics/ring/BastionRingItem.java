package it.hurts.sskirillss.relics.items.relics.ring;

import com.mojang.datafixers.util.Pair;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;
import java.util.Optional;

public class BastionRingItem extends RelicItem {
    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("compass", RelicAbilityEntry.builder()
                                .maxLevel(0)
                                .build())
                        .ability("trade", RelicAbilityEntry.builder()
                                .requiredLevel(5)
                                .requiredPoints(2)
                                .stat("rolls", RelicAbilityStat.builder()
                                        .initialValue(0D, 1D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 200))
                .styleData(RelicStyleData.builder()
                        .borders("#008cd7", "#0a3484")
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        Level world = player.getCommandSenderWorld();

        if (world.isClientSide() || world.dimension() != Level.NETHER || DurabilityUtils.isBroken(stack))
            return;

        Piglin piglin = world.getNearestEntity(Piglin.class, TargetingConditions.DEFAULT, player,
                player.getX(), player.getY(), player.getZ(), player.getBoundingBox().inflate(5));

        if (piglin == null || piglin.getTarget() == player)
            return;

        ServerLevel serverLevel = (ServerLevel) world;

        ResourceKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation("bastion_remnant"));

        Optional<HolderSet<Structure>> optional = serverLevel.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY)
                .getHolder(ResourceKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation("bastion_remnant")))
                .map(HolderSet::direct);

        if (optional.isEmpty())
            return;

        Pair<BlockPos, Holder<Structure>> bastion = serverLevel.getChunkSource().getGenerator().findNearestMapStructure(serverLevel,
                optional.get(), player.blockPosition(), 100, false);

        if (bastion == null)
            return;

        BlockPos bastionPos = bastion.getFirst();

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

    @Override
    public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.BASTION_RING.get());

            if (stack.isEmpty())
                return;

            LivingEntity entity = event.getEntity();

            if (entity instanceof ZombifiedPiglin)
                addExperience(player, stack, 1);

            if (entity instanceof Piglin)
                addExperience(player, stack, 5);

            if (entity instanceof PiglinBrute)
                addExperience(player, stack, 10);
        }
    }
}