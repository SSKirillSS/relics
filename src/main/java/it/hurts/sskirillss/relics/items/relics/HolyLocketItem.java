package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

public class HolyLocketItem extends RelicItem<HolyLocketItem.Stats> {
    private static final String TAG_IS_ACTIVE = "active";

    public static HolyLocketItem INSTANCE;

    public HolyLocketItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.DESERT)
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.damageMultiplier * 100 - 100) + "%")
                        .varArg((int) (config.igniteChance * 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.healMultiplier * 100 - 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg(config.invulnerabilityTime / 20.0F)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity.invulnerableTime <= 10)
            NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, true);

        else if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
            livingEntity.invulnerableTime += config.invulnerabilityTime;

            NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, false);
        }
    }

    @Mod.EventBusSubscriber
    static class HolyLocketEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            Entity source = event.getSource().getEntity();

            if (!(source instanceof PlayerEntity))
                return;

            LivingEntity entity = event.getEntityLiving();

            if (!entity.isInvertedHealAndHarm())
                return;

            if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.HOLY_LOCKET.get(), (PlayerEntity) source).isPresent()) {
                if (random.nextFloat() <= config.igniteChance)
                    entity.setSecondsOnFire(4);

                event.setAmount(event.getAmount() * config.damageMultiplier);
            }
        }

        @SubscribeEvent
        public static void onLivingHeal(LivingHealEvent event) {
            Stats config = INSTANCE.config;
            LivingEntity entity = event.getEntityLiving();

            if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.HOLY_LOCKET.get(), entity).isPresent())
                event.setAmount(event.getAmount() * config.healMultiplier);
        }
    }

    public static class Stats extends RelicStats {
        public float damageMultiplier = 1.5F;
        public float healMultiplier = 1.25F;
        public float igniteChance = 0.25F;
        public int invulnerabilityTime = 10;
    }
}