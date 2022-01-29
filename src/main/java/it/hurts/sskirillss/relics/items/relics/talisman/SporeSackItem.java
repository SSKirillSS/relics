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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

            if (!(event.getEntity() instanceof ProjectileEntity))
                return;

            ProjectileEntity projectile = (ProjectileEntity) event.getEntity();

            if (projectile.getOwner() == null || !(projectile.getOwner() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) projectile.getOwner();
            World world = projectile.getCommandSenderWorld();

            if (world.isClientSide())
                return;

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.SPORE_SACK.get()).isEmpty()
                    || world.getRandom().nextFloat() > stats.chance)
                return;

            ((ServerWorld) world).sendParticles(new RedstoneParticleData(0, 255, 0, 1),
                    projectile.getX(), projectile.getY(), projectile.getZ(), 100, 1, 1, 1, 0.5);
            world.playSound(null, projectile.blockPosition(), SoundEvents.FIRE_EXTINGUISH,
                    SoundCategory.PLAYERS, 1.0F, 0.5F);
            player.getCooldowns().addCooldown(ItemRegistry.SPORE_SACK.get(), stats.cooldown * 20);

            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(stats.radius))) {
                if (entity == player)
                    continue;

                entity.addEffect(new EffectInstance(Effects.POISON, stats.poisonDuration * 20, stats.poisonAmplifier));
                entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, stats.slownessDuration * 20, stats.slownessAmplifier));
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