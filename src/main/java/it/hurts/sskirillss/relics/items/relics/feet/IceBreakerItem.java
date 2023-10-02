package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.api.events.common.LivingSlippingEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.entities.ShockwaveEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateInfo;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class IceBreakerItem extends RelicItem {
    public static final String TAG_FALLING = "falling";

    @Override
    public RelicData constructRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("sustainability", RelicAbilityEntry.builder()
                                .stat("modifier", RelicAbilityStat.builder()
                                        .initialValue(0.75, 0.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, -0.05D)
                                        .formatValue(value -> (int) (MathUtils.round(1 - value, 1) * 100))
                                        .build())
                                .build())
                        .ability("impact", RelicAbilityEntry.builder()
                                .maxLevel(10)
                                .active(AbilityCastType.INSTANTANEOUS, AbilityCastPredicate.builder()
                                        .predicate("falling", data -> {
                                                    Player player = data.getPlayer();

                                                    return PredicateInfo.builder()
                                                            .condition(!(player.onGround() || player.isSpectator()))
                                                            .build();
                                                }
                                        )
                                )
                                .stat("size", RelicAbilityStat.builder()
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.3D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value, 1))
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
                .attribute(new RelicAttributeModifier.Modifier(Attributes.KNOCKBACK_RESISTANCE, (float) AbilityUtils.getAbilityValue(stack, "sustainability", "modifier")))
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, AbilityCastType type, AbilityCastStage stage) {
        if (ability.equals("impact")) {
            NBTUtils.setBoolean(stack, TAG_FALLING, true);
        }
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof Player player))
            return;

        boolean isFalling = NBTUtils.getBoolean(stack, TAG_FALLING, false);

        if (!isFalling)
            return;

        Vec3 motion = player.getDeltaMovement();

        if (player.onGround() || player.isSpectator()) {
            NBTUtils.setBoolean(stack, TAG_FALLING, false);

            return;
        }

        player.stopFallFlying();
        player.getAbilities().flying = false;

        player.setDeltaMovement(motion.x(), Math.min(-0.01F, motion.y() * 1.075F), motion.z());
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onLivingSlipping(LivingSlippingEvent event) {
            if (event.getFriction() <= 0.6F || !(event.getEntity() instanceof Player player)
                    || player.isInWater() || player.isInLava())
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.ICE_BREAKER.get());

            if (stack.isEmpty())
                return;

            event.setFriction(0.6F);
        }

        @SubscribeEvent
        public static void onEntityFall(LivingFallEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.ICE_BREAKER.get());

            if (stack.isEmpty())
                return;

            float distance = event.getDistance();

            Level world = player.getCommandSenderWorld();

            if (distance < 2 || !NBTUtils.getBoolean(stack, TAG_FALLING, false))
                return;

            LevelingUtils.addExperience(player, stack, Math.min(10, Math.round(distance / 3F)));

            ShockwaveEntity shockwave = new ShockwaveEntity(world,
                    (int) Math.round(Math.min(AbilityUtils.getAbilityValue(stack, "impact", "size"), distance * 0.25D)),
                    (float) AbilityUtils.getAbilityValue(stack, "impact", "damage"));

            BlockPos pos = player.getOnPos();

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