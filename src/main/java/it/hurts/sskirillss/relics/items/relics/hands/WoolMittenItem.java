package it.hurts.sskirillss.relics.items.relics.hands;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class WoolMittenItem extends RelicItem<WoolMittenItem.Stats> {
    private static WoolMittenItem INSTANCE;

    public WoolMittenItem() {
        super(RelicData.builder()
                .rarity(Rarity.COMMON)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#db9d74", "#634733")
                .ability(AbilityTooltip.builder()
                        .arg((int) stats.minDamage)
                        .arg((int) stats.damagePerSecond)
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Mod.EventBusSubscriber
    public static class WoolMittenEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;

            Entity source = event.getSource().getDirectEntity();

            if (!(source instanceof Snowball snowball))
                return;

            if (!(snowball.getOwner() instanceof Player player))
                return;

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.WOOL_MITTEN.get()).isEmpty())
                return;

            event.setAmount(event.getAmount() + stats.minDamage + (snowball.tickCount / 20F * stats.damagePerSecond));
        }
    }

    public static class Stats extends RelicStats {
        public float minDamage = 1.0F;
        public float damagePerSecond = 1.0F;
    }
}