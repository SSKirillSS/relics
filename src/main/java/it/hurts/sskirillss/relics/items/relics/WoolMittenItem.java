package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class WoolMittenItem extends RelicItem<WoolMittenItem.Stats> {
    private static WoolMittenItem INSTANCE;

    public WoolMittenItem() {
        super(RelicData.builder()
                .config(Stats.class)
                .rarity(Rarity.COMMON)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.COLD)
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .arg((int) config.minDamage)
                        .arg((int) config.damagePerSecond)
                        .build())
                .build();
    }

    @Mod.EventBusSubscriber
    public static class WoolMittenEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;

            Entity source = event.getSource().getDirectEntity();

            if (!(source instanceof SnowballEntity))
                return;

            SnowballEntity snowball = (SnowballEntity) source;

            if (!(snowball.getOwner() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) snowball.getOwner();

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.WOOL_MITTEN.get()).isEmpty())
                return;

            event.setAmount(event.getAmount() + config.minDamage + (snowball.tickCount / 20F * config.damagePerSecond));
        }
    }

    public static class Stats extends RelicStats {
        public float minDamage = 1.0F;
        public float damagePerSecond = 1.0F;
    }
}