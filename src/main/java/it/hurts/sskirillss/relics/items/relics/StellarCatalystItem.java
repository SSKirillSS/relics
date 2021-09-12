package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.entities.StellarCatalystProjectileEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Collections;
import java.util.List;

public class StellarCatalystItem extends RelicItem<StellarCatalystItem.Stats> implements ICurioItem {
    public static StellarCatalystItem INSTANCE;

    public StellarCatalystItem() {
        super(Rarity.EPIC);

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg((int) (config.chance * 100) + "%")
                        .varArg((int) (config.damageMultiplier * 100) + "%")
                        .build())
                .build();
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.END_CITY_TREASURE);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class MoonlightWellServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            Stats config = INSTANCE.config;

            if (!(event.getSource().getEntity() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();

            if (!CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.STELLAR_CATALYST.get(), player).isPresent())
                return;

            LivingEntity target = event.getEntityLiving();
            World world = target.getCommandSenderWorld();

            if (event.getAmount() > config.minDamage
                    && (world.isNight() || world.dimension() == World.END)
                    && world.canSeeSky(target.blockPosition())
                    && random.nextFloat() <= config.chance) {
                StellarCatalystProjectileEntity projectile = new StellarCatalystProjectileEntity((LivingEntity) event.getSource().getEntity(),
                        event.getEntityLiving(), event.getAmount() * config.damageMultiplier);

                projectile.setPos(target.getX(), Math.min(target.getCommandSenderWorld().getMaxBuildHeight(), Math.min(target.getCommandSenderWorld().getMaxBuildHeight(),
                        target.getY() + target.getCommandSenderWorld().getRandom().nextInt(config.additionalSummonHeight) + config.minSummonHeight)), target.getZ());
                projectile.owner = player;

                world.addFreshEntity(projectile);
            }
        }
    }

    public static class Stats extends RelicStats {
        public float chance = 0.15F;
        public float damageMultiplier = 0.75F;
        public int additionalSummonHeight = 20;
        public int minSummonHeight = 20;
        public int explosionRadius = 3;
        public float knockbackPower = 1.0F;
        public int minDamage = 3;
        public float projectileSpeed = 0.7F;
    }
}