package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.client.renderer.items.models.IceBreakerModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicLootData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttribute;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class IceBreakerItem extends RelicItem<IceBreakerItem.Stats> {

    public static IceBreakerItem INSTANCE;

    public IceBreakerItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#6098f4", "#16418a")
                .ability(AbilityTooltip.builder()
                        .negative()
                        .arg("-" + (int) Math.abs(stats.speedModifier * 100) + "%")
                        .arg("+" + (int) ((stats.fallMotionMultiplier - 1) * 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.knockbackResistanceModifier * 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .active(Minecraft.getInstance().options.keyShift)
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .loot(RelicLootData.builder()
                        .table(RelicUtils.Worldgen.COLD)
                        .chance(0.1F)
                        .build())
                .build();
    }

    @Override
    public RelicAttribute getAttributes(ItemStack stack) {
        return RelicAttribute.builder()
                .attribute(new RelicAttribute.Modifier(Attributes.MOVEMENT_SPEED, stats.speedModifier))
                .attribute(new RelicAttribute.Modifier(Attributes.KNOCKBACK_RESISTANCE, stats.knockbackResistanceModifier))
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity) || DurabilityUtils.isBroken(stack))
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;
        Vector3d motion = player.getDeltaMovement();

        if (player.isOnGround() || player.abilities.flying || motion.y() > 0
                || player.isFallFlying() || player.isSpectator())
            return;

        player.setDeltaMovement(motion.x(), motion.y() * stats.fallMotionMultiplier, motion.z());
        player.getCommandSenderWorld().addParticle(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new IceBreakerModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class IceBreakerServerEvents {
        @SubscribeEvent
        public static void onEntityFall(LivingFallEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntityLiving() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.ICE_BREAKER.get()).isEmpty())
                return;

            float distance = event.getDistance();
            World world = player.getCommandSenderWorld();

            if (distance < 2 || !player.isShiftKeyDown())
                return;

            world.playSound(null, player.blockPosition(), SoundEvents.WITHER_BREAK_BLOCK,
                    SoundCategory.PLAYERS, 0.75F, 1.0F);
            world.addParticle(ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY(), player.getZ(), 0, 0, 0);

            player.getCooldowns().addCooldown(ItemRegistry.ICE_BREAKER.get(), Math.round(distance * stats.stompCooldownMultiplier * 20));

            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(distance * stats.stompRadiusMultiplier))) {
                if (entity == player)
                    continue;

                entity.hurt(DamageSource.playerAttack(player), Math.min(stats.maxDealtDamage,
                        distance * stats.dealtDamageMultiplier));
                entity.setDeltaMovement(entity.position().subtract(player.position()).add(0, 1.005F, 0)
                        .multiply(stats.stompMotionMultiplier, stats.stompMotionMultiplier, stats.stompMotionMultiplier));
            }

            event.setDamageMultiplier(stats.incomingFallDamageMultiplier);
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = -0.1F;
        public float knockbackResistanceModifier = 0.5F;
        public float fallMotionMultiplier = 1.075F;
        public float stompCooldownMultiplier = 1.5F;
        public float stompRadiusMultiplier = 0.5F;
        public float stompMotionMultiplier = 1.005F;
        public float dealtDamageMultiplier = 1.0F;
        public int maxDealtDamage = 100;
        public float incomingFallDamageMultiplier = 0.0F;
    }
}