package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.HolyLocketModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class HolyLocketItem extends RelicItem<HolyLocketItem.Stats> {
    private static final String TAG_IS_ACTIVE = "active";

    public static HolyLocketItem INSTANCE;

    public HolyLocketItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.DESERT)
                        .chance(0.05F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .arg("+" + (int) (config.damageMultiplier * 100 - 100) + "%")
                        .arg((int) (config.igniteChance * 100) + "%")
                        .build())
                .shift(ShiftTooltip.builder()
                        .arg("+" + (int) (config.healMultiplier * 100 - 100) + "%")
                        .build())
                .shift(ShiftTooltip.builder()
                        .arg(config.invulnerabilityTime / 20.0F)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (IRepairableItem.isBroken(stack))
            return;

        if (livingEntity.invulnerableTime <= 10)
            NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, true);
        else if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
            livingEntity.invulnerableTime += config.invulnerabilityTime;

            NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, false);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new HolyLocketModel();
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

            if (EntityUtils.findEquippedCurio((PlayerEntity) source, ItemRegistry.HOLY_LOCKET.get()).isEmpty())
                return;

            if (random.nextFloat() <= config.igniteChance)
                entity.setSecondsOnFire(4);

            event.setAmount(event.getAmount() * config.damageMultiplier);
        }

        @SubscribeEvent
        public static void onLivingHeal(LivingHealEvent event) {
            Stats config = INSTANCE.config;

            if (EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.HOLY_LOCKET.get()).isEmpty())
                return;

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