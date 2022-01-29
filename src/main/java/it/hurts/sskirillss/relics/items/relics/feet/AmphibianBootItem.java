package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.client.renderer.items.models.AmphibianBootModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

public class AmphibianBootItem extends RelicItem<AmphibianBootItem.Stats> {
    public static AmphibianBootItem INSTANCE;

    public AmphibianBootItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#60c6b5", "#43645e")
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.swimSpeedModifier * 100 - 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg(stats.airSupplyModifier)
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
    public RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(ForgeMod.SWIM_SPEED.get(), stats.swimSpeedModifier))
                .build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new AmphibianBootModel();
    }

    public static class Stats extends RelicStats {
        public float swimSpeedModifier = 1.25F;
        public int airSupplyModifier = 2;
    }
}