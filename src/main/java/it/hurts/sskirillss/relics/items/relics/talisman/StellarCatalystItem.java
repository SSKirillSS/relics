package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigDataOld;
import it.hurts.sskirillss.relics.entities.FallingStarEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

public class StellarCatalystItem extends RelicItem<StellarCatalystItem.Stats> {
    public static StellarCatalystItem INSTANCE;

    public StellarCatalystItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicStyleData getStyle(ItemStack stack) {
        return RelicStyleData.builder()
                .borders("#0f4ca0", "#2f1d69")
                .ability(AbilityTooltip.builder()
                        .arg((int) (stats.chance * 100) + "%")
                        .arg((int) (stats.damageMultiplier * 100) + "%")
                        .build())
                .build();
    }

    @Override
    public RelicConfigDataOld<Stats> getConfigData() {
        return RelicConfigDataOld.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class StellarCatalystServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof Player) || (source instanceof IndirectEntityDamageSource
                    && source.getDirectEntity() instanceof FallingStarEntity))
                return;

            Player player = (Player) event.getSource().getEntity();

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.STELLAR_CATALYST.get()).isEmpty())
                return;

            LivingEntity target = event.getEntityLiving();
            Level world = target.getCommandSenderWorld();
            Random random = world.getRandom();

            if (world.isNight() && world.canSeeSky(target.blockPosition())
                    && random.nextFloat() <= stats.chance) {
                FallingStarEntity projectile = new FallingStarEntity((LivingEntity) event.getSource().getEntity(),
                        event.getEntityLiving(), event.getAmount() * stats.damageMultiplier);

                projectile.setPos(target.getX(), Math.min(target.getCommandSenderWorld().getMaxBuildHeight(), Math.min(target.getCommandSenderWorld().getMaxBuildHeight(),
                        target.getY() + target.getCommandSenderWorld().getRandom().nextInt(stats.additionalSummonHeight) + stats.minSummonHeight)), target.getZ());
                projectile.owner = player;

                world.addFreshEntity(projectile);
            }
        }
    }

    public static class Stats extends RelicStats {
        public float chance = 0.15F;
        public float damageMultiplier = 2.0F;
        public int additionalSummonHeight = 20;
        public int minSummonHeight = 20;
        public int explosionRadius = 3;
        public float knockbackPower = 1.0F;
        public float projectileSpeed = 0.9F;
    }
}