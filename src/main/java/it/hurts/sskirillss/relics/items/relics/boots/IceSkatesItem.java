package it.hurts.sskirillss.relics.items.relics.boots;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.IceSkatesModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.WorldUtils;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class IceSkatesItem extends RelicItem<IceSkatesItem.Stats> implements ICurioItem {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "ice_skates_movement_speed", UUID.fromString("c0f5890f-a878-49bb-b24c-bbbf60d8539b"));

    public static IceSkatesItem INSTANCE;

    public IceSkatesItem() {
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
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (config.speedModifier * 100 - 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg((int) config.ramDamage)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        World world = livingEntity.getCommandSenderWorld();
        BlockPos pos = WorldUtils.getSolidBlockUnderFeet(world, livingEntity.blockPosition());

        if (pos == null || IRepairableItem.isBroken(stack))
            return;

        if (world.getBlockState(pos).is(BlockTags.ICE)) {
            EntityUtils.applyAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));

            if (livingEntity.isSprinting()) {
                if (livingEntity.isOnGround())
                    world.addParticle(ParticleTypes.SPIT, livingEntity.getX(),
                            livingEntity.getY() + 0.1F, livingEntity.getZ(), 0, 0.2F, 0);

                for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(config.ramRadius))) {
                    if (entity == livingEntity || entity.invulnerableTime > 0)
                        continue;

                    entity.setDeltaMovement(entity.position().subtract(livingEntity.position()).normalize().multiply(0.25F, 0.1F, 0.25F));
                    world.playSound(null, entity.blockPosition(), SoundEvents.AXE_STRIP,
                            SoundCategory.PLAYERS, 1.0F, 1.0F);
                    entity.hurt(DamageSource.FLY_INTO_WALL, config.ramDamage);
                }
            }
        } else
            EntityUtils.removeAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                    config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttributeModifier(slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED),
                new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new IceSkatesModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class IceSkatesEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;

            if (!(event.getEntityLiving() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            if (!EntityUtils.findEquippedCurio(player, ItemRegistry.ICE_SKATES.get()).isEmpty()
                    && player.getCommandSenderWorld().getBlockState(player.blockPosition().below()).is(BlockTags.ICE)
                    && event.getSource() == DamageSource.FALL)
                event.setAmount(event.getAmount() * config.fallingDamageMultiplier);
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 1.1F;
        public float ramRadius = 0.5F;
        public float ramDamage = 5.0F;
        public float fallingDamageMultiplier = 0.25F;
    }
}