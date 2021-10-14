package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Multimap;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.IceBreakerModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class IceBreakerItem extends RelicItem<IceBreakerItem.Stats> implements ICurioItem {

    public static IceBreakerItem INSTANCE;

    public IceBreakerItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .model(new IceBreakerModel())
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.COLD)
                        .chance(0.2F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .negative()
                        .varArg("-" + (int) Math.abs(config.speedModifier * 100) + "%")
                        .varArg("+" + (int) ((config.fallMotionMultiplier - 1) * 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.knockbackResistanceModifier * 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .active(Minecraft.getInstance().options.keyShift)
                        .build())
                .build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> result = super.getAttributeModifiers(slotContext, uuid, stack);

        if (!RelicItem.isBroken(stack)) {
            result.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, Reference.MODID + ":" + "ice_breaker_movement_speed",
                    config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
            result.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, Reference.MODID + ":" + "ice_breaker_knockback_resistance",
                    config.knockbackResistanceModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        return result;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity) || isBroken(stack))
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;
        Vector3d motion = player.getDeltaMovement();

        if (player.isOnGround() || player.isInWater() || motion.y() > 0)
            return;

        player.setDeltaMovement(motion.x(), motion.y() * config.fallMotionMultiplier, motion.z());
        player.getCommandSenderWorld().addParticle(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), 0, 0, 0);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class IceBreakerServerEvents {
        @SubscribeEvent
        public static void onEntityFall(LivingFallEvent event) {
            Stats config = INSTANCE.config;

            if (!(event.getEntityLiving() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ICE_BREAKER.get(), player).ifPresent(triple -> {
                ItemStack stack = triple.getRight();

                if (player.getCooldowns().isOnCooldown(stack.getItem()) || isBroken(stack))
                    return;

                float distance = event.getDistance();
                World world = player.getCommandSenderWorld();

                if (distance < config.minFallDistance || !player.isShiftKeyDown())
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
                    entity.setDeltaMovement(entity.position().subtract(player.position()).add(0, 1.005F, 0).multiply(
                            config.stompMotionMultiplier, config.stompMotionMultiplier, config.stompMotionMultiplier));
                }

                event.setDamageMultiplier(config.incomingFallDamageMultiplier);
            });
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = -0.1F;
        public float knockbackResistanceModifier = 0.5F;
        public int minFallDistance = 3;
        public float fallMotionMultiplier = 1.075F;
        public float stompCooldownMultiplier = 1.5F;
        public float stompRadiusMultiplier = 0.5F;
        public float stompMotionMultiplier = 1.005F;
        public float dealtDamageMultiplier = 1.0F;
        public int maxDealtDamage = 100;
        public float incomingFallDamageMultiplier = 0.0F;
    }
}