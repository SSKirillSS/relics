package it.hurts.sskirillss.relics.items.relics.necklace;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class SpiderNecklaceItem extends RelicItem<SpiderNecklaceItem.Stats> {
    public SpiderNecklaceItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#ffffff", "#aea5ac")
                .ability(AbilityTooltip.builder()
                        .build())
                .ability(AbilityTooltip.builder()
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
        if (DurabilityUtils.isBroken(stack) || livingEntity.isSpectator())
            return;

        if (livingEntity.horizontalCollision && livingEntity.zza > 0) {
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x(),
                    stats.climbSpeed, livingEntity.getDeltaMovement().z());
            livingEntity.fallDistance = 0F;
        }
    }

    public static class Stats extends RelicStats {
        public float climbSpeed = 0.2F;
    }
}