package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Multimap;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.AmphibianBootModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class AmphibianBootItem extends RelicItem<AmphibianBootItem.Stats> implements ICurioItem {
    public static AmphibianBootItem INSTANCE;

    public AmphibianBootItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.AQUATIC)
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .arg("+" + (int) (config.swimSpeedModifier * 100 - 100) + "%")
                        .build())
                .shift(ShiftTooltip.builder()
                        .arg(config.airSupplyModifier)
                        .build())
                .build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> result = super.getAttributeModifiers(slotContext, uuid, stack);

        if (!isBroken(stack))
            result.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier(uuid, Reference.MODID + ":" + "amphibian_boot_swim_speed",
                    config.swimSpeedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));

        return result;
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