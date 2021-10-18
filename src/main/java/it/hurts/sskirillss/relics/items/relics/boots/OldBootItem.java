package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Multimap;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.OldBootModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class OldBootItem extends RelicItem<OldBootItem.Stats> implements ICurioItem {
    public OldBootItem() {
        super(RelicData.builder()
                .rarity(Rarity.COMMON)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(LootTables.FISHING.toString())
                        .chance(0.1F)
                        .build())
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .arg("+" + (int) (config.speedModifier * 100) + "%")
                        .build())
                .build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> result = super.getAttributeModifiers(slotContext, uuid, stack);

        if (!isBroken(stack))
            result.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, Reference.MODID + ":" + "old_boot_movement_speed",
                    config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));

        return result;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new OldBootModel();
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 0.05F;
    }
}