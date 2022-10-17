package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.api.events.LivingSlippingEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.entities.ShockwaveEntity;
import it.hurts.sskirillss.relics.indev.*;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class IceBreakerItem extends RelicItem {
    public static IceBreakerItem INSTANCE;

    public IceBreakerItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicDataNew getNewData() {
        return RelicDataNew.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("sustainability", RelicAbilityEntry.builder()
                                .stat("modifier", RelicAbilityStat.builder()
                                        .initialValue(0.75, 0.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, -0.05D)
                                        .formatValue(value -> String.valueOf((int) (MathUtils.round(1 - value, 1) * 100)))
                                        .build())
                                .build())
                        .ability("impact", RelicAbilityEntry.builder()
                                .stat("size", RelicAbilityStat.builder()
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
                                        .build())
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 200))
                .styleData(RelicStyleData.builder()
                        .borders("#dc41ff", "#832698")
                        .build())
                .build();
    }

    @Override
    public RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(Attributes.KNOCKBACK_RESISTANCE, (float) getAbilityValue(stack, "sustainability", "modifier")))
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        Vec3 motion = player.getDeltaMovement();

        if (player.isOnGround() || player.getAbilities().flying || motion.y() > 0
                || player.isFallFlying() || player.isSpectator())
            return;

        player.setDeltaMovement(motion.x(), motion.y() * 1.075F, motion.z());
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onLivingSlipping(LivingSlippingEvent event) {
            if (event.getFriction() <= 0.6F || !(event.getEntityLiving() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.ICE_BREAKER.get());

            if (stack.isEmpty() || DurabilityUtils.isBroken(stack))
                return;

            event.setFriction(0.6F);
        }

        @SubscribeEvent
        public static void onEntityFall(LivingFallEvent event) {
            if (!(event.getEntityLiving() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.ICE_BREAKER.get());

            if (stack.isEmpty())
                return;

            float distance = event.getDistance();

            Level world = player.getCommandSenderWorld();

            if (distance < 2 || !player.isShiftKeyDown())
                return;

            ShockwaveEntity shockwave = new ShockwaveEntity(world,
                    (int) Math.round(Math.min(getAbilityValue(stack, "impact", "size"), distance * 0.25D)),
                    (float) getAbilityValue(stack, "impact", "damage"));

            BlockPos pos = player.getBlockPosBelowThatAffectsMyMovement();

            shockwave.setOwner(player);
            shockwave.setPos(pos.getX(), pos.getY(), pos.getZ());

            world.addFreshEntity(shockwave);

            world.playSound(null, player.blockPosition(), SoundEvents.WITHER_BREAK_BLOCK,
                    SoundSource.MASTER, 0.75F, 1.0F);
            world.addParticle(ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY(), player.getZ(), 0, 0, 0);

            event.setDamageMultiplier(0F);
        }
    }
}