package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class SporeSackItem extends RelicItem<SporeSackItem.Stats> {
    public static SporeSackItem INSTANCE;

    public SporeSackItem() {
        super(RelicData.builder()
                .rarity(Rarity.UNCOMMON)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#398f00", "#006e00")
                .ability(AbilityTooltip.builder()
                        .arg((int) (stats.chance * 100) + "%")
                        .arg(stats.radius)
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SporeSackEvents {
        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntity() instanceof Projectile projectile))
                return;

            if (projectile.getOwner() == null || !(projectile.getOwner() instanceof Player player))
                return;

            Level world = projectile.getCommandSenderWorld();

            if (world.isClientSide())
                return;

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.SPORE_SACK.get()).isEmpty()
                    || world.getRandom().nextFloat() > stats.chance)
                return;

            world.playSound(null, projectile.blockPosition(), SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.PLAYERS, 1.0F, 0.5F);
            player.getCooldowns().addCooldown(ItemRegistry.SPORE_SACK.get(), stats.cooldown * 20);

            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(stats.radius))) {
                if (entity == player)
                    continue;

                entity.addEffect(new MobEffectInstance(MobEffects.POISON, stats.poisonDuration * 20, stats.poisonAmplifier));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, stats.slownessDuration * 20, stats.slownessAmplifier));
            }
        }
    }

    public static class Stats extends RelicStats {
        public float chance = 0.3F;
        public int radius = 3;
        public int cooldown = 5;
        public int poisonAmplifier = 2;
        public int poisonDuration = 5;
        public int slownessAmplifier = 0;
        public int slownessDuration = 5;
    }
}