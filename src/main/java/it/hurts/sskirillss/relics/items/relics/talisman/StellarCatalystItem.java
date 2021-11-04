package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.ConfigData;
import it.hurts.sskirillss.relics.configs.data.LootData;
import it.hurts.sskirillss.relics.entities.StellarCatalystProjectileEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class StellarCatalystItem extends RelicItem<StellarCatalystItem.Stats> {
    public static StellarCatalystItem INSTANCE;

    public StellarCatalystItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .arg((int) (stats.chance * 100) + "%")
                        .arg((int) (stats.damageMultiplier * 100) + "%")
                        .build())
                .build();
    }

    @Override
    public ConfigData<Stats> getConfigData() {
        return ConfigData.<Stats>builder()
                .stats(new Stats())
                .loot(LootData.builder()
                        .table(LootTables.END_CITY_TREASURE.toString())
                        .chance(0.05F)
                        .build())
                .build();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class StellarCatalystServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof PlayerEntity) || (source instanceof IndirectEntityDamageSource
                    && source.getDirectEntity() instanceof StellarCatalystProjectileEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.STELLAR_CATALYST.get()).isEmpty())
                return;

            LivingEntity target = event.getEntityLiving();
            World world = target.getCommandSenderWorld();

            if (world.isNight() && world.canSeeSky(target.blockPosition())
                    && random.nextFloat() <= stats.chance) {
                StellarCatalystProjectileEntity projectile = new StellarCatalystProjectileEntity((LivingEntity) event.getSource().getEntity(),
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