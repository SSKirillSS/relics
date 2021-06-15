package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.RelicStats;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Collections;
import java.util.List;

public class LuckyHorseshoeItem extends RelicItem<LuckyHorseshoeItem.Stats> implements ICurioItem, IHasTooltip {
    public LuckyHorseshoeItem() {
        super(Rarity.RARE);
    }

    @Override
    public java.util.List<ITextComponent> getShiftTooltip() {
        java.util.List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.lucky_horseshoe.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return livingEntity.getCommandSenderWorld().getRandom().nextFloat() <= config.lootingChance ? config.additionalLooting : 0;
    }

    @Override
    public int getFortuneBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return livingEntity.getCommandSenderWorld().getRandom().nextFloat() <= config.fortuneChance ? config.additionalFortune : 0;
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.VILLAGE_BUTCHER);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    public static class Stats extends RelicStats {
        public float lootingChance = 0.1F;
        public float fortuneChance = 0.1F;
        public int additionalLooting = 3;
        public int additionalFortune = 3;
    }
}