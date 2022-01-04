package it.hurts.sskirillss.relics.items.relics.necklace;

import it.hurts.sskirillss.relics.client.renderer.items.models.HolyLocketModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
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
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#ff6800", "#0087ff")
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.damageMultiplier * 100 - 100) + "%")
                        .arg((int) (stats.igniteChance * 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.healMultiplier * 100 - 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg(stats.invulnerabilityTime / 20.0F)
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (DurabilityUtils.isBroken(stack))
            return;

        if (livingEntity.invulnerableTime <= 10)
            NBTUtils.setBoolean(stack, TAG_IS_ACTIVE, true);
        else if (NBTUtils.getBoolean(stack, TAG_IS_ACTIVE, false)) {
            livingEntity.invulnerableTime += stats.invulnerabilityTime;

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
            Stats stats = INSTANCE.stats;
            Entity source = event.getSource().getEntity();

            if (!(source instanceof PlayerEntity))
                return;

            LivingEntity entity = event.getEntityLiving();

            if (!entity.isInvertedHealAndHarm())
                return;

            if (EntityUtils.findEquippedCurio((PlayerEntity) source, ItemRegistry.HOLY_LOCKET.get()).isEmpty())
                return;

            if (random.nextFloat() <= stats.igniteChance)
                entity.setSecondsOnFire(4);

            event.setAmount(event.getAmount() * stats.damageMultiplier);
        }

        @SubscribeEvent
        public static void onLivingHeal(LivingHealEvent event) {
            Stats stats = INSTANCE.stats;

            if (EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.HOLY_LOCKET.get()).isEmpty())
                return;

            event.setAmount(event.getAmount() * stats.healMultiplier);
        }
    }

    public static class Stats extends RelicStats {
        public float damageMultiplier = 1.5F;
        public float healMultiplier = 1.25F;
        public float igniteChance = 0.25F;
        public int invulnerabilityTime = 10;
    }
}