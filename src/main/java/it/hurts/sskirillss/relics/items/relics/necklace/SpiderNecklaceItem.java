package it.hurts.sskirillss.relics.items.relics.necklace;

import it.hurts.sskirillss.relics.client.renderer.items.models.SpiderNecklaceModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicLootData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpiderNecklaceItem extends RelicItem<SpiderNecklaceItem.Stats> {
    public SpiderNecklaceItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
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
                .loot(RelicLootData.builder()
                        .table(RelicUtils.Worldgen.CAVE)
                        .chance(0.15F)
                        .build())
                .loot(RelicLootData.builder()
                        .table(EntityType.SPIDER.getDefaultLootTable().toString())
                        .chance(0.001F)
                        .build())
                .loot(RelicLootData.builder()
                        .table(EntityType.CAVE_SPIDER.getDefaultLootTable().toString())
                        .chance(0.0075F)
                        .build())
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new SpiderNecklaceModel();
    }

    public static class Stats extends RelicStats {
        public float climbSpeed = 0.2F;
    }
}