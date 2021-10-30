package it.hurts.sskirillss.relics.items.relics.boots;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttribute;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.IceBreakerModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
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
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class IceBreakerItem extends RelicItem<IceBreakerItem.Stats> implements ICurioItem {

    public static IceBreakerItem INSTANCE;

    public IceBreakerItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.COLD)
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .negative()
                        .arg("-" + (int) Math.abs(config.speedModifier * 100) + "%")
                        .arg("+" + (int) ((config.fallMotionMultiplier - 1) * 100) + "%")
                        .build())
                .shift(ShiftTooltip.builder()
                        .arg("+" + (int) (config.knockbackResistanceModifier * 100) + "%")
                        .build())
                .shift(ShiftTooltip.builder()
                        .active(Minecraft.getInstance().options.keyShift)
                        .build())
                .build();
    }

    @Override
    public RelicAttribute getAttributes(ItemStack stack) {
        return RelicAttribute.builder()
                .attribute(new RelicAttribute.Modifier(Attributes.MOVEMENT_SPEED, config.speedModifier))
                .attribute(new RelicAttribute.Modifier(Attributes.KNOCKBACK_RESISTANCE, config.knockbackResistanceModifier))
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity) || IRepairableItem.isBroken(stack))
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;
        Vector3d motion = player.getDeltaMovement();

        if (player.isOnGround() || player.abilities.flying || motion.y() > 0
                || player.isFallFlying() || player.isSpectator())
            return;

        player.setDeltaMovement(motion.x(), motion.y() * config.fallMotionMultiplier, motion.z());
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
            Stats config = INSTANCE.config;

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

            player.getCooldowns().addCooldown(ItemRegistry.ICE_BREAKER.get(), Math.round(distance * config.stompCooldownMultiplier * 20));

            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(distance * config.stompRadiusMultiplier))) {
                if (entity == player)
                    continue;

                entity.hurt(DamageSource.playerAttack(player), Math.min(config.maxDealtDamage,
                        distance * config.dealtDamageMultiplier));
                entity.setDeltaMovement(entity.position().subtract(player.position()).add(0, 1.005F, 0)
                        .multiply(config.stompMotionMultiplier, config.stompMotionMultiplier, config.stompMotionMultiplier));
            }

            event.setDamageMultiplier(config.incomingFallDamageMultiplier);
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