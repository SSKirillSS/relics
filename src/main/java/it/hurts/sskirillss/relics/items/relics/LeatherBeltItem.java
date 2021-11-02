package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.api.integration.curios.ISlotModifier;
import it.hurts.sskirillss.relics.api.integration.curios.SlotModifierData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.LeatherBeltModel;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

public class LeatherBeltItem extends RelicItem<RelicStats> implements ISlotModifier {
    public LeatherBeltItem() {
        super(RelicData.builder()
                .rarity(Rarity.COMMON)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.VILLAGE)
                        .chance(0.05F)
                        .build())
                .build());
    }

    @Override
    public SlotModifierData getSlotModifiers() {
        return SlotModifierData.builder()
                .entry(Pair.of("talisman", 3))
                .build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new LeatherBeltModel();
    }
}