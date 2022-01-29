package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class IceSkatesItem extends RelicItem<IceSkatesItem.Stats> {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "ice_skates_movement_speed", UUID.fromString("c0f5890f-a878-49bb-b24c-bbbf60d8539b"));

    public static IceSkatesItem INSTANCE;

    public IceSkatesItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#7ba3e0", "#477d91")
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.speedModifier * 100 - 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg((int) stats.ramDamage)
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
        AttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        Level world = livingEntity.getCommandSenderWorld();
        BlockPos pos = WorldUtils.getSolidBlockUnderFeet(world, livingEntity.blockPosition());

        if (pos == null || DurabilityUtils.isBroken(stack))
            return;

        if (world.getBlockState(pos).is(BlockTags.ICE)) {
            EntityUtils.applyAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(), stats.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));

            if (livingEntity.isSprinting()) {
                if (livingEntity.isOnGround())
                    world.addParticle(ParticleTypes.SPIT, livingEntity.getX(),
                            livingEntity.getY() + 0.1F, livingEntity.getZ(), 0, 0.2F, 0);

                for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(stats.ramRadius))) {
                    if (entity == livingEntity || entity.invulnerableTime > 0)
                        continue;

                    entity.setDeltaMovement(entity.position().subtract(livingEntity.position()).normalize().multiply(0.25F, 0.1F, 0.25F));
                    world.playSound(null, entity.blockPosition(), SoundEvents.AXE_STRIP,
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.hurt(DamageSource.FLY_INTO_WALL, stats.ramDamage);
                }
            }
        } else
            EntityUtils.removeAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                    stats.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttributeModifier(slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED),
                new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(), stats.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class IceSkatesEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntityLiving() instanceof Player player))
                return;

            if (!EntityUtils.findEquippedCurio(player, ItemRegistry.ICE_SKATES.get()).isEmpty()
                    && player.getCommandSenderWorld().getBlockState(player.blockPosition().below()).is(BlockTags.ICE)
                    && event.getSource() == DamageSource.FALL)
                event.setAmount(event.getAmount() * stats.fallingDamageMultiplier);
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 1.1F;
        public float ramRadius = 0.5F;
        public float ramDamage = 5.0F;
        public float fallingDamageMultiplier = 0.25F;
    }
}